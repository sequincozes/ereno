package br.ufu.facom.ereno.attacks.uc03.devices;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.attacks.uc03.creator.MaqueradeFakeFaultCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class FakeFaultMasqueratorIED extends IED { // Masquerade attacks assume the attacker have full knowledge about the victim ProtectionIED
    protected ArrayList<Goose> masqueradeMessages; // The generated masquerade messages will be stored here

    ProtectionIED legitimateIED;

    public FakeFaultMasqueratorIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        masqueradeMessages = new ArrayList<>();
    }

    @Override
    public void run(int numMasqueradeInstances) {
        Logger.getLogger("MasqueratorIED").info(
                "Feeding Masquerator IED with " + legitimateIED.getMessages().size() + " legitimate messages to generate " + numMasqueradeInstances + " masquerade fake faults.");
        messageCreator = new MaqueradeFakeFaultCreator(legitimateIED.getMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numMasqueradeInstances); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        if (GooseFlow.ECF.numberOfMessages >= masqueradeMessages.size()) masqueradeMessages.add((Goose) message);
    }

    public ArrayList<Goose> getMasqueradeMessages() {
        return this.masqueradeMessages;
    }

    public int getNumberOfMessages() {
        return masqueradeMessages.size();
    }

    public ProtectionIED getLegitimateIED() {
        return legitimateIED;
    }
}
