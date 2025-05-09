/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc08.creator;

import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

/**
 * @author silvio
 */
public class GrayHoleVictimCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;

    public GrayHoleVictimCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    int rate = 30;

    @Override
    public void generate(IED ied, int numberOfMessages) {

        for (int i = 0; i < numberOfMessages; i++) {

            Goose message = legitimateMessages.get(i);
            Goose nextMessage = (i + 1 < numberOfMessages) ? legitimateMessages.get(i + 1) : null;
            boolean toDiscard = randomBetween(0, 100) < rate;
            boolean toLabel = false;

            if (toDiscard) {

                if (i + 1 < numberOfMessages) {
                    nextMessage = legitimateMessages.get(i + 1);
                    toLabel = true;
                }
                ied.removeMessage(message);
                System.out.println("[GRAYHOLE] Discarded the message of timestamp " + message.getTimestamp());
            } else {
                ied.addMessage(message);
            }
            if (toLabel && nextMessage != null) {
                nextMessage.setLabel(GSVDatasetWriter.label[8]);
                System.out.println("[GRAYHOLE] Labeled message with timestamp: " + nextMessage.getTimestamp());
            }
        }
    }
}