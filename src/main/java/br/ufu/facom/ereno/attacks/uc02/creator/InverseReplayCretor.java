package br.ufu.facom.ereno.attacks.uc02.creator;

import br.ufu.facom.ereno.utils.Util;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

public class InverseReplayCretor implements MessageCreator {


    private final int timeTakenByAttacker = 1;
    private final ArrayList<Goose> legitimateMessages;

    public InverseReplayCretor(                               ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int numReplayInstances) {
        for (int replayMessageIndex = 0; replayMessageIndex <= numReplayInstances; replayMessageIndex++) {

            // Step 1 - Pickups one old GOOSE randomly
            int randomIndex = randomBetween(0, legitimateMessages.size());
            Goose replayMessage = legitimateMessages.get(randomIndex);
            replayMessage.label = Util.label[2];  // label it as inverse replay (uc02)

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
