/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc04.devices;


import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc04.creator.MasqueradeFakeNornalCreator;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author silvio
 */
public class MasqueradeFakeNormalED extends ProtectionIED {
//    LegitimateProtectionIED victimIED;
    private Goose seedMessage;

    @Deprecated
    public MasqueradeFakeNormalED(LegitimateProtectionIED legitimateProtectionIED) {
        super(DatasetWriter.label[4]);
        messages = new ArrayList<>();
//        this.victimIED = legitimateProtectionIED;
        throw new IllegalArgumentException("This method is being deprecated");
    }

    public MasqueradeFakeNormalED() {
        super(DatasetWriter.label[4]);
        messages = new ArrayList<>();
    }


    private int initialStNum = Integer.parseInt(SetupIED.stNum);
    private int initialSqNum = Integer.parseInt(SetupIED.sqNum);
    //    static double[] burstingInterval = {0.5, 0.6}; // timestam to p (in seconds)
    public static double delayFromEvent = 0.00631;
    double firstGooseTime = 0.01659;

    double initialBackoffInterval = 6.33000000000011f; // IED processing time
    double minTime = Integer.parseInt(SetupIED.minTime);
    long maxTime = Integer.parseInt(SetupIED.maxTime);
    private boolean initialCbStatus = GooseFlow.cbstatus;

    protected ArrayList<Goose> messages;

    @Override
    public void enableRandomOffsets(int max) {
        super.enableRandomOffsets(max);
        this.initialStNum = randomBetween(0, max);
        this.initialSqNum = randomBetween(0, max);
    }

    @Override
    public void run(int numberOfMessages) {
        // Here we set the GooseCreator for creating GOOSE messages for ProtectionIED
        messageCreator = new MasqueradeFakeNornalCreator(getLabel());
        if (this.substationNetwork.stationBusMessages.size() == 0) {
            throw new IllegalArgumentException("Cannot run Masquerade attacks with zero messages. Please ensure there are at least one normal message sent previously.");
        }
        messageCreator.generate(this, numberOfMessages);
    }

    @Override
    public void addMessage(EthernetFrame periodicGoose) {
        if (GooseFlow.numberOfMessages >= messages.size()) {
            this.messages.add((Goose) periodicGoose);
        } else {
            Logger.getLogger("addMessage").warning("Adding more GOOSE than the predefined threshold. There is something wrong with your logic.");
        }
    }


    public double[] exponentialBackoff(long minTime, long maxTime, double intervalMultiplier) {
        long retryIntervalMs = minTime;

        ArrayList<Double> tIntervals = new ArrayList<>();
        do {
            tIntervals.add(retryIntervalMs / 1000.0);
            retryIntervalMs *= intervalMultiplier;
            if (retryIntervalMs > maxTime) {
                tIntervals.add((double) maxTime);
                break;
            } else if (retryIntervalMs == maxTime) {
                tIntervals.add(retryIntervalMs / 1000.0);
                break;
            }

        } while (retryIntervalMs <= maxTime);

        int i = 0;
        double[] arrayIntervals = new double[tIntervals.size() + 1];
        arrayIntervals[i++] = tIntervals.get(0); // first two retransmission are on same period
        for (double ti : tIntervals) {
            arrayIntervals[i++] = ti;
        }

        return arrayIntervals;
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

    public void setMessages(ArrayList<Goose> gooseMessages) {
        this.messages = gooseMessages;
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

    float restartCounters(boolean random) {
        if (random) {
            initialTimestamp = randomBetween(0, 5000); // offset
            initialStNum = randomBetween(0, 5000);
            initialSqNum = randomBetween(0, 5000);
        } else {
            initialTimestamp = 0;
            initialStNum = 0;
            initialSqNum = 0;
        }
        return initialTimestamp;
    }

    float defineCounters(int initialStNum, int initialSqNum, float offset) {
        this.initialStNum = initialStNum;
        this.initialSqNum = initialSqNum;
        this.initialTimestamp = offset;
        return offset;
    }

    public boolean isInitialCbStatus() {
        return initialCbStatus;
    }

    public void setInitialCbStatus(boolean initialCbStatus) {
        this.initialCbStatus = initialCbStatus;
    }

    public int toInt(boolean cbStatus) {
        if (!cbStatus) {
            return 0;
        } else {
            return 1;
        }

    }

    public ArrayList<Goose> copyMessages() {
        Logger.getLogger("copyMessage").info("Copying " + messages.size() + " messages.");
        ArrayList<Goose> copied = new ArrayList<>();
        for (Goose originalGoose : messages) {
            copied.add(originalGoose.copy());
        }

        if (messages.equals(copied)) {
            throw new IllegalArgumentException("Messages are not copied! Message 0 timestamp (messages):" + messages + " == (copied):" + copied);
        }
        return copied;
    }

    public ArrayList<Goose> getMessages() {
        return this.messages;
    }

    public Goose getPreviousGoose(Goose goose, MasqueradeFakeNormalED ied) {
        for (int i = 0; i < ied.getMessages().size(); i++) {
            if (goose.equals(ied.getMessages().get(i))) {
                if (i == 0) {
                    Goose pseudoPast = ied.getMessages().get(0).copy(); // Pseudo past
                    double pseudoPastTimestamp = ied.getMessages().get(0).getTimestamp() - maxTime;
                    pseudoPast.setTimestamp(pseudoPastTimestamp); //Assume the last message wast sent at now - maxtime
                    pseudoPast.setSqNum(pseudoPast.getSqNum() - 1);
                    return pseudoPast;
                } else {
                    return ied.getMessages().get(i - 1);
                }
            }
        }
        return null;
    }

    public Goose getLastGooseFromSV(double timestamp, ArrayList<Goose> gooseMessages) {
        Goose lastGooseMessage = gooseMessages.get(0);
        for (Goose goose : gooseMessages) {
            if (goose.getTimestamp() > timestamp) {
                return lastGooseMessage;
            } else {
                lastGooseMessage = goose;
            }
        }
        return lastGooseMessage;
    }

    public Goose getLastGooseFromTime(double timestamp) {
        Goose lastGooseMessage = getMessages().get(0);
        for (Goose goose : getMessages()) {
            if (goose.getTimestamp() > timestamp) {
                return lastGooseMessage;
            } else {
                lastGooseMessage = goose;
            }
        }
        return lastGooseMessage;
    }

    public double getInitialBackoffInterval() {
        return initialBackoffInterval;
    }

    public void setInitialBackoffInterval(double initialBackoffInterval) {
        this.initialBackoffInterval = initialBackoffInterval;
    }

    public int getNumberOfMessages() {
        return messages.size();
    }


//    public LegitimateProtectionIED getVictimIED() {
//        return this.victimIED;
//    }

    public Goose getSeedMessage() {
        return this.seedMessage;
    }

    public void setSeed(Goose randomFaultySeed) {
        this.seedMessage = randomFaultySeed;
    }
}
