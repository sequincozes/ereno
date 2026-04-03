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
        RANDOMIC_MESSAGE, RANDOMIC_BURST, DETERMINISTIC_BURST
    }

    ArrayList<Goose> legitimateMessages;
    Integer discardRate;
    Integer toDiscardPackets;

    attackApproaches approaches;

    public OrientedGrayHoleCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
        this.discardRate = 15;
        this.toDiscardPackets = 5;
        this.approaches = attackApproaches.RANDOMIC_MESSAGE  ;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {

        for (int i = 0; i < numberOfMessages; i++) {

            Goose message = legitimateMessages.get(i);
            Goose lastMessage = (i > 0) ? legitimateMessages.get(i - 1) : null;
            Goose nextMessage = (i + 1 < numberOfMessages) ? legitimateMessages.get(i + 1) : null;
            boolean toLabel = false;

            if (lastMessage != null && message.getStNum() != lastMessage.getStNum()) {
                System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] STATUS HAS CHANGED " + message.getStNum());
                if (Objects.requireNonNull(approaches) == attackApproaches.RANDOMIC_BURST) {
                    if (randomBetween(0, 100) < discardRate) {
                        System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Discarded the message of timestamp " + message.getTimestamp() + " through the " + approaches.name() + " approach.    ");

                        if (i + toDiscardPackets < numberOfMessages) {
                            nextMessage = legitimateMessages.get(i + toDiscardPackets);
                            toLabel = true;
                        }

                        for (int j = 0; j < toDiscardPackets && (i + j) < numberOfMessages; j++) {
                            Goose messageToRemove = legitimateMessages.get(i + j);
                            ied.removeMessage(messageToRemove);
                            System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Removed message with timestamp " + messageToRemove.getTimestamp());
                        }

                        i += toDiscardPackets - 1;
                    } else {
                        ied.addMessage(message);
                        System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Avoided discarding the message of timestamp " + message.getTimestamp());
                    }
                    if (toLabel && nextMessage != null) {
                        nextMessage.setLabel(approaches.name() + "_" + (GSVDatasetWriter.label[9]).toUpperCase());
                        System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Labeled next message with timestamp: " + nextMessage.getTimestamp());
                    }}
                else if (Objects.requireNonNull(approaches) == attackApproaches.RANDOMIC_MESSAGE) {
                    System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Discarding messages starting from timestamp " + message.getTimestamp() + " through the " + approaches.name() + " approach.");

                    // Iterate through the next `toDiscardPackets` messages and decide for each one individually
                    for (int j = 0; j < toDiscardPackets && (i + j) < numberOfMessages; j++) {
                        Goose messageToEvaluate = legitimateMessages.get(i + j);

                        if (randomBetween(0, 100) < discardRate) {
                            // Discard the message if the condition is met
                            ied.removeMessage(messageToEvaluate);
                            System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Removed message with timestamp " + messageToEvaluate.getTimestamp());

                            // Label the very next message (if there is one)
                            if ((i + j + 1) < numberOfMessages) {
                                Goose nextMessageLabel = legitimateMessages.get(i + j + 1);
                                nextMessageLabel.setLabel(approaches.name() + "_" + (GSVDatasetWriter.label[9]).toUpperCase());
                                System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Labeled next message with timestamp: " + nextMessageLabel.getTimestamp());
                            }
                        } else {
                            ied.addMessage(messageToEvaluate);
                            System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Avoided discarding the message of timestamp " + messageToEvaluate.getTimestamp());
                        }
                    }

                    i += toDiscardPackets - 1;
                }
                else if (Objects.requireNonNull(approaches) == attackApproaches.DETERMINISTIC_BURST) {
                    System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Discarded the message of timestamp " + message.getTimestamp() + " through the " + approaches.name() + " approach.    ");

                    if (i + toDiscardPackets < numberOfMessages) {
                        nextMessage = legitimateMessages.get(i + toDiscardPackets);
                        toLabel = true;
                    }

                    for (int j = 0; j < toDiscardPackets && (i + j) < numberOfMessages; j++) {
                        Goose messageToRemove = legitimateMessages.get(i + j);
                        ied.removeMessage(messageToRemove);
                        System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Removed message with timestamp " + messageToRemove.getTimestamp());
                    }

                    i += toDiscardPackets - 1;
                    if (toLabel && nextMessage != null) {
                        nextMessage.setLabel(approaches.name() + "_" + (GSVDatasetWriter.label[9]).toUpperCase());
                        System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Labeled next message with timestamp: " + nextMessage.getTimestamp());
                    }
                }
                else {
                    ied.addMessage(message);
                    System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] Avoided discarding the message of timestamp " + message.getTimestamp());
                }

            } else {
                ied.addMessage(message);
            }
        }
    }


}