/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author silvio
 */
public class GooseCreator implements MessageCreator {
    int count;
    private ProtectionIED protectionIED;
    private String label;

    public GooseCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int numberOfPeriodicMessages) {
        this.protectionIED = (ProtectionIED) ied;
        this.count = numberOfPeriodicMessages;
        Goose previousGoose = null;
        Goose periodicGoose;

        for (int i = 0; i <= numberOfPeriodicMessages; i++) {
            if (previousGoose != null) {
                // Already exists some messages
                double networkDelay;
                periodicGoose = new Goose(
                        previousGoose.getCbStatus(),
                        previousGoose.getStNum(),
                        previousGoose.getSqNum() + 1,
                        previousGoose.getTimestamp() + protectionIED.getMaxTime()/1000 + getNetworkDelay(),
                        previousGoose.getT(),
                        this.label);
                protectionIED.addMessage(periodicGoose);
                previousGoose = periodicGoose.copy();
//                System.out.println(i + " - GOOSE sq(" + periodicGoose.getSqNum() + "/st" + periodicGoose.getStNum() + ") em " + periodicGoose.getTimestamp() + "(T: " + periodicGoose.getT() + ")");

            } else if (protectionIED.getNumberOfMessages() > 0) {
                previousGoose = protectionIED.copyMessages().get(protectionIED.getNumberOfMessages() - 1).copy();
            } else {
                previousGoose = new Goose(
                        protectionIED.toInt(protectionIED.isInitialCbStatus()),
                        protectionIED.getInitialStNum(),
                        protectionIED.getInitialSqNum() - 1,
                        protectionIED.getFirstGooseTime() + protectionIED.getInitialTimestamp() - 1, // simulates a previous message timestamp
                        protectionIED.getFirstGooseTime(),
                        this.label);
                Logger.getLogger("GooseCreator").info("Skipping pseudo-GOOSE");
            }
        }
    }

    private double getNetworkDelay() {
        return IED.randomBetween(0.001,0.031);
    }

    public void reportEventAt(double eventTimestamp) {
//        System.out.println("WILL REPORT EVENT");
        // I think this will not be more necessary because I'm removing additional messages manually
//        removeMessagesAfterEvent(eventTimestamp); // cancel programmed messages to replace them by a bursting

        if (GSVDatasetWritter.Debug.gooseMessages) {
            Logger.getLogger("GooseCreator").log(Level.INFO, "Reporting an event at " + eventTimestamp + "!");
        }

//        System.out.println("protectionIED.getMessages().size() - 1: " + (protectionIED.getMessages().size() - 1));
        protectionIED.setFirstGooseTime(
                protectionIED.copyMessages().get(protectionIED.copyMessages().size() - 1).getTimestamp()
        );

        // Status change
        protectionIED.setInitialCbStatus(!protectionIED.isInitialCbStatus());
        protectionIED.setInitialStNum(protectionIED.getInitialStNum() + 1);

        int sqNum = 1;
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
