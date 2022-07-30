/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.devices;

import br.ufu.facom.ereno.creators.MessageCreator;
import br.ufu.facom.ereno.devices.legitimate.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author silvio
 */
public abstract class IED {
    private ArrayList<EthernetFrame> messages;
    static protected float initialTimestamp = 0;
    protected MessageCreator messageCreator;

    public void enableRandomOffsets(int max) {
         this.initialTimestamp = randomBetween(0, max);
    }


    public static int randomBetween(int min, int max) {
        return new Random(System.nanoTime()).nextInt(max - min) + min;
    }

    abstract public void run();

    public abstract void addMessage(EthernetFrame message);

}
