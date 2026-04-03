package br.ufu.facom.ereno.benign.uc00.devices;

import br.ufu.facom.ereno.benign.uc00.creator.SVCreator;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Simulation model of an IEC 61850 Merging Unit (MU) for benign test scenarios (UC00).
 *
 * This class extends {@code IED} and handles the generation and storage of Sampled Value (SV)
 * messages using a configured {@code SVCreator}. It is responsible for:
 * - Loading electrical measurements from CSV payload files
 * - Generating and storing SV messages with synchronized timestamps
 * - Managing the lifecycle and access to generated SV messages
 *
 * Designed for simulation environments that require realistic MU behavior for testing
 * and validation of SV-based communication flows.
 *
 * @see br.ufu.facom.ereno.benign.uc00.creator.SVCreator
 * @see br.ufu.facom.ereno.general.IED
 * @see br.ufu.facom.ereno.messages.Sv
 */
public class MergingUnit extends IED {
    protected ArrayList<Sv> messages;

    String[] payloadFiles;

    public MergingUnit(String[] payloadFiles) {
        this.messages = new ArrayList<>();
        this.payloadFiles = payloadFiles;
    }

    @Override
    public void run(int numberOfSVMessages) {
        messageCreator = new SVCreator(payloadFiles);
        Logger.getLogger("SVCreator").info("Initial Timestamp: "+ getInitialTimestamp());
        messageCreator.generate(this, numberOfSVMessages);
    }

    @Override
    public void addMessage(EthernetFrame message) {
        this.messages.add((Sv) message);
    }

    @Override
    public void removeMessage(EthernetFrame message) {
        this.messages.remove((Sv) message);
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
