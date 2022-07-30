package br.ufu.facom.ereno.devices.attackers;

import br.ufu.facom.ereno.devices.IED;
import br.ufu.facom.ereno.devices.legitimate.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

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
