package br.ufu.facom.ereno;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.MasqueradeFakeFaultIED;
import br.ufu.facom.ereno.attacks.uc05.devices.InjectorIED;
import br.ufu.facom.ereno.attacks.uc06.devices.HighStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc07.devices.HighRateStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc08.devices.GrayHoleVictimIED;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.io.*;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.utils.GSVDatasetWritter.*;

public class SingleSource {

    public static void main(String[] args) throws IOException {
//        scriptForGoose("webapp/download/goose.arff");

        // Load setup file
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
        Attacks.ECF.grayhole = false;

        SingleSource.lightweightDataset("E:\\ereno dataset\\new\\" + SetupIED.ECF.iedName + ".arff", true);
    }

    /**
     * Below you can write your scripts for data generation
     * (e.g., you can specify which devices and how they will interate each other)
     */

    public static void lightweightDataset(String datasetOutputLocation, boolean generateSV) throws IOException { // Generates only GOOSE data
        long beginTime = System.currentTimeMillis();

        // Start writting
        Logger.getLogger("Extractor").info(datasetOutputLocation + " writting...");
        startWriting(datasetOutputLocation);
        int totalMessageCount = 0;
        write("@relation ereno_lightweight");

        // Generate and write samples for legitimate and attacks choosen in attacks.json
        LegitimateProtectionIED uc00 = null;
        if (Attacks.ECF.legitimate) {
            uc00 = new LegitimateProtectionIED();
//            uc00.run(GooseFlow.ECF.numberOfMessages);
            uc00.run(1000);
            writeGooseMessagesToFile(uc00.copyMessages(), true);
            totalMessageCount = totalMessageCount + uc00.getNumberOfMessages();
        }

        RandomReplayerIED uc01;
        if (Attacks.ECF.randomReplay) {
            uc01 = new RandomReplayerIED(uc00);
            uc01.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc01.getMessages(), false);
            totalMessageCount = totalMessageCount + uc01.getNumberOfMessages();
        }

        InverseReplayerIED uc02;
        if (Attacks.ECF.inverseReplay) {
            uc02 = new InverseReplayerIED(uc00);
            uc02.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc02.getMessages(), false);
            totalMessageCount = totalMessageCount + uc02.getNumberOfMessages();
        }

        MasqueradeFakeFaultIED uc03;
        if (Attacks.ECF.masqueradeOutage) {
            uc03 = new MasqueradeFakeFaultIED(uc00);
            uc03.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc03.getMessages(), false);
            totalMessageCount = totalMessageCount + uc03.getNumberOfMessages();
        }

