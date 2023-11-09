/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.utils.Util;
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
        this.count = numberOfPeriodicMessages + 1; // this additional message will be used as reference for consistency features and then discarded

        for (int i = 0; i <= numberOfPeriodicMessages; i++) {
            Goose periodicGoose;
            try {
                // Already exists some messages
                Goose previousGoose = protectionIED.getMessages().get(protectionIED.getMessages().size() - 1);
                periodicGoose = new Goose(
                        previousGoose.getCbStatus(),
                        previousGoose.getStNum(),
                        previousGoose.getSqNum() + 1,
                        previousGoose.getTimestamp() + 1,
                        previousGoose.getT(),
                        this.label);

            } catch (IndexOutOfBoundsException e) {
                // This will be the first message, for referene only (it will not be included into the dataset)
                // @TODO Refactore all UCs to remove this. It should be handled at the feature generation process rather than in message generation.
                periodicGoose = new Goose(
                        protectionIED.toInt(protectionIED.isInitialCbStatus()),
                        protectionIED.getInitialStNum(),
                        protectionIED.getInitialSqNum(),
                        protectionIED.getFirstGooseTime() - 1, // simulates a previous message timestamp
                        protectionIED.getFirstGooseTime(),
                        this.label);
            }

            protectionIED.addMessage(periodicGoose);
            protectionIED.setInitialSqNum(protectionIED.getInitialSqNum() + 1);
        }
    }

    public void reportEventAt(double eventTimestamp) {
        removeMessagesAfterEvent(eventTimestamp); // cancel programmed messages to replace them by a bursting

        if (Util.Debug.gooseMessages) {
            Logger.getLogger("GooseCreator").log(Level.INFO, "Reporting an event at " + eventTimestamp + "!");
        }

        System.out.println("protectionIED.getMessages().size() - 1: "+ (protectionIED.getMessages().size() - 1));
        protectionIED.setFirstGooseTime(
                protectionIED.getMessages().get(protectionIED.getMessages().size() - 1).getTimestamp()
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
    }

    public void removeMessagesAfterEvent(double eventTimestamp) {
        if (protectionIED.getMessages().size() > 1) {
            double lastTimestamp = protectionIED.getMessages().get(protectionIED.getMessages().size() - 1).getTimestamp();
            if (lastTimestamp > eventTimestamp) {
                // Remove messages after the repported event
                protectionIED.getMessages().remove(protectionIED.getMessages().size() - 1);
                removeMessagesAfterEvent(eventTimestamp);
            }
        } else {
            Logger.getLogger("NoGooseMessages").warning("There are no GOOSE messages.");
        }
    }


}
