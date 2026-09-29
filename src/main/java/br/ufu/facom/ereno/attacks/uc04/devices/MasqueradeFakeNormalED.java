package br.ufu.facom.ereno.attacks.uc04.devices;

import br.ufu.facom.ereno.attacks.uc04.creator.MasqueradeFakeNornalCreator;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

/** UC04 attacker that forges normal operation during a legitimate fault. */
public class MasqueradeFakeNormalED extends ProtectionIED {
    private final LegitimateProtectionIED victimIED;
    private Goose seedMessage;

    public MasqueradeFakeNormalED(LegitimateProtectionIED victimIED) {
        super(DatasetWriter.label[4]);
        this.victimIED = victimIED;
    }

    /**
     * Kept for scenario code that injects a SubstationNetwork later via
     * setSubstationNetwork(). The creator will use that network if no victim is set.
     */
    public MasqueradeFakeNormalED() {
        this(null);
    }

    @Override
    public void run(int numberOfMessages) {
        messageCreator = new MasqueradeFakeNornalCreator(getLabel());
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
        return seedMessage == null ? null : seedMessage.copy();
    }

    public void setSeed(Goose seedMessage) {
        this.seedMessage = seedMessage == null ? null : seedMessage.copy();
    }
}
