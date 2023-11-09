package br.ufu.facom.ereno.attacks.uc07.creator;

import br.ufu.facom.ereno.utils.Util;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

public class HighRateStNumInjectionCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     */
    public HighRateStNumInjectionCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    /**
     * Increases StNum at high rate
     */
    @Override
    public void generate(IED ied, int numberofMessages) {
        double minGoose = legitimateMessages.get(0).getTimestamp();
        double maxGoose = legitimateMessages.get(legitimateMessages.size() - 1).getTimestamp();
        double timestamp = randomBetween(minGoose, maxGoose);
        double t = timestamp;
        int stNum = 1;
        int sqNum = 1;
        int cbStatus = 1;

//        Goose injectionMessage = new Goose(cbStatus, stNum, sqNum, timestamp, t, Util.label[7]);
//        injectionMessage.setSqNum(sqNum);
//        injectionMessage.setCbStatus(cbStatus);
//        injectionMessage.setConfRev(confRev);
//        injectionMessage.setGooseTimeAllowedtoLive(timeAllowedToLive);

        for (int i = 0; i < numberofMessages; i++) {
            // Increases the stNum at a high rate (up to 100x the minTime)
            double minTime = Double.valueOf(SetupIED.ECF.minTime);
            double randomDelay = randomBetween((minTime / 10000), minTime / 1000);
            timestamp = timestamp + randomDelay;
            stNum = stNum + 1;
            // Send the generated message to InjectorIED
            ied.addMessage(new Goose(cbStatus, stNum, sqNum, timestamp, t, Util.label[7]));

        }

    }
}
