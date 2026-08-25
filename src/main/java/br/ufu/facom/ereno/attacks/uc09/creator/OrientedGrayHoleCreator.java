package br.ufu.facom.ereno.attacks.uc09.creator;

import br.ufu.facom.ereno.api.RunContext;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Objects;

import static br.ufu.facom.ereno.general.IED.randomBetween;

public class OrientedGrayHoleCreator implements MessageCreator {

    ArrayList<Goose> legitimateMessages;
    Integer discardRate;
    Integer toDiscardPackets;

    RunContext.Variant approaches;

    /**
     * Reads the variant and the attack parameters from {@link RunContext}
     * rather than hardcoding them.
     *
     * <p>These three values used to be literals in this constructor, so
     * producing the four variants meant editing this file and rebuilding once
     * per variant. That is why the delivered dataset holds exactly four traces,
     * one per class, with nothing in the rows saying which build produced which
     * row. The variant enum now lives in {@link RunContext.Variant} so the value
     * the attack switches on and the value written into the dataset cannot
     * drift apart.</p>
     */
    public OrientedGrayHoleCreator(ArrayList<Goose> legitimateMessages) {
        RunContext.requireLoaded();
        this.legitimateMessages = legitimateMessages;
        this.discardRate = RunContext.discardRate;
        this.toDiscardPackets = RunContext.burstSize;
        this.approaches = RunContext.variant;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {

        // FULLY_RANDOMIZED approach doesn't care about stNum changes
        if (approaches == RunContext.Variant.FULLY_RANDOMIZED) {
            generateFullyRandomized(ied, numberOfMessages);
            return;
        }

        // Original approaches (stNum-oriented)
        for (int i = 0; i < numberOfMessages; i++) {

            Goose message = legitimateMessages.get(i);
            Goose lastMessage = (i > 0) ? legitimateMessages.get(i - 1) : null;
            Goose nextMessage = (i + 1 < numberOfMessages) ? legitimateMessages.get(i + 1) : null;
            boolean toLabel = false;

            if (lastMessage != null && message.getStNum() != lastMessage.getStNum()) {
                System.out.println("[" + approaches.name() + " ORIENTED GRAYHOLE] STATUS HAS CHANGED " + message.getStNum());
                if (Objects.requireNonNull(approaches) == RunContext.Variant.RANDOMIC_BURST) {
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
                else if (Objects.requireNonNull(approaches) == RunContext.Variant.RANDOMIC_MESSAGE) {
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
                else if (Objects.requireNonNull(approaches) == RunContext.Variant.DETERMINISTIC_BURST) {
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

    /**
     * Fully randomized grayhole approach.
     * Every message is evaluated independently:
     * - Random draw based on discard rate
     * - If discarded: remove message and label the next one
     * - If not discarded: add message normally
     * Does NOT care about stNum changes.
     */
    private void generateFullyRandomized(IED ied, int numberOfMessages) {
        System.out.println("[FULLY_RANDOMIZED GRAYHOLE] Starting fully randomized approach with discard rate: " + discardRate + "%");

        for (int i = 0; i < numberOfMessages; i++) {
            Goose message = legitimateMessages.get(i);

            // Random draw: should this message be discarded?
            if (randomBetween(0, 100) < discardRate) {
                // DISCARD this message
                ied.removeMessage(message);
                System.out.println("[FULLY_RANDOMIZED GRAYHOLE] Discarded message at timestamp " + message.getTimestamp());

                // Label the next message (if it exists)
                if ((i + 1) < numberOfMessages) {
                    Goose nextMessage = legitimateMessages.get(i + 1);
                    nextMessage.setLabel("FULLY_RANDOMIZED_" + (GSVDatasetWriter.label[9]).toUpperCase());
                    System.out.println("[FULLY_RANDOMIZED GRAYHOLE] Labeled next message at timestamp: " + nextMessage.getTimestamp());
                }
            } else {
                // KEEP this message
                ied.addMessage(message);
                System.out.println("[FULLY_RANDOMIZED GRAYHOLE] Kept message at timestamp " + message.getTimestamp());
            }
        }

        System.out.println("[FULLY_RANDOMIZED GRAYHOLE] Completed processing " + numberOfMessages + " messages");
    }


}