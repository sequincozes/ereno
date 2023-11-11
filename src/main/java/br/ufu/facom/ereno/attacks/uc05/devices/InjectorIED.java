package br.ufu.facom.ereno.attacks.uc05.devices;

import br.ufu.facom.ereno.MultiSource;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.attacks.uc05.creator.InjectionCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class InjectorIED extends IED {

    protected ArrayList<Goose> injectedMessages; // The generated messages will be stored here

    ProtectionIED legitimateIED; // injector IED will inject messages between the legitimate ones

    public InjectorIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        injectedMessages = new ArrayList<>();
    }

    @Override
    public void run(int injectionMessages) {
        Logger.getLogger("InjectorIED").info(
                "Feeding injector IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new InjectionCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, injectionMessages); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        if (((Goose) message).getTimestamp() < 0) {
            System.out.println("CULPADO: "+ MultiSource.run);
            throw new IllegalArgumentException("The GOOSE message has a negative timestamp");
        } else if (GooseFlow.ECF.numberOfMessages >= injectedMessages.size()){
            this.injectedMessages.add((Goose) message);
        }
    }

    public ArrayList<Goose> getInjectedMessages() {
        return this.injectedMessages;
    }

    public int getNumberOfMessages() {
        return getInjectedMessages().size();
    }
}