//        MasqueradeFakeFaultIED uc04;
//        if (Attacks.ECF.masqueradeDamage) {
//            uc04 = new MasqueradeFakeFaultIED(uc00.getMessages().get(uc00.getMessages().size()-1));
//            uc04.run(GooseFlow.ECF.numberOfMessages);
//            writeGooseMessagesToFile(uc04.getMessages(), false);
//            totalMessageCount = totalMessageCount + uc04.getNumberOfMessages();
//        }

        InjectorIED uc05;
        if (Attacks.ECF.randomInjection) {
            uc05 = new InjectorIED(uc00);
            uc05.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc05.getMessages(), false);
            totalMessageCount = totalMessageCount + uc05.getNumberOfMessages();
        }

        HighStNumInjectorIED uc06;
        if (Attacks.ECF.highStNum) {
            uc06 = new HighStNumInjectorIED(uc00);
            uc06.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc06.getMessages(), false);
            totalMessageCount = totalMessageCount + uc06.getNumberOfMessages();
        }

        HighRateStNumInjectorIED uc07;
        if (Attacks.ECF.flooding) {
            uc07 = new HighRateStNumInjectorIED(uc00);
            uc07.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc07.getMessages(), false);
            totalMessageCount = totalMessageCount + uc07.getNumberOfMessages();
        }

        GrayHoleVictimIED uc08;
        if (Attacks.ECF.grayhole) {
            uc08 = new GrayHoleVictimIED(uc00);
//            uc08.run(GooseFlow.ECF.numberOfMessages);
            uc08.run(20);
            writeGooseMessagesToFile(uc08.getMessages(), false);
            totalMessageCount = totalMessageCount + uc08.getNumberOfMessages();
        }

        finishWriting();
        long endTime = System.currentTimeMillis();
        Logger.getLogger("Time").info("Tempo gasto para gerar "
                + Integer.valueOf(totalMessageCount) + " mensagens: "
                + (endTime - beginTime));
    }


    public static void scriptForGooseAndSV(String datasetOutputLocation, boolean generateSV) throws IOException { // Generates only GOOSE data
        long beginTime = System.currentTimeMillis();

        // Start writting
        Logger.getLogger("Extractor").info(datasetOutputLocation + " writting...");
        startWriting(datasetOutputLocation);
        int totalMessageCount = 0;
        write("@relation goose_traffic");

        // Generate and write samples for legitimate and attacks choosen in attacks.json
        LegitimateProtectionIED uc00 = null;
        if (Attacks.ECF.legitimate) {
            uc00 = new LegitimateProtectionIED();
//            uc00.run(GooseFlow.ECF.numberOfMessages);
            uc00.run(1000);
            writeGooseMessagesToFile(uc00.copyMessages(), true);
            totalMessageCount = totalMessageCount + uc00.getNumberOfMessages();
        }

        RandomReplayerIED uc01;
        if (Attacks.ECF.randomReplay) {
            uc01 = new RandomReplayerIED(uc00);
            uc01.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc01.getMessages(), false);
            totalMessageCount = totalMessageCount + uc01.getNumberOfMessages();
        }

        InverseReplayerIED uc02;
        if (Attacks.ECF.inverseReplay) {
            uc02 = new InverseReplayerIED(uc00);
            uc02.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc02.getMessages(), false);
            totalMessageCount = totalMessageCount + uc02.getNumberOfMessages();
        }

        MasqueradeFakeFaultIED uc03;
        if (Attacks.ECF.masqueradeOutage) {
            uc03 = new MasqueradeFakeFaultIED(uc00);
            uc03.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc03.getMessages(), false);
            totalMessageCount = totalMessageCount + uc03.getNumberOfMessages();
        }

//        MasqueradeFakeFaultIED uc04;
//        if (Attacks.ECF.masqueradeDamage) {
//            uc04 = new MasqueradeFakeFaultIED(uc00);
//            uc04.run(GooseFlow.ECF.numberOfMessages);
//            writeGooseMessagesToFile(uc04.getMessages(), false);
//            totalMessageCount = totalMessageCount + uc04.getNumberOfMessages();
//        }

        InjectorIED uc05;
        if (Attacks.ECF.randomInjection) {
            uc05 = new InjectorIED(uc00);
            uc05.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc05.getMessages(), false);
            totalMessageCount = totalMessageCount + uc05.getNumberOfMessages();
        }

        HighStNumInjectorIED uc06;
        if (Attacks.ECF.highStNum) {
            uc06 = new HighStNumInjectorIED(uc00);
            uc06.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc06.getMessages(), false);
            totalMessageCount = totalMessageCount + uc06.getNumberOfMessages();
        }

        HighRateStNumInjectorIED uc07;
        if (Attacks.ECF.flooding) {
            uc07 = new HighRateStNumInjectorIED(uc00);
            uc07.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc07.getMessages(), false);
            totalMessageCount = totalMessageCount + uc07.getNumberOfMessages();
        }

        GrayHoleVictimIED uc08;
        if (Attacks.ECF.grayhole) {
            uc08 = new GrayHoleVictimIED(uc00);
//            uc08.run(GooseFlow.ECF.numberOfMessages);
            uc08.run(20);
            writeGooseMessagesToFile(uc08.getMessages(), false);
            totalMessageCount = totalMessageCount + uc08.getNumberOfMessages();
        }

        finishWriting();
        long endTime = System.currentTimeMillis();
        Logger.getLogger("Time").info("Tempo gasto para gerar "
                + Integer.valueOf(totalMessageCount) + " mensagens: "
                + (endTime - beginTime));
    }

    public static void scriptForSV(String[] svData, String datasetLocation) throws IOException { // Generates only SV data
        // String svData[] = new String[]{"E:\ereno dataset\electrical-sources"}
        // String datasetLocation = new "E:\ereno dataset\dataset_mu.arff"
        MergingUnit mu = new MergingUnit(svData);
        mu.run(4800); // setup here how many lines will be consumed by messages SV (setting a very large number will use all available SV data)
        startWriting(datasetLocation);
        writeSvMessagesToFile(mu.getMessages(), true, "sb");
        finishWriting();
    }

}
