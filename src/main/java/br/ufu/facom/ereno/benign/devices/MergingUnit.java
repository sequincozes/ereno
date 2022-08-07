package br.ufu.facom.ereno.benign.devices;

import br.ufu.facom.ereno.benign.creator.SVCreator;
import br.ufu.facom.ereno.benign.messages.EthernetFrame;
import br.ufu.facom.ereno.benign.messages.Sv;

import java.util.ArrayList;

public class MergingUnit extends IED {
    protected ArrayList<Sv> messages;
    public static boolean generateSingleRound = false;

    String payloadFiles[];

    public MergingUnit(String[] payloadFiles) {
        this.messages = new ArrayList<>();
        this.payloadFiles = payloadFiles;
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
