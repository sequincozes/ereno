package br.ufu.facom.ereno.attacks.uc04.creator;

import br.ufu.facom.ereno.attacks.uc04.devices.MasqueradeFakeNormalED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * UC04: forge a normal GOOSE state while the legitimate system is in a fault
 * state. Frames inherit victim metadata and use causal, protocol-valid timing.
 */
public class MasqueradeFakeNornalCreator implements MessageCreator {
    private final String label;
    private MasqueradeFakeNormalED attacker;

    public MasqueradeFakeNornalCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {
        if (numberOfMessages <= 0) {
            return;
        }
        this.attacker = (MasqueradeFakeNormalED) ied;
        ArrayList<Goose> source = sourceMessages();
        source.sort(Comparator.comparingDouble(Goose::getTimestamp));

        ArrayList<Window> faultWindows = buildWindows(source, 1);
        if (faultWindows.isEmpty()) {
            throw new IllegalArgumentException("UC04 requires at least one legitimate fault interval.");
        }

        int attempts = 0;
        while (attacker.getNumberOfMessages() < numberOfMessages && attempts < numberOfMessages * 30 + 100) {
            Window window = faultWindows.get(IED.randomBetween(0, faultWindows.size() - 1));
            emitForgedEpisode(window, numberOfMessages);
            attempts++;
        }
        if (attacker.getNumberOfMessages() < numberOfMessages) {
            throw new IllegalStateException("UC04 could not place the requested number of forged frames inside fault windows.");
        }
    }

    private ArrayList<Goose> sourceMessages() {
        if (attacker.getVictimIED() != null) {
            return attacker.getVictimIED().copyMessages();
        }
        if (attacker.getSubstationNetwork() == null || attacker.getSubstationNetwork().stationBusMessages.isEmpty()) {
            throw new IllegalArgumentException("UC04 requires a victim IED or a non-empty SubstationNetwork.");
        }
        ArrayList<Goose> messages = new ArrayList<>();
        for (EthernetFrame frame : attacker.getSubstationNetwork().stationBusMessages) {
            if (frame instanceof Goose) {
                messages.add(((Goose) frame).copy());
            }
        }
        return messages;
    }

    private void emitForgedEpisode(Window window, int targetCount) {
        double processingDelay = Math.max(0.0, attacker.getDelayFromEvent());
        double lower = window.seed.getTimestamp() + 0.001;
        double upper = window.endExclusive - processingDelay - 0.001;
        if (upper <= lower) {
            return;
        }
        double eventTime = IED.randomBetween(lower, upper);
        if (attacker.getSeedMessage() == null) {
            attacker.setSeed(window.seed);
        }

        int forgedStNum = window.seed.getStNum() + 1;
        int sqNum = 1;
        double t = eventTime;
        double timestamp = t + processingDelay;
        double[] intervals = attacker.exponentialBackoff(
                Math.round(attacker.getMinTime()),
                attacker.getMaxTime(),
                attacker.getInitialBackoffInterval());

        for (double interval : intervals) {
            if (attacker.getNumberOfMessages() >= targetCount || timestamp >= window.endExclusive) {
                break;
            }
            Goose forged = window.seed.copy();
            forged.setLabel(label);
            forged.setCbStatus(0);
            forged.setStNum(forgedStNum);
            forged.setSqNum(sqNum++);
            forged.setT(t);
            forged.setTimestamp(timestamp);
            attacker.addMessage(forged);
            timestamp += interval;
        }
    }

    private ArrayList<Window> buildWindows(ArrayList<Goose> messages, int desiredStatus) {
        ArrayList<Window> windows = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            Goose seed = messages.get(i);
            if (seed.getCbStatus() != desiredStatus) {
                continue;
            }
            double end = Double.NaN;
            for (int j = i + 1; j < messages.size(); j++) {
                if (messages.get(j).getCbStatus() != desiredStatus) {
                    end = messages.get(j).getTimestamp();
                    break;
                }
            }
            if (Double.isNaN(end)) {
                end = messages.get(messages.size() - 1).getTimestamp()
                        + Math.max(1.0, attacker.getMaxTime() / 1000.0 * 10.0);
            }
            if (end - seed.getTimestamp() > attacker.getDelayFromEvent() + 0.003) {
                windows.add(new Window(seed.copy(), end));
            }
        }
        return windows;
    }

    private static final class Window {
        private final Goose seed;
        private final double endExclusive;

        private Window(Goose seed, double endExclusive) {
            this.seed = seed;
            this.endExclusive = endExclusive;
        }
    }
}
