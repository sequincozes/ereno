/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.creators;

import br.ufu.facom.ereno.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static br.ufu.facom.ereno.devices.IED.randomBetween;

/**
 * @author silvio
 */
public class RandomReplayCreator implements MessageCreator {
    ArrayList<Goose> gooseMessages;
    int numReplayInstances;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     * @param numReplayInstances - number of attack instances
     */
    public RandomReplayCreator(ArrayList<Goose> legitimateMessages, int numReplayInstances) {
        this.gooseMessages = legitimateMessages;
        this.numReplayInstances = numReplayInstances;
    }

    @Override
    public void generate(IED ied) {
        for (int i = 0; i < numReplayInstances; i++) {
            // Pickups one old GOOSE randomly
            Goose randomGoose = gooseMessages.get(randomBetween(0, gooseMessages.size()));

            // Refresh the message timestamp
            Goose lastLegitimateGoose = gooseMessages.get(gooseMessages.size() - 1);
            randomGoose.setTimestamp(lastLegitimateGoose.getTimestamp() + 1);

            // At the replayed message to the legitimate messages list
            gooseMessages.add(randomGoose);
        }

    }
}
