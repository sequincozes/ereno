package br.ufu.facom.ereno.attacks.uc08.devices;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.attacks.uc08.creator.GrayHoleVictimCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class GrayHoleVictimIED extends IED {  // Gray hole attackers does not have any knowledge about the victim, thus it extends a generic IED

    protected ArrayList<Goose> nonDiscardedMessages; // The messages which are not dicarded will be stored here
    ProtectionIED legitimateIED; // GrayHoleVictimIED will discard mensagens from that legitimate device

    public GrayHoleVictimIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        nonDiscardedMessages = new ArrayList<>();
    }

    @Override
    public void run(int discardingRate) {
        Logger.getLogger("GrayHoleVictimIED").info(
                "Feeding gray hole victim IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new GrayHoleVictimCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, discardingRate); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        if (GooseFlow.ECF.numberOfMessages>=nonDiscardedMessages.size())
            nonDiscardedMessages.add((Goose) message);
    }

    public ArrayList<Goose> getNonDiscardedMessages() {
        return this.nonDiscardedMessages;
    }

    public int getNumberOfMessages() {
        return getNonDiscardedMessages().size();
    }
}
