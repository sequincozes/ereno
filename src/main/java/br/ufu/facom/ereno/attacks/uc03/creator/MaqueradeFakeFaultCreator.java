package br.ufu.facom.ereno.attacks.uc03.creator;

import br.ufu.facom.ereno.utils.Util;
import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

public class MaqueradeFakeFaultCreator implements MessageCreator {

    private final int timeTakenByAttacker = 1;
    private final ArrayList<Goose> legitimateMessages;

    public MaqueradeFakeFaultCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;

    }

    @Override
    public void generate(IED ied, int numMasqueradeInstances) {
        for (int masqueradeMessageIndex = 0; masqueradeMessageIndex <= numMasqueradeInstances; masqueradeMessageIndex++) {

            // Step 1 - Monitors the network until finding a GOOSE with CBStatus = 0
            Goose lastLegitimateGoose = null;
            boolean foundNormalCBStatus = false;
            while (!foundNormalCBStatus) {
                int lastGoose = randomBetween(0, legitimateMessages.size());
                lastLegitimateGoose = legitimateMessages.get(lastGoose);
                if (lastLegitimateGoose.getCbStatus() == 0) {
                    foundNormalCBStatus = true;
                }
            }

            // Step 2 - Reports a fake fault (CBStatus = 1)
            ArrayList<Goose> gooseMessages = reportFakeEventAt((FakeFaultMasqueratorIED) ied, lastLegitimateGoose);
            for (Goose masquerade : gooseMessages) {
                ied.addMessage(masquerade);
            }

            if (((FakeFaultMasqueratorIED) ied).getNumberOfMessages() >= 1000) {
                break;
            }
        }

    }

    public ArrayList<Goose> reportFakeEventAt(FakeFaultMasqueratorIED fakeFaultMasqueratorIED, Goose lastLegitimateMessage) {
        ArrayList<Goose> masqueratedGooseMessages = new ArrayList<>();
        double fakeEventTimestamp = lastLegitimateMessage.getTimestamp() + timeTakenByAttacker; // the masquerade messages will be transmitted immediately after this message
        String label = Util.label[3];  // label it as masquerade fake fault (uc03)
        int fakeCBStatus = 1;
        int fakeIncreasedStNum = lastLegitimateMessage.getStNum() + 1;
        int fakeIncreasingSqNum = 1;
        double t = fakeEventTimestamp + ProtectionIED.delayFromEvent; // new t
        double timestamp = t; // timestamp has the same value as the last change (t) because it simulates an status change

        double[] burstingIntervals = fakeFaultMasqueratorIED.getLegitimateIED().exponentialBackoff(
                (long) fakeFaultMasqueratorIED.getLegitimateIED().getMinTime(),
                fakeFaultMasqueratorIED.getLegitimateIED().getMaxTime(),
                fakeFaultMasqueratorIED.getLegitimateIED().getInitialBackoffInterval());
        
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
