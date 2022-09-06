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
//    private ArrayList<EthernetFrame> messages;
    static protected float initialTimestamp = 0;
    protected MessageCreator messageCreator;

    public void enableRandomOffsets(int max) {
         this.initialTimestamp = randomBetween(0, max);
    }

    public static int randomBetween(int min, int max) {
        return new Random(System.nanoTime()).nextInt(max - min) + min;
    }

    abstract public void run(int messageCount);

    public abstract void addMessage(EthernetFrame message);

}
