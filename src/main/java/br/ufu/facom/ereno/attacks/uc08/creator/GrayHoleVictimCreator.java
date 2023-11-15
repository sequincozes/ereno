/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.attacks.uc08.creator;

import br.ufu.facom.ereno.utils.GSVDatasetWritter;
import br.ufu.facom.ereno.benign.uc00.creator.MessageCreator;
import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.Goose;

import java.util.ArrayList;

import static br.ufu.facom.ereno.benign.uc00.devices.IED.randomBetween;

/**
 * @author silvio
 */
public class GrayHoleVictimCreator implements MessageCreator {
    ArrayList<Goose> legitimateMessages;
    public GrayHoleVictimCreator(ArrayList<Goose> legitimateMessages) {
        this.legitimateMessages = legitimateMessages;
    }

    @Override
    public void generate(IED ied, int selectionRate) {
        for (Goose goose : legitimateMessages) {
            if (randomBetween(0, 100) < selectionRate) { // avoid this message to being discarded
                goose.label = GSVDatasetWritter.label[8]; // label it as gray hole attack (uc08)
                ied.addMessage(goose);
            }
        }
    }
}
