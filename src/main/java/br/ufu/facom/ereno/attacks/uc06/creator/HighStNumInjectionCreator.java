package br.ufu.facom.ereno.attacks.uc06.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWriter;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

public class HighStNumInjectionCreator implements MessageCreator {
    private final ArrayList<Goose> legitimateMessages;

    public HighStNumInjectionCreator(ArrayList<Goose> legitimateMessages) {
        super();
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int numberofMessages) {
        for (int i = 0; i < numberofMessages; i++) {
            double minGoose = legitimateMessages.get(0).getTimestamp();
            double maxGoose = legitimateMessages.get(legitimateMessages.size() - 1).getTimestamp();
            double timestamp = randomBetween(minGoose, maxGoose);
            double t = randomBetween(minGoose, maxGoose);
            int stNum = randomBetween(1000, 100000);
            int sqNum = randomBetween(0, 100);
            int cbStatus = randomBetween(0, 1);
            int timeAllowedToLive = 10000;
            int confRev = randomBetween(0, 100);

            // Last Goose Message from the random time
            Goose injectionMessage = new Goose(cbStatus, stNum, sqNum, timestamp, t, GSVDatasetWriter.label[6]);
            injectionMessage.setSqNum(sqNum);
            injectionMessage.setStNum(stNum);
            injectionMessage.setCbStatus(cbStatus);
//            injectionMessage.setConfRev(confRev);
//            injectionMessage.setGooseTimeAllowedtoLive(timeAllowedToLive);

            // Send the generated message to InjectorIED
            ied.addMessage(injectionMessage);

        }

    }
}
