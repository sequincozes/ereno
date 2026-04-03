package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generator of GOOSE messages for both normal operation and fault scenarios.
 *
 * Implements IEC 61850 GOOSE message creation with:
 * - Periodic normal state messages
 * - Event-driven fault reporting
 * - Automatic recovery sequences
 * - Exponential backoff bursting during events
 *
 * Handles complete message lifecycle including sequence numbering,
 * timestamp synchronization, and status change propagation. Supports
 * both steady-state operation and transient fault scenarios with
 * configurable timing parameters.
 *
 * @see br.ufu.facom.ereno.messages.Goose
 * @see br.ufu.facom.ereno.api.GooseFlow
 * @see br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation
 */


public class GooseCreator implements MessageCreator {
    int count;
    private ProtectionIED protectionIED;
    private Goose previousGoose;
    private String label;
    private Goose seedMessage;

    public GooseCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int normalMessages) {
        this.protectionIED = (ProtectionIED) ied;
        this.count = normalMessages;
        int faultProbability = 5;

        generateSeedGoose();

        int barWidth = 50; // Width of the progress bar

        while (((ProtectionIED) ied).getMessages().size() < normalMessages) {
            int currentSize = ((ProtectionIED) ied).getMessages().size();
            double progress = (double) currentSize / normalMessages;

            System.out.print("\r[");
            int pos = (int) (barWidth * progress);
            for (int i = 0; i < barWidth; i++) {
                if (i < pos) System.out.print("=");
                else if (i == pos) System.out.print(">");
                else System.out.print(" ");
            }
            System.out.print("] " + (int)(progress * 100) + "%");

            int randomPercentage = IED.randomBetween(1, 100);
            if (currentSize > 0) {
                if (randomPercentage > faultProbability) {
                    generateNormalGoose();
                } else {
                    generateFaultAndRecovery();
                }
            } else {
                generateNormalGoose();
            }
        }

        System.out.println("\r[" + "=".repeat(barWidth) + "] 100%");
    }

    private void generateFaultAndRecovery() {
        double lastPeriodicMessage = protectionIED.getMessages().get(protectionIED.getNumberOfMessages() - 1).getTimestamp();
        reportEventAt(((int) lastPeriodicMessage) + 0.5);
//        Logger.getLogger("ProtectionIED.run()").info("Reporting fault at " + lastPeriodicMessage + 0.5);
        protectionIED.getMessages().remove(protectionIED.getNumberOfMessages() - 1);
        reportEventAt(((int) lastPeriodicMessage) + 0.6);
//        Logger.getLogger("ProtectionIED.run()").info("Reporting normal operation at " + (((int) lastPeriodicMessage) + 0.6));
    }

    private void generateNormalGoose() {
        if (previousGoose != null) {
            Goose periodicGoose = new Goose(
                    previousGoose.getCbStatus(),
                    previousGoose.getStNum(),
                    previousGoose.getSqNum() + 1,
                    previousGoose.getTimestamp() + protectionIED.getMaxTime() / 1000 + getNetworkDelay(),
                    previousGoose.getT(),
                    this.label);
            protectionIED.addMessage(periodicGoose);
            previousGoose = periodicGoose.copy();
        } else {
            throw new IllegalArgumentException("There must be a pseudo message. Please call generateSeedGoose() before.");
        }
    }

    private void generateSeedGoose() {
        System.out.println("protectionIED.isInitialCbStatus(): " + protectionIED.isInitialCbStatus());
        previousGoose = new Goose(
                protectionIED.toInt(protectionIED.isInitialCbStatus()),
                protectionIED.getInitialStNum(),
                protectionIED.getInitialSqNum() - 1,
                protectionIED.getFirstGooseTime() + protectionIED.getInitialTimestamp() - 1,
                protectionIED.getFirstGooseTime(),
                this.label);
        seedMessage = previousGoose.copy();
    }

    private double getNetworkDelay() {
        return IED.randomBetween(0.001, 0.031);
    }

    public void reportEventAt(double eventTimestamp) {
        if (GSVDatasetWriter.Debug.gooseMessages) {
            Logger.getLogger("GooseCreator").log(Level.INFO, "Reporting an event at " + eventTimestamp + "!");
        }

        protectionIED.setFirstGooseTime(
                protectionIED.copyMessages().get(protectionIED.copyMessages().size() - 1).getTimestamp()
        );

        protectionIED.setInitialCbStatus(!protectionIED.isInitialCbStatus());
        protectionIED.setInitialStNum(protectionIED.getInitialStNum() + 1);

        int sqNum = 1;
        double t = eventTimestamp + protectionIED.getDelayFromEvent();
        double timestamp = t;

        double[] burstingIntervals = protectionIED.exponentialBackoff(
                (long) protectionIED.getMinTime(),
                protectionIED.getMaxTime(),
                protectionIED.getInitialBackoffInterval());

        for (double interval : burstingIntervals) {
            Goose gm = new Goose(
                    protectionIED.toInt(protectionIED.isInitialCbStatus()),
                    protectionIED.getInitialStNum(),
                    sqNum++,
                    timestamp,
                    t,
                    label
            );

            timestamp = timestamp + interval;
            protectionIED.addMessage(gm);
            previousGoose = gm;
        }
    }

    public void removeMessagesAfterEvent(double eventTimestamp) {
        if (protectionIED.copyMessages().size() > 1) {
            double lastTimestamp = protectionIED.copyMessages().get(protectionIED.copyMessages().size() - 1).getTimestamp();
            if (lastTimestamp > eventTimestamp) {
                protectionIED.getMessages().remove(protectionIED.copyMessages().size() - 1);
                removeMessagesAfterEvent(eventTimestamp);
            }
        } else {
            Logger.getLogger("NoGooseMessages").warning("There are no GOOSE messages.");
        }
    }

    public Goose getSeedMessage() {
        return this.seedMessage;
    }
}
