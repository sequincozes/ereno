package br.ufu.facom.ereno.attacks.uc07.devices;

import br.ufu.facom.ereno.attacks.uc07.creator.HighRateStNumInjectionCreator;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;

import java.util.logging.Logger;

public class HighRateStNumInjectorIED extends ProtectionIED {

    ProtectionIED legitimateIED; // injector IED will inject messages between the legitimate ones

    public HighRateStNumInjectorIED(ProtectionIED legitimate) {
        super(GSVDatasetWriter.label[7]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int injectionMessages) {
        setInitialTimestamp((float) legitimateIED.getMessages().get(0).getTimestamp());

        Logger.getLogger("HighStNumInjectionIED").info(
                "Feeding HighStNumInjection IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new HighRateStNumInjectionCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, injectionMessages); // pass itself to receive messages from generator
    }

}
