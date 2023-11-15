package br.ufu.facom.ereno.attacks.uc04.devices;

import br.ufu.facom.ereno.attacks.uc04.creator.MaqueradeFakeNormalCreator;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.util.logging.Logger;

public class FakeNormalMasqueratorIED extends ProtectionIED { // Masquerade attacks assume the attacker have full knowledge about the victim ProtectionIED
    ProtectionIED legitimateIED;

    public FakeNormalMasqueratorIED(ProtectionIED legitimate) {
        super(GSVDatasetWritter.label[4]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int numMasqueradeInstances) {
        Logger.getLogger("MasqueratorIED").info(
                "Feeding Masquerator IED with " + legitimateIED.copyMessages().size() + " legitimate messages to generate " + numMasqueradeInstances + " masquerade fake normal.");
        messageCreator = new MaqueradeFakeNormalCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numMasqueradeInstances); // pass itself to receive messages from generator
    }


    public ProtectionIED getLegitimateIED() {
        return this.legitimateIED;
    }
}
