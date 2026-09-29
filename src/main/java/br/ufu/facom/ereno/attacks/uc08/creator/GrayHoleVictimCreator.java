package br.ufu.facom.ereno.attacks.uc08.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

/** UC08: retain only a fraction of the victim stream, without mutating UC00. */
public class GrayHoleVictimCreator implements MessageCreator {
    private final ArrayList<Goose> legitimateMessages;

    public GrayHoleVictimCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int selectionRate) {
        if (selectionRate < 0 || selectionRate > 100) {
            throw new IllegalArgumentException("selectionRate must be between 0 and 100.");
        }
        for (Goose original : legitimateMessages) {
            if (IED.randomBetween(0, 99) < selectionRate) {
                Goose observed = original.copy();
                observed.setLabel(DatasetWriter.label[8]);
                ied.addMessage(observed);
            }
        }
    }
}
