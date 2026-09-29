package br.ufu.facom.ereno.attacks.uc02.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * UC02: after a legitimate state transition, replay a frame carrying the
 * previous (inverse) state. Exactly {@code numReplayInstances} are produced
 * whenever at least one legitimate state transition exists.
 */
public class InverseReplayCretor implements MessageCreator {
    private final ArrayList<Goose> legitimateMessages;

    public InverseReplayCretor(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = deepSortedCopy(legitimateMessages);
    }

    @Override
    public void generate(IED ied, int numReplayInstances) {
        if (numReplayInstances <= 0) {
            return;
        }

        ArrayList<Integer> transitions = new ArrayList<>();
        for (int i = 1; i < legitimateMessages.size(); i++) {
            if (legitimateMessages.get(i - 1).getCbStatus() != legitimateMessages.get(i).getCbStatus()) {
                transitions.add(i);
            }
        }
        if (transitions.isEmpty()) {
            throw new IllegalArgumentException("UC02 requires at least one legitimate GOOSE state transition.");
        }

        ArrayList<Goose> generated = new ArrayList<>(numReplayInstances);
        for (int i = 0; i < numReplayInstances; i++) {
            int transitionIndex = transitions.get(IED.randomBetween(0, transitions.size() - 1));
            Goose beforeChange = legitimateMessages.get(transitionIndex - 1);
            Goose changedState = legitimateMessages.get(transitionIndex);

            double delay = IED.randomBetween(0.010, 0.100);
            double sendTime = changedState.getTimestamp() + delay;

            Goose replay = beforeChange.copy();
            replay.setLabel(DatasetWriter.label[2]);
            replay.setTimestamp(sendTime);
            // t and sequence/state fields remain those of the captured old frame.
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
