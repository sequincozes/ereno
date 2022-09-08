package br.ufu.facom.ereno.attacks.uc05.devices;

import br.ufu.facom.ereno.attacks.uc01.creator.RandomReplayCreator;
import br.ufu.facom.ereno.attacks.uc05.creator.InjectionCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class InjectorIED extends IED {  // Replay attacks does not have any knowledge about the victim, thus it extends a generic IED

    protected ArrayList<Goose> injectedMessages; // The generated replay messages will be stored here

    ProtectionIED legitimateIED; // injector IED will inject messages between the legitimate ones

    public InjectorIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        injectedMessages = new ArrayList<>();
    }

    @Override
    public void run(int injectionMessages) {
        Logger.getLogger("InjectorIED").info(
                "Feeding injector IED with " + legitimateIED.getMessages().size() + " legitimate messages");
        messageCreator = new InjectionCreator(legitimateIED.getMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, injectionMessages); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        injectedMessages.add((Goose) message);
    }

    public ArrayList<Goose> getInjectedMessages() {
        return this.injectedMessages;
    }

    public int getNumberOfMessages() {
        return getInjectedMessages().size();
    }
}
