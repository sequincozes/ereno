package br.ufu.facom.ereno.attacks.uc06.devices;

import br.ufu.facom.ereno.attacks.uc06.creator.HighStNumInjectionCreator;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWriter;

import java.util.logging.Logger;

public class HighStNumInjectorIED extends ProtectionIED {
    ProtectionIED legitimateIED; // injector IED will inject messages between the legitimate ones

    public HighStNumInjectorIED(ProtectionIED legitimate) {
        super(GSVDatasetWriter.label[6]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int injectionMessages) {
        Logger.getLogger("HighStNumInjectionIED").info(
                "Feeding HighStNumInjection IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new HighStNumInjectionCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, injectionMessages); // pass itself to receive messages from generator
    }

}