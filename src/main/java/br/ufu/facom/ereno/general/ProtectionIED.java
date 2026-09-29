package br.ufu.facom.ereno.general;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.benign.uc00.creator.GooseCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Common state and timing behavior for protection IEDs and attack IEDs.
 */
public class ProtectionIED extends IED {
    private int initialStNum;
    private int initialSqNum;
    private double delayFromEvent = 0.00631;
    private double firstGooseTime = 0.01659;
    private double initialBackoffInterval = 6.33000000000011;
    private double minTime;
    private long maxTime;
    private boolean initialCbStatus;
    private String label;

    protected ArrayList<Goose> messages = new ArrayList<>();

    public ProtectionIED(String label) {
        this.label = label;
        this.initialStNum = parseInt(SetupIED.ECF.stNum, 1);
        this.initialSqNum = parseInt(SetupIED.ECF.sqNum, 1);
        this.minTime = parseDouble(SetupIED.ECF.minTime, 4.0);
        this.maxTime = Math.round(parseDouble(SetupIED.ECF.maxTime, 1000.0));
        this.initialCbStatus = GooseFlow.ECF.cbstatus;
    }

    private static int parseInt(String value, int fallback) {
        try {
            return value == null ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return value == null ? fallback : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    @Override
    public void enableRandomOffsets(int max) {
        super.enableRandomOffsets(max);
        this.initialStNum = randomBetween(0, max);
        this.initialSqNum = randomBetween(0, max);
    }

    @Override
    public void run(int numberOfMessages) {
        messageCreator = new GooseCreator(label);
        messageCreator.generate(this, numberOfMessages);
    }

    @Override
    public void addMessage(EthernetFrame message) {
        messages.add((Goose) message);
    }

    /**
     * IEC 61850-style retransmission intervals in seconds. Inputs are in ms.
     * The first retransmission interval is intentionally repeated, matching the
     * original ERENO behavior, but every returned value uses the same unit.
     */
    public double[] exponentialBackoff(long minTimeMs, long maxTimeMs, double multiplier) {
        if (minTimeMs <= 0 || maxTimeMs <= 0 || minTimeMs > maxTimeMs) {
            throw new IllegalArgumentException("Invalid GOOSE retransmission bounds: min=" + minTimeMs + ", max=" + maxTimeMs);
        }
        if (multiplier <= 1.0) {
            throw new IllegalArgumentException("Backoff multiplier must be > 1.0");
        }

        ArrayList<Double> intervals = new ArrayList<>();
        double firstSeconds = minTimeMs / 1000.0;
        intervals.add(firstSeconds);
        intervals.add(firstSeconds);

        long currentMs = minTimeMs;
        while (currentMs < maxTimeMs) {
            long nextMs = Math.max(currentMs + 1L, Math.round(currentMs * multiplier));
            if (nextMs >= maxTimeMs) {
                intervals.add(maxTimeMs / 1000.0);
                break;
            }
            intervals.add(nextMs / 1000.0);
            currentMs = nextMs;
        }

        double[] result = new double[intervals.size()];
        for (int i = 0; i < intervals.size(); i++) {
            result[i] = intervals.get(i);
        }
        return result;
    }

    public ArrayList<Goose> copyMessages() {
        ArrayList<Goose> copied = new ArrayList<>(messages.size());
        for (Goose original : messages) {
            copied.add(original.copy());
        }
        return copied;
    }

    public ArrayList<Goose> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Goose> gooseMessages) {
        this.messages = gooseMessages;
    }

    /** Strict predecessor: never returns the frame at the queried timestamp. */
    public Goose getLastGooseBeforeTime(double timestamp) {
        Goose best = null;
        for (Goose goose : messages) {
            if (goose.getTimestamp() < timestamp
                    && (best == null || goose.getTimestamp() > best.getTimestamp())) {
                best = goose;
            }
        }
        return best;
    }

    /**
     * Backward-compatible name. The semantics are intentionally strict (<),
     * eliminating the historical self-comparison bug in attack feature extraction.
     */
    public Goose getLastGooseFromTime(double timestamp) {
        return getLastGooseBeforeTime(timestamp);
    }

    public Goose getLastGooseFromSV(double timestamp, ArrayList<Goose> gooseMessages) {
        Goose best = null;
        for (Goose goose : gooseMessages) {
            if (goose.getTimestamp() < timestamp
                    && (best == null || goose.getTimestamp() > best.getTimestamp())) {
                best = goose;
            }
        }
        return best;
    }

    public Goose getPreviousGoose(Goose goose, ProtectionIED ied) {
        ArrayList<Goose> sorted = ied.copyMessages();
        sorted.sort(Comparator.comparingDouble(Goose::getTimestamp));
        Goose previous = null;
        for (Goose current : sorted) {
            if (current.getTimestamp() >= goose.getTimestamp()) {
                return previous;
            }
            previous = current;
        }
        return previous;
    }

    public void sortMessagesChronologically() {
        messages.sort(Comparator.comparingDouble(Goose::getTimestamp));
    }

    public int getNumberOfMessages() {
        return messages.size();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getInitialStNum() {
        return initialStNum;
    }

    public void setInitialStNum(int initialStNum) {
        this.initialStNum = initialStNum;
    }

    public int getInitialSqNum() {
        return initialSqNum;
    }

    public void setInitialSqNum(int initialSqNum) {
        this.initialSqNum = initialSqNum;
    }

    public double getDelayFromEvent() {
        return delayFromEvent;
    }

    public void setDelayFromEvent(double delayFromEvent) {
        this.delayFromEvent = delayFromEvent;
    }

    public double getFirstGooseTime() {
        return firstGooseTime;
    }

    public void setFirstGooseTime(double firstGooseTime) {
        this.firstGooseTime = firstGooseTime;
    }

    public double getMinTime() {
        return minTime;
    }

    public void setMinTime(double minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public boolean isInitialCbStatus() {
        return initialCbStatus;
    }

    public void setInitialCbStatus(boolean initialCbStatus) {
        this.initialCbStatus = initialCbStatus;
    }

    public double getInitialBackoffInterval() {
        return initialBackoffInterval;
    }

    public void setInitialBackoffInterval(double initialBackoffInterval) {
        this.initialBackoffInterval = initialBackoffInterval;
    }

    public int toInt(boolean cbStatus) {
        return cbStatus ? 1 : 0;
    }
}
