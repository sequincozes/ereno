package br.ufu.facom.ereno.attacks.uc04.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;
import br.ufu.facom.ereno.attacks.uc04.devices.FakeNormalMasqueratorIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

public class MaqueradeFakeNormalCreator implements MessageCreator {

    private final int timeTakenByAttacker = 1;
    private final ArrayList<Goose> legitimateMessages;

    public MaqueradeFakeNormalCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int numMasqueradeInstances) {
        for (int masqueradeMessageIndex = 0; masqueradeMessageIndex <= numMasqueradeInstances; masqueradeMessageIndex++) {

            // Step 1 - Monitors the network until finding a GOOSE with CBStatus = 1
            Goose lastLegitimateGoose = null;
            boolean foundFalseCBStatus = false;
            while (!foundFalseCBStatus) {
                int lastGoose = randomBetween(0, legitimateMessages.size()-1);
                lastLegitimateGoose = legitimateMessages.get(lastGoose);
                if (lastLegitimateGoose.getCbStatus() == 1) {
                    foundFalseCBStatus = true;
                }
            }

            // Step 2 - Reports a fake fault (CBStatus = 1)
            ArrayList<Goose> gooseMessages = reportFakeEventAt((FakeNormalMasqueratorIED) ied, lastLegitimateGoose);
            for (Goose masquerade : gooseMessages) {
                ied.addMessage(masquerade);
            }

            if (((FakeNormalMasqueratorIED) ied).getNumberOfMessages() >= 1000) {
                break;
            }
        }

    }

    public ArrayList<Goose> reportFakeEventAt(FakeNormalMasqueratorIED fakeNormalMasqueratorIED, Goose lastLegitimateMessage) {
        ArrayList<Goose> masqueratedGooseMessages = new ArrayList<>();
        double fakeEventTimestamp = lastLegitimateMessage.getTimestamp() + timeTakenByAttacker; // the masquerade messages will be transmitted immediately after this message
        String label = GSVDatasetWritter.label[4];  // label it as masquerade fake fault (uc03)
        int fakeCBStatus = 0;
        int fakeIncreasedStNum = lastLegitimateMessage.getStNum() + 1;
        int fakeIncreasingSqNum = 1;
        double t = fakeEventTimestamp + ProtectionIED.delayFromEvent; // new t
        double timestamp = t; // timestamp has the same value as the last change (t) because it simulates an status change

        double[] burstingIntervals = fakeNormalMasqueratorIED.getLegitimateIED().exponentialBackoff(
                (long) fakeNormalMasqueratorIED.getLegitimateIED().getMinTime(),
                fakeNormalMasqueratorIED.getLegitimateIED().getMaxTime(),
                fakeNormalMasqueratorIED.getLegitimateIED().getInitialBackoffInterval());

        for (double interval : burstingIntervals) { // GOOSE BURST MODE
            Goose masqueratedGooseMessage = new Goose(
                    fakeCBStatus, // current status
                    fakeIncreasedStNum, // same stNum
                    fakeIncreasingSqNum++, // increase sqNum
                    timestamp, // current timestamp
                    t, // timestamp of last st change
                    label
            );

            timestamp = timestamp + interval;
            masqueratedGooseMessages.add(masqueratedGooseMessage);
        }
        return masqueratedGooseMessages;
    }


}
