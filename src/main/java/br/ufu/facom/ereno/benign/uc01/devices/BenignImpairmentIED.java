package br.ufu.facom.ereno.benign.uc01.devices;

import br.ufu.facom.ereno.api.RunContext;
import br.ufu.facom.ereno.benign.uc01.creator.BenignImpairmentCreator;
import br.ufu.facom.ereno.dataExtractors.DatasetWriter;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;

import java.util.logging.Logger;

/**
 * Replays a legitimate GOOSE stream through one non-malicious impairment
 * mechanism (major-revision card C, {@link RunContext.Impairment}), producing
 * a {@code benign_degradation} control paired against the grayhole attack
 * classes. Mirrors {@code OrientedGrayHoleIED}'s role in the attack registry.
 *
 * @see BenignImpairmentCreator
 */
public class BenignImpairmentIED extends ProtectionIED {

    private final ProtectionIED legitimateIED;

    public BenignImpairmentIED(ProtectionIED legitimate) {
        // Starting label is "normal" - the creator relabels only the rows the
        // active mechanism actually affects, same convention as legitimate/attack IEDs.
        super(DatasetWriter.label[0]);
        this.legitimateIED = legitimate;
    }

    @Override
    public void run(int numberOfMessages) {
        setInitialTimestamp((float) legitimateIED.getMessages().get(0).getTimestamp());

        Logger.getLogger("BenignImpairmentIED").info(
                "Feeding BenignImpairmentIED (" + RunContext.impairmentMode + ") with "
                        + legitimateIED.copyMessages().size() + " legitimate messages");
        messageCreator = new BenignImpairmentCreator(legitimateIED.copyMessages());
        messageCreator.generate(this, numberOfMessages);
    }

    /**
     * DUPLICATION inserts extra rows beyond the legitimate batch size, which
     * the inherited {@code ProtectionIED.addMessage} threshold check would
     * silently drop with just a warning. Bypass it, same as
     * {@code LegitimateProtectionIED} already does.
     */
    @Override
    public void addMessage(EthernetFrame message) {
        this.messages.add((Goose) message);
    }
}
