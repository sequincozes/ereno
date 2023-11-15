package br.ufu.facom.ereno.attacks.uc02.devices;

import br.ufu.facom.ereno.attacks.uc02.creator.InverseReplayCretor;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.util.ArrayList;
import java.util.logging.Logger;

public class InverseReplayerIED extends ProtectionIED {  // Replay attacks does not have any knowledge about the victim, thus it extends a generic IED
    ProtectionIED legitimateIED; // ReplayerIED will replay mensagens from that legitimate device

    public InverseReplayerIED(ProtectionIED legitimate) {
        super(GSVDatasetWritter.label[2]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int numReplayMessages) {
        Logger.getLogger("ReplayerIED").info(
                "Feeding replayer IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new InverseReplayCretor(new ArrayList<>(legitimateIED.copyMessages())); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numReplayMessages); // pass itself to receive messages from generator
    }

}
