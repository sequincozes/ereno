package br.ufu.facom.ereno.attacks.uc05.devices;

import br.ufu.facom.ereno.attacks.uc05.creator.InjectionCreator;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.util.logging.Logger;

public class InjectorIED extends ProtectionIED {

    ProtectionIED legitimateIED; // injector IED will inject messages between the legitimate ones

    public InjectorIED(ProtectionIED legitimate) {
        super(GSVDatasetWritter.label[5]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int injectionMessages) {
        Logger.getLogger("InjectorIED").info(
                "Feeding injector IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new InjectionCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, injectionMessages); // pass itself to receive messages from generator
    }

}
