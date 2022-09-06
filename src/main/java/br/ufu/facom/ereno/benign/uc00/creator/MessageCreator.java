package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.messages.EthernetFrame;

public interface MessageCreator {
    void generate(IED ied, int numberofMessages);
}
