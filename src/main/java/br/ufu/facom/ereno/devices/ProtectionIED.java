/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.devices;


import br.ufu.facom.ereno.model.GooseMessage;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author silvio
 */
public final class ProtectionIED {

    private double offset;
    private int initialStNum;
    private int initialSqNum;
    private double[] tIntervals; //time in seconds
    //    private double[] eventsTimestamp;
    private ArrayList<GooseMessage> gooseMessages;
    double delayFromEvent;
    double firstGooseTime;

    double currentGooseTime;
    double backoffStartingMult = 6.33000000000011f;
    double minTime;
    long maxTime;
    private boolean initialCbStatus;
    GooseMessage pseudoPast;

    public ProtectionIED(boolean cbStatus, int stNum, int sqNum, double delayFromEvent,
                         double firstGoose, double initialBackoffInterval, long minTime, long maxTime, double offset) {
        this.initialCbStatus = cbStatus;
        this.initialStNum = stNum;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.initialSqNum = sqNum;
        this.tIntervals = exponentialBackoff(minTime, maxTime, initialBackoffInterval);
        this.delayFromEvent = delayFromEvent;
        this.firstGooseTime = firstGoose;
        this.offset = offset;
    }

    public boolean generatePeriodicGooseMessages(int n) {
        boolean cbStatus = isInitialCbStatus();
        if (this.gooseMessages == null) {
            this.gooseMessages = new ArrayList<>();
        }

        for (int i = 0; i <= n; i++) {
            GooseMessage periodicGoose;
            try {
                // Already exists some messages
                GooseMessage previousGoose = gooseMessages.get(gooseMessages.size() - 1);
                periodicGoose = new GooseMessage(previousGoose.getCbStatus(), previousGoose.getStNum(), previousGoose.getSqNum() + 1,
                        previousGoose.getTimestamp() + 1, previousGoose.getT());
            } catch (IndexOutOfBoundsException e) {
                // These will be the first messages
                periodicGoose = new GooseMessage(toInt(cbStatus), getInitialStNum(), getInitialSqNum(), getFirstGooseTime(), getFirstGooseTime());
            }
            this.gooseMessages.add(periodicGoose);
            setInitialSqNum(getInitialSqNum() + 1);
        }
        return true;
    }

    public ArrayList<GooseMessage> reportEventAt(double eventTimestamp) {
        removeMessagesBeforeThan(eventTimestamp);
        setFirstGooseTime(gooseMessages.get(gooseMessages.size() - 1).getTimestamp());

        // Status change
        setInitialCbStatus(!isInitialCbStatus());
        setInitialStNum(getInitialStNum() + 1);
        ;
        int sqNum = 1;
        double t = eventTimestamp + getDelayFromEvent(); // new t
        double timestamp = t; // timestamp
        System.out.println(t);

        for (double interval : gettIntervals()) { // GOOSE BURST MODE
            GooseMessage gm = new GooseMessage(
                    toInt(isInitialCbStatus()), // current status
                    getInitialStNum(), // same stNum
                    sqNum++, // increase sqNum
                    timestamp, // current timestamp
                    t // timestamp of last st change
            );

            timestamp = timestamp + interval;
            this.gooseMessages.add(gm);
        }
        return this.gooseMessages;
    }

    public ProtectionIED(boolean cbStatus, int stNum, int sqNum, double currentTimestamp, double delayFromEvent,
                         double firstGoose, double initialBackoffInterval, long minTime, long maxTime, double offset) {
        this.initialCbStatus = cbStatus;
        this.initialStNum = stNum;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.initialSqNum = sqNum;
        this.tIntervals = exponentialBackoff(minTime, maxTime, initialBackoffInterval);
//        this.eventsTimestamp = new double[]{};
        this.delayFromEvent = delayFromEvent;
        this.firstGooseTime = firstGoose;
        this.currentGooseTime = currentTimestamp;
    }



//    public ArrayList<GooseMessage> generateBurstingGooseMessages(double currentTimestamp) {
//        if (this.gooseMessages == null) {
//            this.gooseMessages = new ArrayList<>();
//        }
//        int stNum = getInitialStNum();
//        int sqNum = getInitialSqNum();
//        boolean cbStatus = isInitialCbStatus();
//        GooseMessage periodicGoose = new GooseMessage(toInt(cbStatus), stNum, sqNum, currentTimestamp, getFirstGooseTime());
//        this.gooseMessages.add(periodicGoose);              // periodic message
//
//        if (debug) {
//            System.out.println(getPreviousGoose(periodicGoose).asCSVFull());
//            System.out.println(periodicGoose.asCSVFull());
//        }
//
//        for (double eventTimestamp : getEventsTimestamp()) {
//            // Status change
//            cbStatus = !cbStatus;
//            stNum = stNum + 1;
//            sqNum = 1;
//            double timestamp = getDelayFromEvent() + eventTimestamp;
//            double t = timestamp; // new t
//
////            System.out.println(t);
//            for (double interval : gettIntervals()) { // GOOSE BURST MODE
//                if (eventTimestamp == getEventsTimestamp()[0] && interval >= getEventsTimestamp()[1]) {
//                    break;
//                } else {
//                    GooseMessage gm = new GooseMessage(
//                            toInt(cbStatus), // current status
//                            stNum, // same stNum
//                            sqNum++, // increase sqNum
//                            timestamp, // current timestamp
//                            t // timestamp of last st change
//                    );
//                    if (debug) {
//                        System.out.println(gm.asCSVFull());
//                    }
//                    this.gooseMessages.add(gm);
//                    timestamp = timestamp + interval;                               // burst mode
//                }
//            }
//        }
//        if (debug) {
//            System.exit(0);
//        }
//        return this.gooseMessages;
//    }

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

    public double[] exponentialBackoffForUC01(long minTime, long maxTime, double intervalMultiplier) {
        long retryIntervalMs = minTime;

        ArrayList<Double> tIntervals = new ArrayList<>();
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

    public double[] gettIntervals() {
        return tIntervals;
    }

    public void settIntervals(double[] tIntervals) {
        this.tIntervals = tIntervals;
    }

//    public double[] getEventsTimestamp() {
//        return eventsTimestamp;
//    }
//
//    public void setEventsTimestamp(double[] eventsTimestamp) {
//        this.eventsTimestamp = eventsTimestamp;
//    }

    public ArrayList<GooseMessage> getGooseMessages() {
        return gooseMessages;
    }

    public void setGooseMessages(ArrayList<GooseMessage> gooseMessages) {
        this.gooseMessages = gooseMessages;
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

    public double getBackoffStartingMult() {
        return backoffStartingMult;
    }

    public void setBackoffStartingMult(double backoffStartingMult) {
        this.backoffStartingMult = backoffStartingMult;
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

    private int toInt(boolean cbStatus) {
        if (cbStatus == false) {
            return 0;
        } else {
            return 1;
        }

    }

    private long getRandomTime() {
        Random gooseRandom = new Random(System.nanoTime());
        return gooseRandom.nextInt(1000); // random index, random SV messages
    }

    private int getRandomTimeBetween(int low, int high) {
        return new Random(System.currentTimeMillis()).nextInt(high - low) + low;
    }

    public void removeMessagesBeforeThan(double timestamp) {
        double lastTimestamp = gooseMessages.get(gooseMessages.size() - 1).getTimestamp();
        if (lastTimestamp > timestamp) {
            gooseMessages.remove(gooseMessages.size() - 1);
            removeMessagesBeforeThan(timestamp);
        }
    }

}
