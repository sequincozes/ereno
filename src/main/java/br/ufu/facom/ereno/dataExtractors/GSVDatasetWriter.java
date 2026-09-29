package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static br.ufu.facom.ereno.api.GooseFlow.ECF.numberOfMessages;

/** GOOSE+SV writer with causal predecessor selection. */
public class GSVDatasetWriter extends DatasetWriter {

    public static int writeNormal(Goose seedMessage, ArrayList<Goose> gooseMessages,
                                  ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        if (printHeader) {
            writeHybridHeader();
        }
        return writeSequence(seedMessage, gooseMessages, svMessages);
    }

    public static int writeMasquerade(Goose seedMessage, ArrayList<Goose> gooseMessages,
                                      ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        if (printHeader) {
            writeHybridHeader();
        }
        return writeSequence(seedMessage, gooseMessages, svMessages);
    }

    private static int writeSequence(Goose seedMessage, ArrayList<Goose> messages,
                                     ArrayList<Sv> svMessages) throws IOException {
        ArrayList<Goose> stream = deepSortedCopy(messages);
        Goose previous = seedMessage == null ? null : seedMessage.copy();
        int written = 0;
        for (Goose current : stream) {
            if (previous != null && previous.getTimestamp() < current.getTimestamp()) {
                if (writeHybridRow(svMessages, previous, current)) {
                    written++;
                }
            }
            previous = current;
        }
        return written;
    }

    /**
     * Writes only malicious rows, but derives each row's inter-message features
     * from the immediately preceding frame in a merged normal+attack timeline.
     */
    public static int writeAttack(ProtectionIED legitimateIED, ProtectionIED attacker,
                                  MergingUnit mu, boolean printHeader) throws IOException {
        if (printHeader) {
            writeHybridHeader();
        }

        ArrayList<Goose> normal = deepSortedCopy(legitimateIED.getMessages());
        ArrayList<Goose> attack = deepSortedCopy(attacker.getMessages());
        int cap = numberOfMessages > 0 ? Math.min(numberOfMessages, attack.size()) : attack.size();
        if (attack.size() > cap) {
            attack = new ArrayList<>(attack.subList(0, cap));
        }

        ArrayList<Goose> stream = new ArrayList<>(normal.size() + attack.size());
        stream.addAll(normal);
        stream.addAll(attack);
        // Java's TimSort is stable: for exact timestamp ties, normal frames (added
        // first) precede attack frames, avoiding arbitrary class-dependent ordering.
        stream.sort(Comparator.comparingDouble(Goose::getTimestamp));

        String attackLabel = attacker.getLabel();
        Goose previous = null;
        int written = 0;
        for (Goose current : stream) {
            if (previous != null && attackLabel.equals(current.getLabel())) {
                if (writeHybridRow(mu.getMessages(), previous, current)) {
                    written++;
                }
            }
            previous = current;
        }
        return written;
    }
}
