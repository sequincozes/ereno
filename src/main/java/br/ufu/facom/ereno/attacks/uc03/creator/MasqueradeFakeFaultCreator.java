/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc03.creator;

import br.ufu.facom.ereno.attacks.uc03.devices.MasqueradeFakeFaultIED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.general.IED.randomBetween;

/**
 * @author silvio
 */
public class MasqueradeFakeFaultCreator implements MessageCreator {
    int count;
    private MasqueradeFakeFaultIED attacker;
    private Goose previousGoose;
    private String label;
    private Goose seedMessage;
    private Goose firstSeedMessage;

    public MasqueradeFakeFaultCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int normalMessages) {
        this.attacker = (MasqueradeFakeFaultIED) ied;
        this.count = normalMessages;
        int faultProbability = 5;

        //This uses a legitimate message as seed
        generateSeedGoose(attacker.getVictimIED()); // uncomment it to generate a new seedMessage
        Logger.getLogger("getSeedMessage()").info("Seed message sent at: " + seedMessage.getTimestamp());

        // Generate faults and normal randomly
        while (((ProtectionIED) ied).getMessages().size() < normalMessages) {
            int percentage = randomBetween(1, 100); // decide whether it will be fault or normal
            if (((ProtectionIED) ied).getMessages().size() > 0) {
                if (percentage > faultProbability) {
                    generateFakePeriodicGoose();
                } else { // Generates fault and recovery events
                    generateSeedGoose(attacker.getVictimIED()); // uncomment it to generate a new seedMessage
                    generateFakeFault();
                }
            } else {
                // First message must be fake fault
                generateFakeFault();
            }
        }
    }

    private void generateFakeFault() {
        double lastPeriodicMessage = 0;
        if (attacker.getMessages().size() > 0) {
            lastPeriodicMessage = attacker.getMessages().get(attacker.getNumberOfMessages() - 1).getTimestamp();
        } else {
            lastPeriodicMessage = getSeedMessage().getTimestamp();
        }
        int msFault = 500;
        // avoids SV messages related to faults (between 500 and 600ms)
        while (msFault >= 500 && msFault <= 600) {
            msFault = randomBetween(1, 1000);
        }
        reportEventAt(((int) lastPeriodicMessage) + msFault / 1000); // fault at the beggin of the second
        Logger.getLogger("ProtectionIED.run()").info("Reporting fault at " + lastPeriodicMessage);
        previousGoose = attacker.getMessages().get(attacker.getNumberOfMessages()-1);
    }

    private void generateFakePeriodicGoose() {
        if (previousGoose != null) {
            // Already exists some messages
            Goose periodicGoose = new Goose(
                    1, // always a fault
                    previousGoose.getStNum(),
                    previousGoose.getSqNum() + 1,
                    previousGoose.getTimestamp() + attacker.getMaxTime() / 1000 + getNetworkDelay(),
                    previousGoose.getT(),
                    this.label);
            attacker.addMessage(periodicGoose);
            previousGoose = periodicGoose.copy();
        } else {
            throw new IllegalArgumentException("There must be a pseudo message. Please call generateSeedGoose() before.");
        }
    }

    private void generateSeedGoose(LegitimateProtectionIED victimIED) {
        int seedIndex = randomBetween(1, victimIED.getNumberOfMessages() / 4);
        Goose masqueradeSeed = victimIED.getMessages().get(seedIndex);
        seedMessage = masqueradeSeed.copy();
        seedMessage.setLabel(this.label);
        previousGoose = seedMessage.copy();
        Logger.getLogger("MasqueradeFakeFaultIED").info("Seed message sent at: " + seedMessage.getTimestamp());
    }

    private double getNetworkDelay() {
        return randomBetween(0.001, 0.031);
    }

    public void reportEventAt(double eventTimestamp) {
        if (GSVDatasetWriter.Debug.gooseMessages) {
            Logger.getLogger("GooseCreator").log(Level.INFO, "Reporting an event at " + eventTimestamp + "!");
        }

        attacker.setFirstGooseTime(seedMessage.getTimestamp());

        // Status change
        attacker.setInitialCbStatus(!attacker.isInitialCbStatus());
        attacker.setInitialStNum(attacker.getInitialStNum() + 1);

        int sqNum = 1;
        double t = eventTimestamp + attacker.getDelayFromEvent(); // new t
        double timestamp = t; // timestamp

        double[] burstingIntervals = attacker.exponentialBackoff(
                (long) attacker.getMinTime(),
                attacker.getMaxTime(),
                attacker.getInitialBackoffInterval());

        for (double interval : burstingIntervals) { // GOOSE BURST MODE
            Goose gm = new Goose(
                    attacker.toInt(attacker.isInitialCbStatus()), // current status
                    attacker.getInitialStNum(), // same stNum
                    sqNum++, // increase sqNum
                    timestamp, // current timestamp
                    t, // timestamp of last st change
                    label
            );

            timestamp = timestamp + interval;
            attacker.addMessage(gm);
            previousGoose = gm;
        }

//        System.out.println("ALREADY REPORTED");
    }

    public void removeMessagesAfterEvent(double eventTimestamp) {
        if (attacker.copyMessages().size() > 1) {
            double lastTimestamp = attacker.copyMessages().get(attacker.copyMessages().size() - 1).getTimestamp();
            if (lastTimestamp > eventTimestamp) {
                // Remove messages after the repported event
                attacker.getMessages().remove(attacker.copyMessages().size() - 1);
                removeMessagesAfterEvent(eventTimestamp);
            }
        } else {
            Logger.getLogger("NoGooseMessages").warning("There are no GOOSE messages.");
        }
    }

    public Goose getSeedMessage() {
        return this.seedMessage;
    }
}
