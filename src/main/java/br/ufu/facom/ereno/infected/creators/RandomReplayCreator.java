/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.infected.creators;

import br.ufu.facom.ereno.benign.devices.IED;
import br.ufu.facom.ereno.benign.messages.Goose;
import br.ufu.facom.ereno.benign.creator.MessageCreator;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.devices.IED.randomBetween;

/**
 * @author silvio
 */
public class RandomReplayCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;
    int numReplayInstances;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     * @param numReplayInstances - number of attack instances
     */
    public RandomReplayCreator(ArrayList<Goose> legitimateMessages, int numReplayInstances) {
        this.legitimateMessages = legitimateMessages;
        this.numReplayInstances = numReplayInstances;
    }

    @Override
    public void generate(IED ied) {
        for (int i = 0; i < numReplayInstances; i++) {
            // Pickups one old GOOSE randomly
            Goose randomGoose = legitimateMessages.get(randomBetween(0, legitimateMessages.size()));

            // Refresh the message timestamp
            Goose lastLegitimateGoose = legitimateMessages.get(legitimateMessages.size() - 1);
            randomGoose.setTimestamp(lastLegitimateGoose.getTimestamp() + 1);

            // Send back the random replayed message to ReplayerIED
            ied.addMessage(randomGoose);

        }

    }
}
