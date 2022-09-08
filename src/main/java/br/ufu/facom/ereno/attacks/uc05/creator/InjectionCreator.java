package br.ufu.facom.ereno.attacks.uc05.creator;

import br.ufu.facom.ereno.Util;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

public class InjectionCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;

    /**
     * @param legitimateMessages - previously generated legitimate messages
     */
    public InjectionCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int numberofMessages) {
        for (int i = 0; i < numberofMessages; i++) {
            double minGoose = legitimateMessages.get(0).getTimestamp();
            double maxGoose = legitimateMessages.get(legitimateMessages.size() - 1).getTimestamp();
            double timestamp = randomBetween(minGoose, maxGoose);
            double t = randomBetween(minGoose, maxGoose);
            int stNum = randomBetween(0, 10000);
            int sqNum = randomBetween(0, 10000);
            int cbStatus = randomBetween(0, 2);
            int timeAllowedToLive = randomBetween(0, 100000);
            int confRev = randomBetween(0, 100);

            // Last Goose Message from the random time
            Goose injectionMessage = new Goose(cbStatus, stNum, sqNum, timestamp, t, Util.label[5]);
            injectionMessage.setSqNum(sqNum);
            injectionMessage.setStNum(stNum);
            injectionMessage.setCbStatus(cbStatus);
            injectionMessage.setConfRev(confRev);
            injectionMessage.setGooseTimeAllowedtoLive(timeAllowedToLive);

            String[] legitimateMacAddress = {"FF:FF:FF:FF:FF:11", "FF:FF:FF:FF:FF:22", "FF:FF:FF:FF:FF:33", "FF:FF:FF:FF:FF:44", "FF:FF:FF:FF:FF:55", "FF:FF:FF:FF:FF:66", "FF:FF:FF:FF:FF:FF", "FF:FF:FF:FF:FF:77", "FF:FF:FF:FF:FF:AA", "FF:FF:FF:FF:FF:BB", "FF:FF:FF:FF:FF:CC", "FF:FF:FF:FF:FF:DD", "FF:FF:FF:FF:FF:EE", "FF:FF:FF:FF:FF:AB", "FF:FF:FF:FF:FF:AC"};
            Goose.ethDst = legitimateMacAddress[randomBetween(0, legitimateMacAddress.length - 1)];
            Goose.ethSrc = legitimateMacAddress[randomBetween(0, legitimateMacAddress.length - 1)];

            /**
             @TODO in next versions, one can vary the parameters below (randomly, as done above)
             */
            Goose.ethType = "0x000077b7";
            Goose.gooseAppid = "0x00003002";
            Goose.TPID = "0x7101";
            Goose.gocbRef = "LD/LLN0$IntLockB";
            Goose.datSet = "AA1C1Q01A1LD0/LLN0$InterlockingC";
            Goose.goID = "InterlockingF";
            Goose.test = "TRUE";
            Goose.ndsCom = "TRUE";
            Goose.protocol = "SV";

            // Send the generated message to InjectorIED
            ied.addMessage(injectionMessage);

        }

    }
}
