/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.devices.legitimate;


import br.ufu.facom.ereno.creators.GooseCreator;
import br.ufu.facom.ereno.devices.IED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

/**
 * @author silvio
 */
public class ProtectionIED extends IED {

    private int initialStNum = 0;
    private int initialSqNum = 0;
    private double[] burstingInterval; // timestam to p (in seconds)
    double delayFromEvent = 0.01659;
    double firstGooseTime = 6.33000000000011f; // IED processing time
    double currentGooseTime = 0.00631;
    double minTime = 4;
    public static long maxTime = 1000;
    private boolean initialCbStatus = false;
    private int numberOfPeriodicMessages = 50;
    protected ArrayList<Goose> messages;

    @Override
    public void enableRandomOffsets(int max) {
        super.enableRandomOffsets(max);
        this.initialStNum = randomBetween(0, max);
        this.initialSqNum = randomBetween(0, max);
    }

    @Override
    public void run() {
        double firstEvent = initialTimestamp + numberOfPeriodicMessages + 0.5;
        double secondEvent = initialTimestamp + numberOfPeriodicMessages + 0.6;

        // Here we set the GooseCreator for creating GOOSE messages for ProtectionIED
        messageCreator = new GooseCreator(numberOfPeriodicMessages);
        messageCreator.generate(this);
        GooseCreator gc = (GooseCreator) messageCreator;
        gc.reportEventAt(firstEvent);
        gc.reportEventAt(secondEvent);
    }

    @Override
    public void addMessage(EthernetFrame periodicGoose) {
        this.messages.add((Goose) periodicGoose);
    }

    public double[] getBurstingInterval() {
        return this.burstingInterval;
    }

    public double[] exponentialBackoff(long minTime, long maxTime, double intervalMultiplier) {
        long retryIntervalMs = minTime;

        ArrayList<Double> tIntervals = new ArrayList<>();
        tIntervals.add(retryIntervalMs / 1000.0);

        do {
            tIntervals.add(retryIntervalMs / 1000.0);
            retryIntervalMs *= intervalMultiplier;
            if (retryIntervalMs > maxTime) {
                intervalMultiplier = intervalMultiplier + 0.001;
                retryIntervalMs = minTime;
            } else if (retryIntervalMs == maxTime) {
                tIntervals.add(retryIntervalMs / 1000.0);
                break;
            }

        } while (retryIntervalMs <= maxTime);

        int i = 0;
        double[] arrayIntervals = new double[tIntervals.size() + 1];
//        arrayIntervals[i++] = tIntervals.get(0); // first two retransmission are on same period
        for (double ti : tIntervals) {
            arrayIntervals[i++] = ti;
//            System.out.println("Interval: " + ti);
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

    public void setBurstingInterval(double[] burstingInterval) {
        this.burstingInterval = burstingInterval;
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

    public int getNumberOfMessages() {
        return this.numberOfPeriodicMessages;
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
//            System.out.println("Buscando: SV "+timestamp+" em GOOSE: "+gooseMessage.getTimestamp());
            if (goose.getTimestamp() > timestamp) {
//                System.out.println("ACHOU: depois de "+timestamp+" veio "+gooseMessage.getTimestamp());
//                System.exit(0);
                return lastGooseMessage;
            } else {
                lastGooseMessage = goose;
            }
        }
        return lastGooseMessage;
    }

}
