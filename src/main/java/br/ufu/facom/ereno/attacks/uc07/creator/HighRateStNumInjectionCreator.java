package br.ufu.facom.ereno.attacks.uc07.creator;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;

/** UC07: approximately 1,000 forged state-number updates per second. */
public class HighRateStNumInjectionCreator implements MessageCreator {
    private static final double TARGET_RATE_HZ = 1000.0;
    private static final double BASE_INTERVAL_SECONDS = 1.0 / TARGET_RATE_HZ;

    private final ArrayList<Goose> legitimateMessages;

    public HighRateStNumInjectionCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = deepSortedCopy(legitimateMessages);
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {
        if (numberOfMessages <= 0) {
            return;
        }
        if (legitimateMessages.isEmpty()) {
            throw new IllegalArgumentException("UC07 requires legitimate GOOSE context.");
        }

        double attackDuration = Math.max(0.0, (numberOfMessages - 1) * BASE_INTERVAL_SECONDS);
        double firstTime = legitimateMessages.get(0).getTimestamp();
        double lastTime = legitimateMessages.get(legitimateMessages.size() - 1).getTimestamp();
        double latestSafeStart = Math.max(firstTime, lastTime - attackDuration);
        double startTime = latestSafeStart > firstTime
                ? IED.randomBetween(firstTime, latestSafeStart)
                : firstTime;

        Goose template = lastLegitimateAtOrBefore(startTime);
        ProtectionIED protectionIED = (ProtectionIED) ied;
        double processingDelay = Math.max(0.0, protectionIED.getDelayFromEvent());
        int stNum = legitimateMessages.stream().mapToInt(Goose::getStNum).max().orElse(template.getStNum()) + 1;
        double eventTime = startTime;

        for (int i = 0; i < numberOfMessages; i++) {
            if (i > 0) {
                // Small jitter avoids a synthetic perfectly periodic fingerprint while
                // retaining an average rate close to 1,000 messages/s.
                double jitterFactor = IED.randomBetween(0.90, 1.10);
                eventTime += BASE_INTERVAL_SECONDS * jitterFactor;
            }

            Goose attack = template.copy();
            attack.setLabel(DatasetWriter.label[7]);
            attack.setT(eventTime);
            attack.setTimestamp(eventTime + processingDelay);
            attack.setStNum(stNum++);
            attack.setSqNum(1);
            ied.addMessage(attack);
        }
    }

    private Goose lastLegitimateAtOrBefore(double timestamp) {
        Goose best = legitimateMessages.get(0);
        for (Goose goose : legitimateMessages) {
            if (goose.getTimestamp() <= timestamp) {
                best = goose;
            } else {
                break;
            }
        }
        return best;
    }

    private static ArrayList<Goose> deepSortedCopy(ArrayList<Goose> source) {
        ArrayList<Goose> copy = new ArrayList<>(source.size());
        for (Goose goose : source) {
            copy.add(goose.copy());
        }
        copy.sort(Comparator.comparingDouble(Goose::getTimestamp));
        return copy;
    }
}
