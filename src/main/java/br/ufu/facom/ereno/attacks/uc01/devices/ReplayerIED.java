package br.ufu.facom.ereno.attacks.uc01.devices;

import br.ufu.facom.ereno.benign.devices.IED;
import br.ufu.facom.ereno.benign.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.attacks.uc01.creator.RandomReplayCreator;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ReplayerIED extends IED {

    protected ArrayList<Goose> replayedMessages;

    ProtectionIED legitimateIED; // ReplayerIED will replay mensagens from that legitimate device

    private int numReplayMessages;

    public ReplayerIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        replayedMessages = new ArrayList<>();
    }

    @Override
    public void run() {
        Logger.getLogger("ReplayerIED").info(
                "Feeding replayer IED with " + legitimateIED.getMessages().size() + " legitimate messages");
        messageCreator = new RandomReplayCreator(legitimateIED.getMessages(), getNumReplayMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        replayedMessages.add((Goose) message);
    }

    public ArrayList<Goose> getReplayedMessages() {
        return this.replayedMessages;
    }

    public int getNumReplayMessages() {
        return numReplayMessages;
    }

    public void setNumReplayMessages(int numReplayMessages) {
        this.numReplayMessages = numReplayMessages;
    }
}
