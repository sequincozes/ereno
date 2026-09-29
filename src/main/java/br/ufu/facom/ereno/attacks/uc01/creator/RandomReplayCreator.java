package br.ufu.facom.ereno.attacks.uc01.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/** UC01: replay a previously observed legitimate GOOSE at a later time. */
public class RandomReplayCreator implements MessageCreator {
    private final ArrayList<Goose> legitimateMessages;

    public RandomReplayCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = deepSortedCopy(legitimateMessages);
    }

    @Override
    public void generate(IED ied, int numReplayInstances) {
        if (numReplayInstances <= 0) {
            return;
        }
        if (legitimateMessages.size() < 2) {
            throw new IllegalArgumentException("UC01 requires at least two legitimate GOOSE messages.");
        }

        ArrayList<Goose> generated = new ArrayList<>(numReplayInstances);
        double endTime = legitimateMessages.get(legitimateMessages.size() - 1).getTimestamp();

        for (int i = 0; i < numReplayInstances; i++) {
            int captureIndex = IED.randomBetween(0, legitimateMessages.size() - 2);
            Goose captured = legitimateMessages.get(captureIndex);

            double available = endTime - captured.getTimestamp();
            if (available <= 0.0) {
                continue;
            }

            // A replay delay is random, not tied to the attack sample index.
            double maxDelay = Math.min(10.0, available);
            double minDelay = Math.min(0.010, maxDelay / 2.0);
            double delay = maxDelay > minDelay
                    ? IED.randomBetween(minDelay, maxDelay)
                    : Math.max(Math.ulp(captured.getTimestamp()), maxDelay / 2.0);

            Goose replay = captured.copy();
            replay.setLabel(DatasetWriter.label[1]);
            replay.setTimestamp(captured.getTimestamp() + delay);
            // Keep t, StNum, SqNum and payload exactly as captured: it is a replay.
            generated.add(replay);
        }

        generated.sort(Comparator.comparingDouble(Goose::getTimestamp));
        for (Goose replay : generated) {
            ied.addMessage(replay);
        }
    }

    private static ArrayList<Goose> deepSortedCopy(ArrayList<Goose> source) {
        ArrayList<Goose> copy = new ArrayList<>(source.size());
        for (Goose goose : source) {
            copy.add(goose.copy());
        }
        copy.sort(Comparator.comparingDouble(Goose::getTimestamp));
        return copy;
    }
}
