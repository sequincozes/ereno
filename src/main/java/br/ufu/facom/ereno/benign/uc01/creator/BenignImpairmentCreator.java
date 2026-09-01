package br.ufu.facom.ereno.benign.uc01.creator;

import br.ufu.facom.ereno.api.RunContext;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.general.IED.randomBetween;

/**
 * Implements the seven non-malicious impairment mechanisms from the major-
 * revision card C ({@code experiments/revision_2026/benign_controls.md} §3):
 * paired, non-malicious packet loss/jitter/delay/duplication/reordering
 * controls, so a classifier cannot learn "gap in GOOSE traffic" as a proxy
 * for "malicious gap in GOOSE traffic".
 *
 * <p>Exactly one mode runs per generation, selected by
 * {@link RunContext#impairmentMode}. Every affected row is labeled
 * {@link #BENIGN_DEGRADATION_LABEL} - never a per-mode label, unlike the
 * attack creators - because {@code impairment_mode} is a run-level
 * {@link RunContext} column (same precedent as {@code attack_variant}) that
 * carries the mechanism for reporting instead.</p>
 *
 * @see RunContext.Impairment
 */
public class BenignImpairmentCreator implements MessageCreator {

    /** The single model-facing class produced by every mechanism here (card C §2). */
    public static final String BENIGN_DEGRADATION_LABEL = "benign_degradation";

    /**
     * Fixed offset applied to a duplicate's timestamp. Far below the default
     * inter-message gap ({@code goose.timing.minTime=100ms}) so a duplicate
     * cannot leapfrog the next legitimate message under default timing.
     */
    private static final double DUPLICATE_OFFSET_SECONDS = 0.001;

    private final ArrayList<Goose> legitimateMessages;

    /**
     * @param legitimateMessages previously generated legitimate messages, in
     *                            chronological order, to replay through the
     *                            selected impairment mechanism.
     */
    public BenignImpairmentCreator(ArrayList<Goose> legitimateMessages) {
        RunContext.requireLoaded();
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int numberOfMessages) {
        int n = Math.min(numberOfMessages, legitimateMessages.size());
        switch (RunContext.impairmentMode) {
            case CONGESTION_LOSS:
                generateCongestionLoss(ied, n);
                break;
            case QUEUE_OVERLOAD_BURST:
                generateQueueOverloadBurst(ied, n);
                break;
            case LINK_FLAP:
                generateLinkFlap(ied, n);
                break;
            case JITTER:
                generateJitter(ied, n);
                break;
            case DELAY:
                generateDelay(ied, n);
                break;
            case DUPLICATION:
                generateDuplication(ied, n);
                break;
            case REORDERING:
                generateReordering(ied, n);
                break;
            case NONE:
            default:
                throw new IllegalStateException(
                        "BenignImpairmentCreator requires RunContext.impairmentMode != NONE "
                                + "(set attack.benignImpairment.mode in params.properties).");
        }
    }

    /**
     * Tier 1. Bernoulli draw per message, independent of protocol state -
     * unlike every {@code OrientedGrayHoleCreator} variant, which keys off
     * StNum changes. Falsifies "isolated random loss ⇒ FRG"; paired with FRG
     * by {@code loss_rate}.
     */
    private void generateCongestionLoss(IED ied, int numberOfMessages) {
        boolean previousDropped = false;
        for (int i = 0; i < numberOfMessages; i++) {
            Goose message = legitimateMessages.get(i);
            if (randomBetween(0, 100) < RunContext.impairmentRate) {
                previousDropped = true;
                continue;
            }
            if (previousDropped) {
                message.setLabel(BENIGN_DEGRADATION_LABEL);
                previousDropped = false;
            }
            ied.addMessage(message);
        }
    }

    /**
     * Tier 1. Bernoulli draw triggers a burst of {@code impairmentBurst}
     * consecutive drops, not aligned to a state change - unlike SAG.PB
     * (RANDOMIC_BURST), which only triggers on a StNum change. Falsifies
     * "burst loss ⇒ SAG.PB"; paired with SAG.PB by {@code loss_rate}/{@code burst_size}.
     */
    private void generateQueueOverloadBurst(IED ied, int numberOfMessages) {
        int i = 0;
        while (i < numberOfMessages) {
            Goose message = legitimateMessages.get(i);
            if (randomBetween(0, 100) < RunContext.impairmentRate) {
                int burstEnd = Math.min(i + RunContext.impairmentBurst, numberOfMessages);
                i = burstEnd;
                if (i < numberOfMessages) {
                    legitimateMessages.get(i).setLabel(BENIGN_DEGRADATION_LABEL);
                }
                continue;
            }
            ied.addMessage(message);
            i++;
        }
    }

