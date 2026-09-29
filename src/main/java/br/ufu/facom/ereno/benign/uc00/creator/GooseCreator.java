package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.logging.Level;
import java.util.logging.Logger;

/** Generates causally ordered legitimate GOOSE traffic. */
public class GooseCreator implements MessageCreator {
    private final String label;
    private ProtectionIED protectionIED;
    private Goose previousGoose;
    private Goose seedMessage;

    public GooseCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int normalMessages) {
        if (normalMessages <= 0) {
            return;
        }
        this.protectionIED = (ProtectionIED) ied;
        generateSeedGoose();

        final int faultProbabilityPercent = 5;
        boolean generatedFault = false;
        while (protectionIED.getMessages().size() < normalMessages) {
            int percentage = IED.randomBetween(1, 100);
            boolean forceOneFault = !generatedFault
                    && normalMessages >= 6
                    && protectionIED.getMessages().size() >= Math.max(1, normalMessages / 3);
            if ((forceOneFault || percentage <= faultProbabilityPercent) && roomForEventBurst(normalMessages)) {
                generateFaultAndRecovery(normalMessages);
                generatedFault = true;
            } else {
                generateNormalGoose();
            }
        }
    }

    private boolean roomForEventBurst(int targetCount) {
        // At minimum, leave room for one fault frame and one recovery frame.
        return targetCount - protectionIED.getMessages().size() >= 2;
    }

    private void generateFaultAndRecovery(int targetCount) {
        double lastTimestamp = previousGoose.getTimestamp();

        // Always schedule events in the future. Older code used floor(last)+0.5,
        // which could move time backwards when the last frame was after .5 s.
        double faultEventTime = lastTimestamp + 0.5;
        emitStateChange(true, faultEventTime, targetCount);

        if (protectionIED.getMessages().size() < targetCount) {
            double recoveryEventTime = Math.max(faultEventTime + 0.1, previousGoose.getTimestamp() + 0.001);
            emitStateChange(false, recoveryEventTime, targetCount);
        }
    }

    private void generateNormalGoose() {
        if (previousGoose == null) {
            throw new IllegalStateException("Seed GOOSE must be generated first.");
        }
        double periodSeconds = protectionIED.getMaxTime() / 1000.0;
        Goose periodicGoose = previousGoose.copy();
        periodicGoose.setLabel(label);
        periodicGoose.setSqNum(previousGoose.getSqNum() + 1);
        periodicGoose.setTimestamp(previousGoose.getTimestamp() + periodSeconds + getNetworkDelay());
        protectionIED.addMessage(periodicGoose);
        previousGoose = periodicGoose;
    }

    private void generateSeedGoose() {
        double seedTimestamp = protectionIED.getFirstGooseTime() + protectionIED.getInitialTimestamp() - 1.0;
        previousGoose = new Goose(
                protectionIED.toInt(protectionIED.isInitialCbStatus()),
                protectionIED.getInitialStNum(),
                Math.max(0, protectionIED.getInitialSqNum() - 1),
                seedTimestamp,
                protectionIED.getFirstGooseTime(),
                label);
        seedMessage = previousGoose.copy();
    }

    private double getNetworkDelay() {
        return IED.randomBetween(0.001, 0.031);
    }

    /** Emits a legitimate GOOSE state-change burst, truncated at targetCount. */
    private void emitStateChange(boolean newStatus, double eventTimestamp, int targetCount) {
        if (eventTimestamp <= previousGoose.getTimestamp()) {
            eventTimestamp = Math.nextUp(previousGoose.getTimestamp());
        }

        protectionIED.setInitialCbStatus(newStatus);
        protectionIED.setInitialStNum(previousGoose.getStNum() + 1);

        int sqNum = 1;
        double t = eventTimestamp;
        double timestamp = t + protectionIED.getDelayFromEvent();

        double[] intervals = protectionIED.exponentialBackoff(
                Math.round(protectionIED.getMinTime()),
                protectionIED.getMaxTime(),
                protectionIED.getInitialBackoffInterval());

        for (double interval : intervals) {
            if (protectionIED.getMessages().size() >= targetCount) {
                break;
            }
            Goose gm = previousGoose.copy();
            gm.setLabel(label);
            gm.setCbStatus(protectionIED.toInt(newStatus));
            gm.setStNum(protectionIED.getInitialStNum());
            gm.setSqNum(sqNum++);
            gm.setT(t);
            gm.setTimestamp(timestamp);
            protectionIED.addMessage(gm);
            previousGoose = gm;
            timestamp += interval;
        }
    }

    /** Backward-compatible public event API used by some scripts. */
    public void reportEventAt(double eventTimestamp) {
        if (protectionIED == null || previousGoose == null) {
            throw new IllegalStateException("generate() must initialize the creator before reportEventAt().");
        }
        boolean newStatus = !protectionIED.isInitialCbStatus();
        emitStateChange(newStatus, eventTimestamp, Integer.MAX_VALUE);
    }

    public void removeMessagesAfterEvent(double eventTimestamp) {
        while (!protectionIED.getMessages().isEmpty()
                && protectionIED.getMessages().get(protectionIED.getMessages().size() - 1).getTimestamp() > eventTimestamp) {
            protectionIED.getMessages().remove(protectionIED.getMessages().size() - 1);
        }
        if (!protectionIED.getMessages().isEmpty()) {
            previousGoose = protectionIED.getMessages().get(protectionIED.getMessages().size() - 1);
        }
    }

    public Goose getSeedMessage() {
        return seedMessage == null ? null : seedMessage.copy();
    }
}
