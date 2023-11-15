package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.general.IED;

public interface MessageCreator {
    void generate(IED ied, int numberofMessages);
}