    /**
     * Tier 2. Deterministic, periodic outage window: {@code impairmentBurst}
     * messages dropped every {@code impairmentPeriod} messages. Unlike
     * QUEUE_OVERLOAD_BURST this never draws randomly, matching SAG.DB's
     * unconditional drop-on-trigger. Falsifies "deterministic burst ⇒ SAG.DB";
     * paired with SAG.DB by {@code burst_size}.
     */
    private void generateLinkFlap(IED ied, int numberOfMessages) {
        int i = 0;
        while (i < numberOfMessages) {
            boolean triggered = i > 0 && i % RunContext.impairmentPeriod == 0;
            if (triggered) {
                int burstEnd = Math.min(i + RunContext.impairmentBurst, numberOfMessages);
                i = burstEnd;
                if (i < numberOfMessages) {
                    legitimateMessages.get(i).setLabel(BENIGN_DEGRADATION_LABEL);
                }
                continue;
            }
            ied.addMessage(legitimateMessages.get(i));
            i++;
        }
    }

    /**
     * Tier 1. No loss: perturbs every message's {@code timestamp} by up to
     * ±{@code impairmentJitterMs}, leaving {@code t} (the event time key)
     * untouched. Falsifies "timing variance ⇒ attack"; negative control, no
     * attack pairing.
     */
    private void generateJitter(IED ied, int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            Goose message = legitimateMessages.get(i);
            double jitterSeconds = randomBetween(-RunContext.impairmentJitterMs, RunContext.impairmentJitterMs) / 1000.0;
            message.setTimestamp(message.getTimestamp() + jitterSeconds);
            message.setLabel(BENIGN_DEGRADATION_LABEL);
            ied.addMessage(message);
        }
    }

    /**
     * Tier 1. No loss: adds a one-directional queueing delay of up to
     * {@code impairmentDelayMs} to every message's {@code timestamp}.
     * Falsifies "delayed delivery ⇒ attack"; negative control, no attack pairing.
     */
    private void generateDelay(IED ied, int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            Goose message = legitimateMessages.get(i);
            double delaySeconds = randomBetween(0, RunContext.impairmentDelayMs) / 1000.0;
            message.setTimestamp(message.getTimestamp() + delaySeconds);
            message.setLabel(BENIGN_DEGRADATION_LABEL);
            ied.addMessage(message);
        }
    }

    /**
     * Tier 2. Reinserts a copy of a message (same StNum/SqNum/t, later
     * timestamp). The original keeps whatever label it already had; only the
     * reinserted copy is labeled, per the "reinserted copy" convention.
     * Falsifies "anomalous sqDiff ⇒ attack"; no attack pairing.
     */
    private void generateDuplication(IED ied, int numberOfMessages) {
        for (int i = 0; i < numberOfMessages; i++) {
            Goose message = legitimateMessages.get(i);
            ied.addMessage(message);
            if (randomBetween(0, 100) < RunContext.impairmentRate) {
                Goose duplicate = message.copy();
                duplicate.setTimestamp(message.getTimestamp() + DUPLICATE_OFFSET_SECONDS);
                duplicate.setLabel(BENIGN_DEGRADATION_LABEL);
                ied.addMessage(duplicate);
            }
        }
    }

    /**
     * Tier 2. Swaps the timestamp of two adjacent messages, so the second one
     * - still carrying the later StNum/SqNum - arrives with the earlier
     * timestamp. That message is "the one that ends up out of order" and gets
     * labeled. Falsifies "broken order ⇒ attack"; no attack pairing.
     */
    private void generateReordering(IED ied, int numberOfMessages) {
        int i = 0;
        while (i < numberOfMessages) {
            if (i + 1 < numberOfMessages && randomBetween(0, 100) < RunContext.impairmentRate) {
                Goose first = legitimateMessages.get(i);
                Goose second = legitimateMessages.get(i + 1);
                double firstTimestamp = first.getTimestamp();
                double secondTimestamp = second.getTimestamp();
                first.setTimestamp(secondTimestamp);
                second.setTimestamp(firstTimestamp);
                second.setLabel(BENIGN_DEGRADATION_LABEL);
                ied.addMessage(first);
                ied.addMessage(second);
                i += 2;
            } else {
                ied.addMessage(legitimateMessages.get(i));
                i++;
            }
        }
    }
}
