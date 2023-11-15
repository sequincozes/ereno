package br.ufu.facom.ereno.attacks.uc03.devices;

import br.ufu.facom.ereno.attacks.uc03.creator.MaqueradeFakeFaultCreator;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.util.logging.Logger;

public class FakeFaultMasqueratorIED extends ProtectionIED { // Masquerade attacks assume the attacker have full knowledge about the victim ProtectionIED
    ProtectionIED legitimateIED;

    public FakeFaultMasqueratorIED(ProtectionIED legitimate) {
        super(GSVDatasetWritter.label[3]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int numMasqueradeInstances) {
        Logger.getLogger("MasqueratorIED").info(
                "Feeding Masquerator IED with " + legitimateIED.copyMessages().size() + " legitimate messages to generate " + numMasqueradeInstances + " masquerade fake faults.");
        messageCreator = new MaqueradeFakeFaultCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numMasqueradeInstances); // pass itself to receive messages from generator
    }

    public ProtectionIED getLegitimateIED() {
        return this.legitimateIED;
    }
}
