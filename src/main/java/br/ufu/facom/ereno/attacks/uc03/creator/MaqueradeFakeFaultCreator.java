package br.ufu.facom.ereno.attacks.uc03.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;
import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

public class MaqueradeFakeFaultCreator implements MessageCreator {

    private float timeTakenByAttacker = 1;
    private final ArrayList<Goose> legitimateMessages;

    public MaqueradeFakeFaultCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;

    }

    @Override
    public void generate(IED ied, int numMasqueradeInstances) {

        // Step 1 - Monitors the network until finding a GOOSE with CBStatus = 0
        System.out.println("numMasqueradeInstances=" + numMasqueradeInstances + " < " + ((FakeFaultMasqueratorIED) ied).getNumberOfMessages());
        while (numMasqueradeInstances > ((FakeFaultMasqueratorIED) ied).getNumberOfMessages()) {
            System.out.println("numMasqueradeInstances=" + numMasqueradeInstances + " < " + ((FakeFaultMasqueratorIED) ied).getNumberOfMessages());
            int randomIndex = randomBetween(0, legitimateMessages.size());
            Goose previousLegitimate = legitimateMessages.get(randomIndex);
            if (previousLegitimate.getCbStatus() == 0) {
                System.out.println("Caiu no IF!");
                // Step 2 - Reports a fake fault (CBStatus = 1)
                ArrayList<Goose> gooseMessages = reportFakeEventAt((FakeFaultMasqueratorIED) ied, previousLegitimate);
                for (Goose masquerade : gooseMessages) {
                    System.out.println("Caiu no FOR!");
                    // Randomize the time taken by an attacker
                    timeTakenByAttacker = (float) (randomBetween(100F, 10000F) / 1000);
                    masquerade.setTimestamp(previousLegitimate.getTimestamp() + timeTakenByAttacker);
                    ied.addMessage(masquerade);
                }
            }
        }
    }

    public ArrayList<Goose> reportFakeEventAt(FakeFaultMasqueratorIED fakeFaultMasqueratorIED, Goose lastLegitimateMessage) {
        ArrayList<Goose> masqueratedGooseMessages = new ArrayList<>();
        double fakeEventTimestamp = lastLegitimateMessage.getTimestamp() + timeTakenByAttacker; // the masquerade messages will be transmitted immediately after this message
        String label = GSVDatasetWritter.label[3];  // label it as masquerade fake fault (uc03)
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
