package br.ufu.facom.ereno.attacks.uc06.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/** UC06: inject a dynamically high/inconsistent StNum without unrelated artifacts. */
public class HighStNumInjectionCreator implements MessageCreator {
    private final ArrayList<Goose> legitimateMessages;

    public HighStNumInjectionCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = deepSortedCopy(legitimateMessages);
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {
        if (numberOfMessages <= 0) {
            return;
        }
        if (legitimateMessages.size() < 2) {
            throw new IllegalArgumentException("UC06 requires at least two legitimate GOOSE messages.");
        }

        ProtectionIED protectionIED = (ProtectionIED) ied;
        double processingDelay = Math.max(0.0, protectionIED.getDelayFromEvent());
        int maxObservedStNum = legitimateMessages.stream().mapToInt(Goose::getStNum).max().orElse(0);
        ArrayList<Goose> generated = new ArrayList<>(numberOfMessages);

        for (int i = 0; i < numberOfMessages; i++) {
            int index = IED.randomBetween(0, legitimateMessages.size() - 2);
            Goose predecessor = legitimateMessages.get(index);
            Goose next = legitimateMessages.get(index + 1);
            double timestamp = midpointOrRandom(predecessor.getTimestamp(), next.getTimestamp());

            Goose injection = predecessor.copy();
            injection.setLabel(DatasetWriter.label[6]);
            injection.setTimestamp(timestamp);
            injection.setT(Math.max(predecessor.getTimestamp(), timestamp - processingDelay));
            injection.setStNum(maxObservedStNum + IED.randomBetween(1000, 10000) + i);
            injection.setSqNum(1);
            // Preserve cbStatus and every protocol/network metadata field.
            generated.add(injection);
        }

        generated.sort(Comparator.comparingDouble(Goose::getTimestamp));
        for (Goose injection : generated) {
            ied.addMessage(injection);
        }
    }

    private static double midpointOrRandom(double lower, double upper) {
        if (upper <= lower) {
            return Math.nextUp(lower);
        }
        double span = upper - lower;
        if (span < 0.002) {
            return lower + span / 2.0;
        }
        return IED.randomBetween(lower + 0.001, upper - 0.001);
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
