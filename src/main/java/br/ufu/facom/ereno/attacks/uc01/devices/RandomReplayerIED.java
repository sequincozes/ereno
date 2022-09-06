package br.ufu.facom.ereno.attacks.uc01.devices;

import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.attacks.uc01.creator.RandomReplayCreator;

import java.util.ArrayList;
import java.util.logging.Logger;

public class RandomReplayerIED extends IED {  // Replay attacks does not have any knowledge about the victim, thus it extends a generic IED

    protected ArrayList<Goose> replayedMessages; // The generated replay messages will be stored here

    ProtectionIED legitimateIED; // ReplayerIED will replay mensagens from that legitimate device

    public RandomReplayerIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        replayedMessages = new ArrayList<>();
    }

    @Override
    public void run(int numberOfReplayMessages) {
        Logger.getLogger("ReplayerIED").info(
                "Feeding replayer IED with " + legitimateIED.getMessages().size() + " legitimate messages");
        messageCreator = new RandomReplayCreator(legitimateIED.getMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numberOfReplayMessages); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        replayedMessages.add((Goose) message);
    }

    public ArrayList<Goose> getReplayedMessages() {
        return this.replayedMessages;
    }

    public int getNumberOfMessages() {
        return getReplayedMessages().size();
    }
}
