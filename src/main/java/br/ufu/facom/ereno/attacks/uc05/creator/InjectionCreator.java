package br.ufu.facom.ereno.attacks.uc05.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * UC05: protocol-valid message injection.
 *
 * <p>The old implementation generated impossible cbStatus values, independent
 * t/timestamp values, and class-exclusive TTL/confRev/network identifiers. Those
 * fields made the class identifiable from generator artifacts. The default UC05
 * now starts from a legitimate packet template and changes only plausible GOOSE
 * state/sequence semantics.</p>
 */
public class InjectionCreator implements MessageCreator {
    private final ArrayList<Goose> legitimateMessages;

    public InjectionCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = deepSortedCopy(legitimateMessages);
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {
        if (numberOfMessages <= 0) {
            return;
        }
        if (legitimateMessages.size() < 2) {
            throw new IllegalArgumentException("UC05 requires at least two legitimate GOOSE messages.");
        }

        ProtectionIED protectionIED = (ProtectionIED) ied;
        double processingDelay = Math.max(0.0, protectionIED.getDelayFromEvent());
        ArrayList<Goose> generated = new ArrayList<>(numberOfMessages);
        for (int i = 0; i < numberOfMessages; i++) {
            int predecessorIndex = randomUsableInterval();
            Goose predecessor = legitimateMessages.get(predecessorIndex);
            Goose next = legitimateMessages.get(predecessorIndex + 1);

            double sendTime = randomStrictlyBetween(predecessor.getTimestamp(), next.getTimestamp());
            Goose injection = predecessor.copy();
            injection.setLabel(DatasetWriter.label[5]);
            injection.setTimestamp(sendTime);

            int strategy = IED.randomBetween(0, 2);
            if (strategy == 0) {
                // Forge a plausible state change.
                injection.setCbStatus(predecessor.getInverseCbStatus());
                injection.setStNum(predecessor.getStNum() + 1);
                injection.setSqNum(1);
                injection.setT(Math.max(predecessor.getTimestamp(), sendTime - processingDelay));
            } else if (strategy == 1) {
                // Keep state but inject an unexpected nearby state number.
                injection.setStNum(predecessor.getStNum() + IED.randomBetween(1, 3));
                injection.setSqNum(1);
                injection.setT(Math.max(predecessor.getTimestamp(), sendTime - processingDelay));
            } else {
                // Duplicate/skip retransmission sequence without malformed fields.
                injection.setStNum(predecessor.getStNum());
                injection.setSqNum(predecessor.getSqNum() + IED.randomBetween(2, 5));
                injection.setT(Math.min(predecessor.getT(), sendTime));
            }

            // Network identifiers, TTL and confRev are intentionally inherited
            // from the legitimate template to avoid generator-only signatures.
            generated.add(injection);
        }

        generated.sort(Comparator.comparingDouble(Goose::getTimestamp));
        for (Goose injection : generated) {
            ied.addMessage(injection);
        }
    }

    private int randomUsableInterval() {
        for (int attempt = 0; attempt < 100; attempt++) {
            int index = IED.randomBetween(0, legitimateMessages.size() - 2);
            if (legitimateMessages.get(index + 1).getTimestamp() > legitimateMessages.get(index).getTimestamp()) {
                return index;
            }
        }
        for (int i = 0; i < legitimateMessages.size() - 1; i++) {
            if (legitimateMessages.get(i + 1).getTimestamp() > legitimateMessages.get(i).getTimestamp()) {
                return i;
            }
        }
        throw new IllegalArgumentException("UC05 cannot find a positive inter-message interval.");
    }

    private static double randomStrictlyBetween(double lower, double upper) {
        if (upper <= lower) {
            return Math.nextUp(lower);
        }
        double margin = Math.min(0.001, (upper - lower) / 4.0);
        double lo = lower + margin;
        double hi = upper - margin;
        if (hi <= lo) {
            return lower + (upper - lower) / 2.0;
        }
        return IED.randomBetween(lo, hi);
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
