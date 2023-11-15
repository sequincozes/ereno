/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc01.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;

import java.util.ArrayList;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

/**
 * @author silvio
 */
public class RandomReplayCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;
    private float timeTakenByAttacker = 1;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     */
    public RandomReplayCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    /**
     * @param ied                - IED that will receive the generated messages
     * @param numReplayInstances - number of attack instances
     */
    @Override
    public void generate(IED ied, int numReplayInstances) {

        for (int i = 0; i < numReplayInstances; i++) {
            // Pickups one old GOOSE randomly
            int randomIndex = randomBetween(0, legitimateMessages.size() - 1);
            Goose randomGoose = legitimateMessages.get(randomIndex).copy();
            Logger.getLogger("RandomReplayCreator").info("Captured the legitimate message at " + randomGoose.getTimestamp());
            randomGoose.label = GSVDatasetWritter.label[1]; // label it as random replay (uc01)

            // Refresh the message timestamp
//            Goose lastLegitimateGoose = legitimateMessages.get(legitimateMessages.size() - 1);

            // Randomize the time taken by an attacker
            timeTakenByAttacker = (float) (randomBetween(100F, 100000F) / 1000);

            // Other strategy: assumes the random replay was close to the original one:
            randomGoose.setTimestamp(randomGoose.getTimestamp() + timeTakenByAttacker);
            Logger.getLogger("RandomReplayCreator").info("Sent the replay message at " + randomGoose.getTimestamp() + "(time taken by attaker: " + timeTakenByAttacker + ")");

            // Send back the random replayed message to ReplayerIED
            ied.addMessage(randomGoose.copy());
        }

    }
}
