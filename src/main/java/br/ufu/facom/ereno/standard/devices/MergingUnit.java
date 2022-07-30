package br.ufu.facom.ereno.standard.devices;

import br.ufu.facom.ereno.standard.creator.GooseCreator;
import br.ufu.facom.ereno.standard.creator.SVCreator;
import br.ufu.facom.ereno.standard.messages.EthernetFrame;
import br.ufu.facom.ereno.standard.messages.Sv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MergingUnit extends IED {
    protected ArrayList<Sv> messages;
    public static boolean generateSingleRound = false;

    String payloadFiles[];

    public MergingUnit() {
        this.messages = new ArrayList<Sv>();
    }

    @Override
    public void run() {
        for (String payloadFile : payloadFiles) {
            messageCreator = new SVCreator(payloadFile);
            messageCreator.generate(this);
        }
    }

    @Override
    public void addMessage(EthernetFrame message) {
        this.messages.add((Sv) message);
    }


    public ArrayList<Sv> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Sv> messages) {
        this.messages = messages;
    }

    public void setPayloadFiles(String[] payloadFiles) {
        this.payloadFiles = payloadFiles;
    }
}
