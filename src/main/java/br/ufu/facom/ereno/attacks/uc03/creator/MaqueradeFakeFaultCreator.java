package br.ufu.facom.ereno.attacks.uc03.creator;

import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

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
            // Take a message as a reference
            int randomIndex = 0;

            // This IF balances between lower and higher indexes
            if (((FakeFaultMasqueratorIED) ied).getNumberOfMessages() % 2 == 0) {
                randomIndex = randomBetween(0, (legitimateMessages.size() - 1) / 2);
            } else {
                randomIndex = randomBetween((legitimateMessages.size() - 1) / 2, legitimateMessages.size() - 1);
            }
            Goose previousLegitimate = legitimateMessages.get(randomIndex);
            System.out.println("Sorteio:" + "Indice(" + randomIndex + ") = Timestamp("
                    + previousLegitimate.getTimestamp() + "), (StNum: " + previousLegitimate.getStNum() + "), (SqNum: " + previousLegitimate.getSqNum() + ")");

            if (previousLegitimate.getCbStatus() == 0) {
                // Step 2 - Reports a fake fault (CBStatus = 1)
                ArrayList<Goose> gooseMessages = reportFakeEventAt((FakeFaultMasqueratorIED) ied, previousLegitimate);
                for (Goose masquerade : gooseMessages) {
                    ied.addMessage(masquerade);
                }

                // Step 3 - Keep sending fake periodic messages
                if (numMasqueradeInstances -
                        ((FakeFaultMasqueratorIED) ied).getNumberOfMessages() > numMasqueradeInstances - 1) {
                    Goose previousFakeMessage = gooseMessages.get(gooseMessages.size() - 1);
                    int numPeriodicMessages = randomBetween(0, numMasqueradeInstances -
                            ((FakeFaultMasqueratorIED) ied).getNumberOfMessages());
                    ArrayList<Goose> periodicMessages = generateFakePeriodicMessages(previousFakeMessage, numPeriodicMessages, (ProtectionIED) ied);
                    for (Goose periodic : periodicMessages) {
                        ied.addMessage(periodic);
                    }
                }
            }


        }
    }

    private double getNetworkDelay() {
        return IED.randomBetween(0.001, 0.031);
    }

    public ArrayList<Goose> generateFakePeriodicMessages(Goose previousGoose, int numPeriodics, ProtectionIED ied) {
        ArrayList<Goose> periodicMsgs = new ArrayList<>();
        for (int i = 0; i < numPeriodics; i++) {
            Goose periodicGoose = new Goose(
                    1,
                    previousGoose.getStNum(),
                    previousGoose.getSqNum() + 1,
                    previousGoose.getTimestamp() + ied.getMaxTime() / 1000 + getNetworkDelay(),
                    previousGoose.getT(),
                    ied.getLabel());
            periodicMsgs.add(periodicGoose);
            previousGoose = periodicGoose.copy();
        }
        return periodicMsgs;
    }

    public ArrayList<Goose> reportFakeEventAt(FakeFaultMasqueratorIED fakeFaultMasqueratorIED, Goose lastLegitimateMessage) {
        ArrayList<Goose> masqueratedGooseMessages = new ArrayList<>();

        // Randomize the time taken by an attacker
        timeTakenByAttacker = (float) (randomBetween(100F, 10000F) / 1000);
        double fakeEventTimestamp = lastLegitimateMessage.getTimestamp() + timeTakenByAttacker; // the masquerade messages will be transmitted immediately after this message
        String label = fakeFaultMasqueratorIED.getLabel();  // label it as masquerade fake fault (uc03)
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
