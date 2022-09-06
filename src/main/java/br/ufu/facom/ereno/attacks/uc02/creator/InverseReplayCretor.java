package br.ufu.facom.ereno.attacks.uc02.creator;

import br.ufu.facom.ereno.Util;
import br.ufu.facom.ereno.benign.creator.MessageCreator;
import br.ufu.facom.ereno.benign.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.devices.IED.randomBetween;

public class InverseReplayCretor implements MessageCreator {


    private final int numReplayInstances;
    private final int timeTakenByAttacker = 1;
    private final ArrayList<Goose> legitimateMessages;

    public InverseReplayCretor(int numReplayInstances,
                               ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
        this.numReplayInstances = numReplayInstances;
    }

    @Override
    public void generate(IED ied) {
        for (int replayMessageIndex = 0; replayMessageIndex <= numReplayInstances; replayMessageIndex++) {

            // Step 1 - Pickups one old GOOSE randomly
            int randomIndex = randomBetween(0, legitimateMessages.size());
            Goose replayMessage = legitimateMessages.get(randomIndex);
            replayMessage.label = Util.label[2];

            // Wait until the status changes
            for (int nextLegitimateIndex = randomIndex + 1; nextLegitimateIndex < legitimateMessages.size(); nextLegitimateIndex++) {
                if (replayMessage.getCbStatus() != legitimateMessages.get(nextLegitimateIndex).getCbStatus()) {
                    // transmit the malicious message at an inverse status, after the next legitimate message
                    Goose nextLegitimateGoose = legitimateMessages.get(nextLegitimateIndex);
                    replayMessage.setTimestamp(nextLegitimateGoose.getTimestamp() + timeTakenByAttacker);
                    ied.addMessage(replayMessage);
                }
            }
        }
    }
}
