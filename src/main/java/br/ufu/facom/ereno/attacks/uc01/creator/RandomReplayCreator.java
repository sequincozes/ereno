/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc01.creator;

import br.ufu.facom.ereno.utils.DatasetWritter;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

/**
 * @author silvio
 */
public class RandomReplayCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;
    private final int timeTakenByAttacker = 1;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     */
    public RandomReplayCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    /**
     * @param ied - IED that will receive the generated messages
     * @param numReplayInstances - number of attack instances
     */
    @Override
    public void generate(IED ied, int numReplayInstances) {

        for (int i = 0; i < numReplayInstances; i++) {
            // Pickups one old GOOSE randomly
            Goose randomGoose = legitimateMessages.get(randomBetween(0, legitimateMessages.size()));
            randomGoose.label = DatasetWritter.label[1]; // label it as random replay (uc01)

            // Refresh the message timestamp
            Goose lastLegitimateGoose = legitimateMessages.get(legitimateMessages.size() - 1);
            randomGoose.setTimestamp(lastLegitimateGoose.getTimestamp() + timeTakenByAttacker);

            // Send back the random replayed message to ReplayerIED
            ied.addMessage(randomGoose);

        }

    }
}
