/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc04.creator;

import br.ufu.facom.ereno.attacks.uc03.devices.MasqueradeFakeFaultIED;
import br.ufu.facom.ereno.attacks.uc04.devices.MasqueradeFakeNormalED;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.general.IED.randomBetween;

/**
 * @author silvio
 */
public class MasqueradeFakeNornalCreator implements MessageCreator {
    int count;
    private MasqueradeFakeNormalED attacker;
    private Goose previousGoose;
    private String label;

    public MasqueradeFakeNornalCreator(String label) {
        this.label = label;
    }

    @Override
    public void generate(IED ied, int masqueradeMessages) {
        this.attacker = (MasqueradeFakeNormalED) ied;
        this.count = masqueradeMessages;

        //This uses a legitimate fault messages as seeds
        ArrayList<Goose> seeds = new ArrayList<>(); // stores all legitimate Fault
        for (Goose legitimateMsg : attacker.getVictimIED().copyMessages()) {
            if (legitimateMsg.isCbStatus() == 1) {
                seeds.add(legitimateMsg);
            }
        }

        if (seeds.size()<1){
            throw new IllegalArgumentException("There must be at least one seed message.");
        }
        while (((ProtectionIED) ied).getMessages().size() < masqueradeMessages) {
            Goose randomFaultySeed = seeds.get(randomBetween(0, seeds.size() - 1));
            if (attacker.getSeedMessage() == null) {
                attacker.setSeed(randomFaultySeed);
            }
            generateFakeNormalEvent(randomFaultySeed);
        }
    }

    private void generateFakeNormalEvent(Goose seedMessage) {
        reportEventAt(seedMessage, seedMessage.getTimestamp());
        Logger.getLogger("ProtectionIED.run()").info("Reporting normal at " + seedMessage.getTimestamp());
        previousGoose = attacker.getMessages().get(attacker.getNumberOfMessages() - 1);
    }

    private double getNetworkDelay() {
        return randomBetween(0.001, 0.031);
    }

    public void reportEventAt(Goose seedMessage, double eventTimestamp) {
        if (GSVDatasetWritter.Debug.gooseMessages) {
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
                    attacker.toInt(false), // always normal status
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


}
