package br.ufu.facom.ereno.infected.devices;

import br.ufu.facom.ereno.standard.devices.IED;
import br.ufu.facom.ereno.standard.messages.EthernetFrame;
import br.ufu.facom.ereno.standard.messages.Goose;

import java.util.ArrayList;

public class ReplayerIED extends IED {

    protected ArrayList<Goose> messages;

    @Override
    public void run() {

    }

    @Override
    public void addMessage(EthernetFrame message) {
        messages.add((Goose) message);
    }

    public ArrayList<Goose> getMessages() {
        return this.messages;
    }
}
