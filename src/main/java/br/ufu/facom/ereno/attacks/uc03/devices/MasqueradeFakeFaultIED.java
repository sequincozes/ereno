package br.ufu.facom.ereno.attacks.uc03.devices;

import br.ufu.facom.ereno.attacks.uc03.creator.MasqueradeFakeFaultCreator;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

/** UC03 attacker that impersonates a legitimate protection IED. */
public class MasqueradeFakeFaultIED extends ProtectionIED {
    private final LegitimateProtectionIED victimIED;

    public MasqueradeFakeFaultIED(LegitimateProtectionIED victimIED) {
        super(DatasetWriter.label[3]);
        if (victimIED == null) {
            throw new IllegalArgumentException("UC03 requires a legitimate victim IED.");
        }
        this.victimIED = victimIED;
    }

    @Override
    public void run(int numberOfMessages) {
        messageCreator = new MasqueradeFakeFaultCreator(getLabel());
        messageCreator.generate(this, numberOfMessages);
        sortMessagesChronologically();
    }

    @Override
    public void addMessage(EthernetFrame message) {
        getMessages().add((Goose) message);
    }

    public LegitimateProtectionIED getVictimIED() {
        return victimIED;
    }

    public Goose getSeedMessage() {
        if (!(messageCreator instanceof MasqueradeFakeFaultCreator)) {
            return null;
        }
        return ((MasqueradeFakeFaultCreator) messageCreator).getSeedMessage();
    }
}
