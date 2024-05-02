package br.ufu.facom.ereno;

import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.messages.EthernetFrame;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class SubstationNetwork {
    //    public ArrayList<EthernetFrame> stationBusMessages;
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
