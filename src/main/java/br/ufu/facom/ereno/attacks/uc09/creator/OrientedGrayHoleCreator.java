package br.ufu.facom.ereno.attacks.uc09.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Objects;

import static br.ufu.facom.ereno.general.IED.randomBetween;

public class OrientedGrayHoleCreator implements MessageCreator {

    enum attackApproaches {
        BURST, RANDOMIC
    }

    ArrayList<Goose> legitimateMessages;
    Integer discardRate;
    Integer toDiscardPackets;

    attackApproaches approaches;

    public OrientedGrayHoleCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
        this.discardRate = 5;
        this.toDiscardPackets = 5;
        this.approaches = attackApproaches.RANDOMIC;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {

        for (int i = 0; i < numberOfMessages; i++) {

            Goose message = legitimateMessages.get(i);
            Goose lastMessage = (i > 0) ? legitimateMessages.get(i - 1) : null;
            Goose nextMessage = (i + 1 < numberOfMessages) ? legitimateMessages.get(i + 1) : null;
            boolean toDiscard = randomBetween(0, 100) < discardRate;
            boolean toLabel = false;

            if (lastMessage != null && message.getStNum() != lastMessage.getStNum()) {
                System.out.println("[ORIENTED GRAYHOLE] STATUS HAS CHANGED " + message.getStNum() + " " + toDiscard);

                if (toDiscard) {
                    System.out.println("[ORIENTED GRAYHOLE] Discarded the message of timestamp " + message.getTimestamp() +
                            " through the " + attackApproaches.RANDOMIC.name() + " approach.");

                    if (i + toDiscardPackets < numberOfMessages) {
                        nextMessage = legitimateMessages.get(i + toDiscardPackets);
                        toLabel = true;
                    }

                    for (int j = 0; j < toDiscardPackets && (i + j) < numberOfMessages; j++) {
                        Goose messageToRemove = legitimateMessages.get(i + j);
                        ied.removeMessage(messageToRemove);
                        System.out.println("[ORIENTED GRAYHOLE] Removed message with timestamp " + messageToRemove.getTimestamp());
                    }

                    i += toDiscardPackets - 1;
                } else {
                    ied.addMessage(message);
                    System.out.println("[ORIENTED GRAYHOLE] Avoided discarding the message of timestamp " + message.getTimestamp());
                }

                if (toLabel && nextMessage != null) {
                    nextMessage.setLabel(GSVDatasetWriter.label[9]);
                    System.out.println("[ORIENTED GRAYHOLE] Labeled next message with timestamp: " + nextMessage.getTimestamp());
                }

            } else {
                ied.addMessage(message);
            }
        }
    }


}