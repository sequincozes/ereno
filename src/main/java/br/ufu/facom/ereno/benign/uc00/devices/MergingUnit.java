package br.ufu.facom.ereno.benign.uc00.devices;

import br.ufu.facom.ereno.benign.uc00.creator.SVCreator;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;

public class MergingUnit extends IED {
    protected ArrayList<Sv> messages;

    String payloadFiles[];

    public MergingUnit(String[] payloadFiles) {
        this.messages = new ArrayList<>();
        this.payloadFiles = payloadFiles;
    }

    @Override
    public void run(int numberOfSVMessages) {
        for (String payloadFile : payloadFiles) {
            messageCreator = new SVCreator(payloadFile);
            messageCreator.generate(this, numberOfSVMessages);
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
