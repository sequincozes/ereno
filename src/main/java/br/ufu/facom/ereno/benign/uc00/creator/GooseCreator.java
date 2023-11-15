/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author silvio
 */
public class GooseCreator implements MessageCreator {
    int count;
    private ProtectionIED protectionIED;
    private Goose previousGoose;
    private String label;

    public GooseCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int normalMessages) {
        this.protectionIED = (ProtectionIED) ied;
        this.count = normalMessages;

        int faultprobability = 5;

        // Generating the default seed message (it will not be written to dataset)
        generateSeedGoose();

        // Generate faults and normal randomly
        while (((ProtectionIED) ied).getMessages().size() < normalMessages) {
            int percentage = ied.randomBetween(1, 100); // decide whether it will be fault or normal
            if (percentage > faultprobability) {
                generateNormalGoose();
            } else { // Generates fault and recovery events
                generateFaultAndRecovery();
            }
        }
    }

    private void generateFaultAndRecovery() {
        double lastPeriodicMessage = protectionIED.getMessages().get(protectionIED.getNumberOfMessages() - 1).getTimestamp();
        reportEventAt(lastPeriodicMessage + 0.5); // fault at middle of the second
        System.out.println("Reportou uma falta em " + lastPeriodicMessage + 0.5);
        Logger.getLogger("ProtectionIED.run()").info("Reporting fault at " + lastPeriodicMessage + 0.5);
        protectionIED.getMessages().remove(protectionIED.getNumberOfMessages() - 1); // need to remove the message after 100ms
        reportEventAt(lastPeriodicMessage + 0.6); // fault recovery 100ms later
        System.out.println("Reportou uma reestabelecimento em " + lastPeriodicMessage + 0.5);
        Logger.getLogger("ProtectionIED.run()").info("Reporting normal operation at " + lastPeriodicMessage + 0.5);
    }

    private void generateNormalGoose() {
        if (previousGoose != null) {
            // Already exists some messages
            Goose periodicGoose = new Goose(
                    previousGoose.getCbStatus(),
                    previousGoose.getStNum(),
                    previousGoose.getSqNum() + 1,
                    previousGoose.getTimestamp() + protectionIED.getMaxTime() / 1000 + getNetworkDelay(),
                    previousGoose.getT(),
                    this.label);
            protectionIED.addMessage(periodicGoose);
            previousGoose = periodicGoose.copy();
//                System.out.println(i + " - GOOSE sq(" + periodicGoose.getSqNum() + "/st" + periodicGoose.getStNum() + ") em " + periodicGoose.getTimestamp() + "(T: " + periodicGoose.getT() + ")");

        } else if (protectionIED.getNumberOfMessages() > 0) {
            previousGoose = protectionIED.copyMessages().get(protectionIED.getNumberOfMessages() - 1).copy();
        } else {
            throw new IllegalArgumentException("There must be a pseudo message. Please call generateSeedGoose() before.");
        }
        System.out.println("Initial CBStatus: " + protectionIED.isInitialCbStatus());
    }

    private void generateSeedGoose() {
        System.out.println("protectionIED.isInitialCbStatus(): "+protectionIED.isInitialCbStatus());
        previousGoose = new Goose(
                protectionIED.toInt(protectionIED.isInitialCbStatus()),
                protectionIED.getInitialStNum(),
                protectionIED.getInitialSqNum() - 1,
                protectionIED.getFirstGooseTime() + protectionIED.getInitialTimestamp() - 1, // simulates a previous message timestamp
                protectionIED.getFirstGooseTime(),
                this.label);
        System.out.println("Seed=> StNum: " + previousGoose.getStNum() + " / " + previousGoose.getCbStatus());
        System.exit(0);
    }

    private double getNetworkDelay() {
        return IED.randomBetween(0.001, 0.031);
    }

    public void reportEventAt(double eventTimestamp) {
        if (GSVDatasetWritter.Debug.gooseMessages) {
            Logger.getLogger("GooseCreator").log(Level.INFO, "Reporting an event at " + eventTimestamp + "!");
        }

        protectionIED.setFirstGooseTime(
                protectionIED.copyMessages().get(protectionIED.copyMessages().size() - 1).getTimestamp()
        );

        // Status change
        protectionIED.setInitialCbStatus(!protectionIED.isInitialCbStatus());
        protectionIED.setInitialStNum(protectionIED.getInitialStNum() + 1);

        int sqNum = 0;
        double t = eventTimestamp + protectionIED.getDelayFromEvent(); // new t
        double timestamp = t; // timestamp

        double[] burstingIntervals = protectionIED.exponentialBackoff(
                (long) protectionIED.getMinTime(),
                protectionIED.getMaxTime(),
                protectionIED.getInitialBackoffInterval());

        for (double interval : burstingIntervals) { // GOOSE BURST MODE
            Goose gm = new Goose(
                    protectionIED.toInt(protectionIED.isInitialCbStatus()), // current status
                    protectionIED.getInitialStNum(), // same stNum
                    sqNum++, // increase sqNum
                    timestamp, // current timestamp
                    t, // timestamp of last st change
                    label
            );

            timestamp = timestamp + interval;
            protectionIED.addMessage(gm);
            previousGoose = gm;
        }

//        System.out.println("ALREADY REPORTED");
    }

    public void removeMessagesAfterEvent(double eventTimestamp) {
        if (protectionIED.copyMessages().size() > 1) {
            double lastTimestamp = protectionIED.copyMessages().get(protectionIED.copyMessages().size() - 1).getTimestamp();
            if (lastTimestamp > eventTimestamp) {
                // Remove messages after the repported event
                protectionIED.getMessages().remove(protectionIED.copyMessages().size() - 1);
                removeMessagesAfterEvent(eventTimestamp);
            }
        } else {
            Logger.getLogger("NoGooseMessages").warning("There are no GOOSE messages.");
        }
    }

}
