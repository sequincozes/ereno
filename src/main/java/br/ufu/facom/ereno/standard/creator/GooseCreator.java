/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.standard.creator;

import br.ufu.facom.ereno.standard.devices.IED;
import br.ufu.facom.ereno.standard.devices.ProtectionIED;
import br.ufu.facom.ereno.standard.messages.Goose;

import java.util.ArrayList;

/**
 * @author silvio
 */
public class GooseCreator implements MessageCreator {
    int count;
    private ProtectionIED protectionIED;

    public GooseCreator(int numberOfPeriodicMessages) {
        this.count = numberOfPeriodicMessages;
    }

    @Override
    public void generate(IED ied) {
        this.protectionIED = (ProtectionIED) ied;

        boolean cbStatus = protectionIED.isInitialCbStatus();
        if (protectionIED.getMessages() == null) {
            protectionIED.setMessages(new ArrayList<>());
        }

        for (int i = 0; i <= protectionIED.getNumberOfMessages(); i++) {
            Goose periodicGoose;
            try {
                // Already exists some messages
                Goose previousGoose = protectionIED.getMessages().get(protectionIED.getMessages().size() - 1);
                periodicGoose = new Goose(previousGoose.getCbStatus(), previousGoose.getStNum(), previousGoose.getSqNum() + 1,
                        previousGoose.getTimestamp() + 1, previousGoose.getT());
            } catch (IndexOutOfBoundsException e) {
                // These will be the first messages
                periodicGoose = new Goose(protectionIED.toInt(cbStatus),
                        protectionIED.getInitialStNum(),
                        protectionIED.getInitialSqNum(),
                        protectionIED.getFirstGooseTime(),
                        protectionIED.getFirstGooseTime());
            }
            protectionIED.addMessage(periodicGoose);
            protectionIED.setInitialSqNum(protectionIED.getInitialSqNum() + 1);

        }


    }

    public void reportEventAt(double eventTimestamp) {
        removeMessagesBeforeThan(eventTimestamp);
        protectionIED.setFirstGooseTime(
                protectionIED.getMessages().get(protectionIED.getMessages().size() - 1).getTimestamp()
        );

        // Status change
        protectionIED.setInitialCbStatus(!protectionIED.isInitialCbStatus());
        protectionIED.setInitialStNum(protectionIED.getInitialStNum() + 1);

        int sqNum = 1;
        double t = eventTimestamp + protectionIED.getDelayFromEvent(); // new t
        double timestamp = t; // timestamp
        System.out.println(t);

        for (double interval : protectionIED.getBurstingInterval()) { // GOOSE BURST MODE
            Goose gm = new Goose(
                    protectionIED.toInt(protectionIED.isInitialCbStatus()), // current status
                    protectionIED.getInitialStNum(), // same stNum
                    sqNum++, // increase sqNum
                    timestamp, // current timestamp
                    t // timestamp of last st change
            );

            timestamp = timestamp + interval;
            protectionIED.addMessage(gm);
        }
    }

    public void removeMessagesBeforeThan(double timestamp) {
        double lastTimestamp = protectionIED.getMessages().get(protectionIED.getMessages().size() - 1).getTimestamp();
        if (lastTimestamp > timestamp) {
            protectionIED.getMessages().remove(protectionIED.getMessages().size() - 1);
            removeMessagesBeforeThan(timestamp);
        }
    }

}
