/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.benign.uc00.devices;

import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author silvio
 */
public abstract class IED {
    protected float initialTimestamp;
    protected MessageCreator messageCreator;

    public void enableRandomOffsets(int max) {
        this.initialTimestamp = randomBetween(0, max);
    }

    public static int randomBetween(int lowerLimit, int upperLimit) {
        if (lowerLimit >= upperLimit) {
            throw new IllegalArgumentException("The lower limit (" + lowerLimit + ") must be less than the upper limit (" + upperLimit + ").");
        }

        return new Random(System.nanoTime()).nextInt(upperLimit - lowerLimit) + lowerLimit;
    }


    public static double randomBetween(double lowerLimit, double upperLimit) {
        if (lowerLimit >= upperLimit) {
            throw new IllegalArgumentException("The lower limit (" + lowerLimit + ") must be less than the upper limit (" + upperLimit + ").");
        }

        Random random = new Random();
        double randomNumber = lowerLimit + (upperLimit - lowerLimit) * random.nextDouble();
        return randomNumber;
    }

    abstract public void run(int messageCount);

    public abstract void addMessage(EthernetFrame message);

    public float getInitialTimestamp() {
        return initialTimestamp;
    }

    public void setInitialTimestamp(float initialTimestamp) {
        this.initialTimestamp = initialTimestamp;
    }
}
