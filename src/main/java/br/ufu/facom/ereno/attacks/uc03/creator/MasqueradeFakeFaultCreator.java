package br.ufu.facom.ereno.attacks.uc03.creator;

import br.ufu.facom.ereno.attacks.uc03.devices.MasqueradeFakeFaultIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * UC03: forge a fault indication while the legitimate victim is in normal state.
 * The forged frames reuse the victim's network/protocol metadata and follow the
 * same GOOSE burst timing model, so the class is not exposed by generator-only
 * identifiers or malformed fields.
 */
public class MasqueradeFakeFaultCreator implements MessageCreator {
    private final String label;
    private MasqueradeFakeFaultIED attacker;
    private Goose seedMessage;

    public MasqueradeFakeFaultCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {
        if (numberOfMessages <= 0) {
            return;
        }
        this.attacker = (MasqueradeFakeFaultIED) ied;
        ArrayList<Goose> victim = attacker.getVictimIED().copyMessages();
        victim.sort(Comparator.comparingDouble(Goose::getTimestamp));

        ArrayList<Window> normalWindows = buildWindows(victim, 0);
        if (normalWindows.isEmpty()) {
            throw new IllegalArgumentException("UC03 requires at least one legitimate normal-operation interval.");
        }

        int attempts = 0;
        while (attacker.getNumberOfMessages() < numberOfMessages && attempts < numberOfMessages * 20 + 100) {
            Window window = normalWindows.get(IED.randomBetween(0, normalWindows.size() - 1));
            emitForgedEpisode(window, numberOfMessages);
            attempts++;
        }

        if (attacker.getNumberOfMessages() < numberOfMessages) {
            throw new IllegalStateException("UC03 could not place the requested number of forged frames inside normal-operation windows.");
        }
    }

    private void emitForgedEpisode(Window window, int targetCount) {
        double processingDelay = Math.max(0.0, attacker.getDelayFromEvent());
        double lower = window.seed.getTimestamp() + 0.001;
        double upper = window.endExclusive - processingDelay - 0.001;
        if (upper <= lower) {
            return;
        }

        double eventTime = IED.randomBetween(lower, upper);
        if (seedMessage == null) {
            seedMessage = window.seed.copy();
            seedMessage.setLabel(label);
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
            forged.setCbStatus(1);
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
                // The victim remains normal after the captured trace. Give the
                // attacker enough room to continue coherent retransmissions.
                end = messages.get(messages.size() - 1).getTimestamp()
                        + Math.max(1.0, attacker.getMaxTime() / 1000.0 * 10.0);
            }
            if (end - seed.getTimestamp() > attacker.getDelayFromEvent() + 0.003) {
                windows.add(new Window(seed.copy(), end));
            }
        }
        return windows;
    }

    public Goose getSeedMessage() {
        return seedMessage == null ? null : seedMessage.copy();
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
