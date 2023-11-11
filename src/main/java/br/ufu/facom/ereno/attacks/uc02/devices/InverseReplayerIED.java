package br.ufu.facom.ereno.attacks.uc02.devices;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.attacks.uc02.creator.InverseReplayCretor;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

public class InverseReplayerIED extends IED {  // Replay attacks does not have any knowledge about the victim, thus it extends a generic IED
    protected ArrayList<Goose> replayedMessages;

    ProtectionIED legitimateIED; // ReplayerIED will replay mensagens from that legitimate device

    public InverseReplayerIED(ProtectionIED legitimate) {
        this.legitimateIED = legitimate;
        replayedMessages = new ArrayList<>();
    }

    @Override
    public void run(int numReplayMessages) {
        Logger.getLogger("ReplayerIED").info(
                "Feeding replayer IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new InverseReplayCretor(new ArrayList<>(legitimateIED.copyMessages())); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numReplayMessages); // pass itself to receive messages from generator
    }

    @Override
    public void addMessage(EthernetFrame message) {
        if (GooseFlow.ECF.numberOfMessages >= replayedMessages.size())
            replayedMessages.add((Goose) message);
    }

    public ArrayList<Goose> getReplayedMessages() {
        return this.replayedMessages;
    }

    public int getNumberOfMessages() {
        return getReplayedMessages().size();
    }
}
