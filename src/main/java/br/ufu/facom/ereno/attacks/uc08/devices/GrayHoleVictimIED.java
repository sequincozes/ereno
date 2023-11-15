package br.ufu.facom.ereno.attacks.uc08.devices;

import br.ufu.facom.ereno.attacks.uc08.creator.GrayHoleVictimCreator;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.util.logging.Logger;

public class GrayHoleVictimIED extends ProtectionIED {  // Gray hole attackers does not have any knowledge about the victim, thus it extends a generic IED

    ProtectionIED legitimateIED; // GrayHoleVictimIED will discard mensagens from that legitimate device

    public GrayHoleVictimIED(ProtectionIED legitimate) {
        super(GSVDatasetWritter.label[8]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int discardingRate) {
        Logger.getLogger("GrayHoleVictimIED").info(
                "Feeding gray hole victim IED with " + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new GrayHoleVictimCreator(legitimateIED.copyMessages()); // feeds the message creator with legitimate messages
        messageCreator.generate(this, discardingRate); // pass itself to receive messages from generator
    }

}
