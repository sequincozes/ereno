package br.ufu.facom.ereno;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.MasqueradeFakeFaultIED;
import br.ufu.facom.ereno.attacks.uc04.devices.MasqueradeFakeNormalED;
import br.ufu.facom.ereno.attacks.uc05.devices.InjectorIED;
import br.ufu.facom.ereno.attacks.uc06.devices.HighStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc07.devices.HighRateStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc08.devices.GrayHoleVictimIED;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.general.ProtectionIED;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter.*;

/** Convenience scripts for single-source dataset generation. */
public class SingleSource {

    public static void main(String[] args) throws IOException {
        Attacks.ECF.loadConfigs();
        GooseFlow.ECF.loadConfigs();
        SetupIED.ECF.loadConfigs();

        Attacks.ECF.legitimate = true;
        Attacks.ECF.randomReplay = true;
        Attacks.ECF.masqueradeOutage = true;
        Attacks.ECF.masqueradeDamage = true;
        Attacks.ECF.randomInjection = true;
        Attacks.ECF.inverseReplay = true;
        Attacks.ECF.highStNum = true;
        Attacks.ECF.flooding = true;
        Attacks.ECF.grayhole = true;

        lightweightDataset("C:\\ereno dataset\\" + SetupIED.ECF.iedName + ".csv", true);
    }

    /**
     * Generates a GOOSE-oriented multi-class dataset. Each attack class is
     * evaluated in a scenario composed of the legitimate victim stream plus that
     * attack, so temporal-difference features use the predecessor an online IDS
     * would actually have observed. Attack classes are not used as each other's
     * predecessor context.
     */
    public static void lightweightDataset(String datasetOutputLocation, boolean generateSV) throws IOException {
        long beginTime = System.currentTimeMillis();
        Logger.getLogger("Extractor").info(datasetOutputLocation + " writing...");

        boolean csv = datasetOutputLocation.toLowerCase(Locale.ROOT).endsWith(".csv");
        startWriting(datasetOutputLocation);
        if (!csv) {
            write("@relation ereno_lightweight");
        }

        int requestedMessages = Math.max(1, GooseFlow.ECF.numberOfMessages);
        boolean headerWritten = false;
        int totalGenerated = 0;

        // A legitimate victim is required even when UC00 itself is not selected,
        // because replay/injection/masquerade attacks need observable context.
        LegitimateProtectionIED uc00 = null;
        if (Attacks.ECF.legitimate || anyAttackEnabled()) {
            uc00 = new LegitimateProtectionIED();
            uc00.run(requestedMessages);
            if (Attacks.ECF.legitimate) {
                writeGooseMessagesToFile(uc00.copyMessages(), true, csv);
                headerWritten = true;
                totalGenerated += uc00.getNumberOfMessages();
            }
        }

        if (uc00 == null) {
            finishWriting();
            return;
        }

        if (Attacks.ECF.randomReplay) {
            RandomReplayerIED attack = new RandomReplayerIED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.inverseReplay) {
            InverseReplayerIED attack = new InverseReplayerIED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.masqueradeOutage) {
            MasqueradeFakeFaultIED attack = new MasqueradeFakeFaultIED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.masqueradeDamage) {
            MasqueradeFakeNormalED attack = new MasqueradeFakeNormalED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.randomInjection) {
            InjectorIED attack = new InjectorIED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.highStNum) {
            HighStNumInjectorIED attack = new HighStNumInjectorIED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.flooding) {
            HighRateStNumInjectorIED attack = new HighRateStNumInjectorIED(uc00);
            attack.run(requestedMessages);
            writeGooseAttackMessagesToFile(uc00, attack, !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        if (Attacks.ECF.grayhole) {
            // Grayhole is a loss scenario: the observed stream is the filtered
            // victim stream itself, not normal+duplicated "attack packets".
            GrayHoleVictimIED attack = new GrayHoleVictimIED(uc00);
            attack.run(80); // retain ~80%, i.e. simulate ~20% selective loss
            writeGooseMessagesToFile(attack.getMessages(), !headerWritten, csv);
            headerWritten = true;
            totalGenerated += attack.getNumberOfMessages();
        }

        finishWriting();
        long endTime = System.currentTimeMillis();
        Logger.getLogger("Time").info("Generated " + totalGenerated + " GOOSE messages in " + (endTime - beginTime) + " ms.");
    }

    /** Legacy alias retained for scripts that call the older method name. */
    public static void scriptForGooseAndSV(String datasetOutputLocation, boolean generateSV) throws IOException {
        lightweightDataset(datasetOutputLocation, generateSV);
    }

    public static void scriptForSV(String[] svData, String datasetLocation) throws IOException {
        MergingUnit mu = new MergingUnit(svData);
        mu.run(4800);
        startWriting(datasetLocation);
        writeSvMessagesToFile(mu.getMessages(), true, "sb");
        finishWriting();
    }

    private static boolean anyAttackEnabled() {
        return Attacks.ECF.randomReplay
                || Attacks.ECF.inverseReplay
                || Attacks.ECF.masqueradeOutage
                || Attacks.ECF.masqueradeDamage
                || Attacks.ECF.randomInjection
                || Attacks.ECF.highStNum
                || Attacks.ECF.flooding
                || Attacks.ECF.grayhole;
    }
}
