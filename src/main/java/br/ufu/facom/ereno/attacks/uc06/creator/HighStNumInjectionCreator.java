package br.ufu.facom.ereno.attacks.uc06.creator;

import br.ufu.facom.ereno.utils.Util;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

public class HighStNumInjectionCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     */
    public HighStNumInjectionCreator(ArrayList<Goose> legitimateMessages) {
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
            int cbStatus = randomBetween(0, 2);
            int timeAllowedToLive = randomBetween(0, 10000);
            int confRev = randomBetween(0, 100);

            // Last Goose Message from the random time
            Goose injectionMessage = new Goose(cbStatus, stNum, sqNum, timestamp, t, Util.label[6]);
            injectionMessage.setSqNum(sqNum);
            injectionMessage.setStNum(stNum);
            injectionMessage.setCbStatus(cbStatus);
            injectionMessage.setConfRev(confRev);
            injectionMessage.setGooseTimeAllowedtoLive(timeAllowedToLive);

            // Send the generated message to InjectorIED
            ied.addMessage(injectionMessage);

        }

    }
}
