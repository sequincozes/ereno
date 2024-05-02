package br.ufu.facom.ereno.attacks.uc01.devices;

import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.attacks.uc01.creator.RandomReplayCreator;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;

import java.util.logging.Logger;

public class RandomReplayerIED extends ProtectionIED {  // Replay attacks does not have any knowledge about the victim, thus it extends a generic IED

    ProtectionIED legitimateIED; // ReplayerIED will replay mensagens from that legitimate device

    public RandomReplayerIED(ProtectionIED legitimate) {
        super(GSVDatasetWriter.label[1]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int numberOfReplayMessages) {
        Logger.getLogger("ReplayerIED").info(
                "Feeding replayer IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new RandomReplayCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, numberOfReplayMessages); // pass itself to receive messages from generator
    }

}
