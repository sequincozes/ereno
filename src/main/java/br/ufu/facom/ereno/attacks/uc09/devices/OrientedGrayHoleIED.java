package br.ufu.facom.ereno.attacks.uc09.devices;

import br.ufu.facom.ereno.attacks.uc09.creator.OrientedGrayHoleCreator;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;
import br.ufu.facom.ereno.general.ProtectionIED;

import java.util.logging.Logger;

public class OrientedGrayHoleIED extends ProtectionIED {

    ProtectionIED legitimateIED;

    public OrientedGrayHoleIED(ProtectionIED legitimate) {
        super(GSVDatasetWriter.label[9]);
        this.legitimateIED = legitimate;
    }

    public void run(int discardRate) {
        setInitialTimestamp((float) legitimateIED.getMessages().get(0).getTimestamp());

        Logger.getLogger("OrientedGrayHoleIED").info(
                "Feeding OrientedGrayHole IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new OrientedGrayHoleCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, discardRate); // pass itself to receive messages from generator
    }

}
