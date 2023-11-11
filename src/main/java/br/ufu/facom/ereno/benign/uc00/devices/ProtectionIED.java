/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.benign.uc00.devices;


import br.ufu.facom.ereno.MultiSource;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.benign.uc00.creator.GooseCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author silvio
 */
public class ProtectionIED extends IED {
    public ProtectionIED() {
        messages = new ArrayList<>();
    }

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

    @Override
    public void enableRandomOffsets(int max) {
        super.enableRandomOffsets(max);
        this.initialStNum = randomBetween(0, max);
        this.initialSqNum = randomBetween(0, max);
    }

    @Override
    public void run(int numberOfPeriodicMessages) {
        // Here we set the GooseCreator for creating GOOSE messages for ProtectionIED
        messageCreator = new GooseCreator(label);
        messageCreator.generate(this, numberOfPeriodicMessages / 2);
        GooseCreator gc = (GooseCreator) messageCreator;
        double lastPeriodicMessage = copyMessages().get(getNumberOfMessages() - 1).getTimestamp();
        gc.reportEventAt(lastPeriodicMessage + 0.5); // fault at middle of the second
        copyMessages().remove(getNumberOfMessages() - 1); // need to remove the message after 100ms
        gc.reportEventAt(lastPeriodicMessage + 0.6); // fault recovery 100ms later
        messageCreator.generate(this, numberOfPeriodicMessages / 2);
    }

    @Override
    public void addMessage(EthernetFrame periodicGoose) {
//        System.out.println("Adding GOOSE with timestamp " + ((Goose) periodicGoose).getTimestamp() + "(SqNum: " + ((Goose) periodicGoose).getSqNum() + " / StNum: " + ((Goose) periodicGoose).getStNum() + ")");
        if (((Goose) periodicGoose).getTimestamp() < 0) {
            System.out.println("CULPADO: " + MultiSource.run);
            throw new IllegalArgumentException("The GOOSE message has a negative timestamp");
        }
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

    public ArrayList<Goose> copyMessages() {
        Logger.getLogger("copyMessage").info("Copying "+messages.size() + " messages.");
        ArrayList<Goose> copied = new ArrayList<>();
        for (Goose originalGoose : messages) {
            copied.add(originalGoose.copy());
        }
//        copied.remove(0);
        if (messages.equals(copied)) {
//            throw new IllegalArgumentException("Messages are not copied! Message 0 timestamp (messages):" + messages.get(0).getTimestamp() + " == (copied):" + copied.get(0).getTimestamp());
            throw new IllegalArgumentException("Messages are not copied! Message 0 timestamp (messages):" + messages + " == (copied):" + copied);
        }
        return copied;
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
        return messages.size();
    }

}
