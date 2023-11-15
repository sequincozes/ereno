package br.ufu.facom.ereno.attacks.uc04.devices;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.attacks.uc04.creator.MaqueradeFakeNormalCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class FakeNormalMasqueratorIED extends ProtectionIED { // Masquerade attacks assume the attacker have full knowledge about the victim ProtectionIED
    ProtectionIED legitimateIED;

    public FakeNormalMasqueratorIED(ProtectionIED legitimate) {
        super();
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
