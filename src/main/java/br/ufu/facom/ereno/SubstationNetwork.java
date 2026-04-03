package br.ufu.facom.ereno;

import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.EthernetFrame;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Represents a simulated substation network environment managing communication messages and devices.
 *
 * This class contains collections representing different levels of the substation network:
 * - stationBusMessages: priority queue of Ethernet frames on the station bus, ordered by timestamp or priority.
 * - processBusMessages: list of Ethernet frames on the process bus.
 * - bayLevelDevices: list of Intelligent Electronic Devices (IEDs) operating at the bay level.
 * - processLevelDevices: list of merging units responsible for processing sampled values at the process level.
 *
 * The SubstationNetwork facilitates the coordination and management of these entities
 * for simulation of IEC 61850 communication, including message handling and device interactions.
 *
 * @see br.ufu.facom.ereno.general.IED
 * @see br.ufu.facom.ereno.benign.uc00.devices.MergingUnit
 * @see br.ufu.facom.ereno.messages.EthernetFrame
 */

public class SubstationNetwork {
    public PriorityQueue<EthernetFrame> stationBusMessages;

    public ArrayList<EthernetFrame> processBusMessages;
    public ArrayList<IED> bayLevelDevices;
    public ArrayList<MergingUnit> processLevelDevices;

    public SubstationNetwork() {
        this.stationBusMessages = new PriorityQueue<>();
        this.processBusMessages = new ArrayList<>();
        this.bayLevelDevices = new ArrayList<>();
        this.processLevelDevices = new ArrayList<>();
    }


}
