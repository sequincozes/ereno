package br.ufu.facom.ereno.general;

import br.ufu.facom.ereno.SubstationNetwork;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;

import java.util.Random;
import java.util.logging.Logger;

/** Base class for ERENO IEDs. */
public abstract class IED {
    protected SubstationNetwork substationNetwork;
    protected float initialTimestamp;
    protected MessageCreator messageCreator;

    private static Random random = new Random();

    /** Allows reproducible generator runs and leakage audits. */
    public static synchronized void setRandomSeed(long seed) {
        random = new Random(seed);
    }

    public void enableRandomOffsets(int max) {
        this.initialTimestamp = randomBetween(0, max);
        Logger.getLogger("enableRandomOffsets(" + max + ")").info("Random offset: " + initialTimestamp);
    }

    public SubstationNetwork getSubstationNetwork() {
        return substationNetwork;
    }

    public void setSubstationNetwork(SubstationNetwork substationNetwork) {
        this.substationNetwork = substationNetwork;
    }

    /** Inclusive integer range. Equal bounds are valid and deterministic. */
    public static synchronized int randomBetween(int lowerLimit, int upperLimit) {
        if (lowerLimit > upperLimit) {
            throw new IllegalArgumentException("The lower limit (" + lowerLimit + ") must not exceed the upper limit (" + upperLimit + ").");
        }
        if (lowerLimit == upperLimit) {
            return lowerLimit;
        }
        return lowerLimit + random.nextInt(upperLimit - lowerLimit + 1);
    }

    /** Half-open-style continuous draw on [lowerLimit, upperLimit). */
    public static synchronized double randomBetween(double lowerLimit, double upperLimit) {
        if (lowerLimit > upperLimit) {
            throw new IllegalArgumentException("The lower limit (" + lowerLimit + ") must not exceed the upper limit (" + upperLimit + ").");
        }
        if (Double.compare(lowerLimit, upperLimit) == 0) {
            return lowerLimit;
        }
        return lowerLimit + (upperLimit - lowerLimit) * random.nextDouble();
    }

    public abstract void run(int messageCount);

    public abstract void addMessage(EthernetFrame message);

    public float getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(float initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }
}
