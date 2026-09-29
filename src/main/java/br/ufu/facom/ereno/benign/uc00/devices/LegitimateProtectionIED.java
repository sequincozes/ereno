package br.ufu.facom.ereno.benign.uc00.devices;

import br.ufu.facom.ereno.benign.uc00.creator.GooseCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

/** Legitimate protection IED used by UC00 and as the victim of attack UCs. */
public class LegitimateProtectionIED extends ProtectionIED {

    public LegitimateProtectionIED() {
        super(DatasetWriter.label[0]);
    }

    @Override
    public void run(int normalMessages) {
        messageCreator = new GooseCreator(getLabel());
        messageCreator.generate(this, normalMessages);
        sortMessagesChronologically();
    }

    @Override
    public void addMessage(EthernetFrame message) {
        getMessages().add((Goose) message);
    }

    public void addMessages(ArrayList<Goose> additionalMessages) {
        getMessages().addAll(additionalMessages);
    }

    public Goose getSeedMessage() {
        if (!(messageCreator instanceof GooseCreator)) {
            return null;
        }
        return ((GooseCreator) messageCreator).getSeedMessage();
    }
}
