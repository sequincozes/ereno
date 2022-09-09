/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.benign.uc00.devices;


import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.benign.uc00.creator.GooseCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

/**
 * @author silvio
 */
public class ProtectionIED extends IED {

    private int initialStNum = Integer.parseInt(SetupIED.ECF.stNum);
    private int initialSqNum = Integer.parseInt(SetupIED.ECF.sqNum);
    //    static double[] burstingInterval = {0.5, 0.6}; // timestam to p (in seconds)
    public static double delayFromEvent = 0.00631;
    double firstGooseTime = 0.01659;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    double initialBackoffInterval = 6.33000000000011f; // IED processing time
    double minTime = Integer.parseInt(SetupIED.ECF.minTime);
    public static long maxTime = Integer.parseInt(SetupIED.ECF.maxTime);
    private boolean initialCbStatus = GooseFlow.ECF.cbstatus;

    protected ArrayList<Goose> messages;
    private String label = "normal";

    public ProtectionIED() {
        this.messages = new ArrayList<>();
    }

    @Override
    public void enableRandomOffsets(int max) {
        super.enableRandomOffsets(max);
        this.initialStNum = randomBetween(0, max);
        this.initialSqNum = randomBetween(0, max);
    }

    @Override
    public void run(int numberOfPeriodicMessages) {
//        numberOfPeriodicMessages = 3;
        double firstEvent = initialTimestamp + numberOfPeriodicMessages - 1 + 0.5;
        double secondEvent = initialTimestamp + numberOfPeriodicMessages - 1 + 0.6;

        // Here we set the GooseCreator for creating GOOSE messages for ProtectionIED
        messageCreator = new GooseCreator(label);
        messageCreator.generate(this, numberOfPeriodicMessages);
        GooseCreator gc = (GooseCreator) messageCreator;
        gc.reportEventAt(firstEvent);
        gc.reportEventAt(secondEvent);
//        messageCreator.generate(this, numberOfPeriodicMessages);

    }

    @Override
    public void addMessage(EthernetFrame periodicGoose) {
        this.messages.add((Goose) periodicGoose);
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

    public ArrayList<Goose> getMessages() {
        return this.messages;
    }

    public Goose getPreviousGoose(Goose goose, ArrayList<Goose> gooseMessages) {
        for (int i = 0; i < gooseMessages.size(); i++) {
            if (goose.equals(gooseMessages.get(i))) {
                if (i == 0) {
                    Goose pseudoPast = gooseMessages.get(0).copy(); // Pseudo past
                    double pseudoPastTimestamp = gooseMessages.get(0).getTimestamp() - ProtectionIED.maxTime;
                    pseudoPast.setTimestamp(pseudoPastTimestamp); //Assume the last message wast sent at now - maxtime
                    pseudoPast.setSqNum(pseudoPast.getSqNum() - 1);
                    return pseudoPast;
                } else {
                    return gooseMessages.get(i - 1);
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

    public double getInitialBackoffInterval() {
        return initialBackoffInterval;
    }

    public void setInitialBackoffInterval(double initialBackoffInterval) {
        this.initialBackoffInterval = initialBackoffInterval;
    }


    public int getNumberOfMessages() {
        return getMessages().size();
    }
}
