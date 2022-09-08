package br.ufu.facom.ereno.attacks.uc07.devices;

import br.ufu.facom.ereno.attacks.uc07.creator.HighRateStNumInjectionCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class HighRateStNumInjectorIED extends IED {

    protected ArrayList<Goose> injectedMessages; // The generated messages will be stored here

    ProtectionIED legitimateIED; // injector IED will inject messages between the legitimate ones

    public HighRateStNumInjectorIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        injectedMessages = new ArrayList<>();
    }

    @Override
    public void run(int injectionMessages) {
        Logger.getLogger("HighStNumInjectionIED").info(
                "Feeding HighStNumInjection IED with " + legitimateIED.getMessages().size() + " legitimate messages");
        messageCreator = new HighRateStNumInjectionCreator(legitimateIED.getMessages()); // feeds the message creator with legitimate messages
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
