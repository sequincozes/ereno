package br.ufu.facom.ereno.general;

import br.ufu.facom.ereno.SubstationNetwork;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Abstract base class for Intelligent Electronic Devices (IED) within a substation network.
 *
 * Provides core properties and utility methods for IEDs, including:
 * - Association with a {@link SubstationNetwork}
 * - Initial timestamp offset management (with optional randomization)
 * - Handling of message creation via {@link MessageCreator}
 *
 * Subclasses must implement lifecycle methods for running simulations,
 * as well as adding and removing Ethernet frames (messages).
 */

public abstract class IED {
    protected SubstationNetwork substationNetwork;
    protected float initialTimestamp;
    protected MessageCreator messageCreator;

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

    public static int randomBetween(int lowerLimit, int upperLimit) {
        if (lowerLimit >= upperLimit) {
            throw new IllegalArgumentException("The lower limit (" + lowerLimit + ") must be less than the upper limit (" + upperLimit + ").");
        }

        Random random = new Random(System.nanoTime());

        return lowerLimit + random.nextInt(upperLimit - lowerLimit + 1);
    }


    public static double randomBetween(double lowerLimit, double upperLimit) {
        if (lowerLimit >= upperLimit) {
            throw new IllegalArgumentException("The lower limit (" + lowerLimit + ") must be less than the upper limit (" + upperLimit + ").");
        }

        Random random = new Random(System.nanoTime());
        return lowerLimit + (upperLimit - lowerLimit) * random.nextDouble();
    }

    abstract public void run(int messageCount);

    public abstract void addMessage(EthernetFrame message);

    public abstract void removeMessage(EthernetFrame message);

    public float getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(float initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }

}
