/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.useCases;

import br.ufu.facom.ereno.model.GooseMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author silvio
 */
public class UC01GooseOnly extends AbstractUseCase {


    // Replay random
//    public static void run(String filename) throws IOException {
//        outputFile = outputLocation + filename;
//        UC01GooseOnly extractor = new UC01GooseOnly();
//        extractor.startWriting();
//        extractor.generateReplayAttacksUC1(10);
//        extractor.finishWriting();
//    }

    /**
     * @param legitimateMessages - previously generated legitimate messages
     * @param numReplayInstances - number of attack instances
     * @throws FileNotFoundException
     */
    public ArrayList<GooseMessage> generateReplayAttacksUC1(ArrayList<GooseMessage> legitimateMessages, int numReplayInstances) throws IOException {

        // Generating Replay Goose Messages
        ArrayList<GooseMessage> randomReplayMessages = new ArrayList<>();

        for (int i = 0; i < numReplayInstances; i++) {
            // Pickups one old GOOSE randomly
            GooseMessage randomGoose = legitimateMessages.get(randomBetween(0, legitimateMessages.size()));

            // Refresh the message timestamp
            GooseMessage lastLegitimateGoose = legitimateMessages.get(legitimateMessages.size() - 1);
            randomGoose.setTimestamp(lastLegitimateGoose.getTimestamp() + 1);

            randomReplayMessages.add(randomGoose);
        }

        return randomReplayMessages;
    }


}
