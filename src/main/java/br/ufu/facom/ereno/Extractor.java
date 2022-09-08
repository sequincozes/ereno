package br.ufu.facom.ereno;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
import br.ufu.facom.ereno.attacks.uc04.devices.FakeNormalMasqueratorIED;
import br.ufu.facom.ereno.attacks.uc05.devices.InjectorIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED;

import java.io.*;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.Util.*;

public class Extractor {

    public static void main(String[] args) throws IOException {
//        scriptForGoose("webapp/download/goose.arff");

        // Load setup file
        Attacks.ECF.loadConfigs();
        GooseFlow.ECF.loadConfigs();
        SetupIED.ECF.loadConfigs();

        scriptForGoose("/home/silvio/datasets/goose.arff");
//         String svData[] = new String[]{"//home/silvio/datasets/Full_SV_2020/resistence_50/second_1.csv"};
//         String datasetLocation = "/home/silvio/datasets/ereno/dataset_mu.arff";
//        scriptForSV(svData,datasetLocation);
    }

    /**
     * Below you can write your scripts for data generation
     * (e.g., you can specify which devices and how they will interate each other)
     */
    public static void scriptForGoose(String datasetOutputLocation) throws IOException { // Generates only GOOSE data
        long beginTime = System.currentTimeMillis();

        // Start writting
        Logger.getLogger("Extractor").info(datasetOutputLocation + " writting...");
        startWriting(datasetOutputLocation);
        int totalMessageCount = 0;
        write("@relation goose_traffic");

        // Generate and write samples for legitimate and attacks choosen in attacks.json
        ProtectionIED uc00 = null;
        if (Attacks.ECF.legitimate) {
            uc00 = new ProtectionIED();
            uc00.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc00.getMessages(), true);
            totalMessageCount = totalMessageCount + uc00.getNumberOfMessages();
        }

        RandomReplayerIED uc01;
        if (Attacks.ECF.randomReplay) {
            uc01 = new RandomReplayerIED(uc00);
            uc01.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc01.getReplayedMessages(), false);
            totalMessageCount = totalMessageCount + uc01.getNumberOfMessages();
        }

        InverseReplayerIED uc02;
        if (Attacks.ECF.inverseReplay) {
            uc02 = new InverseReplayerIED(uc00);
            uc02.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc02.getReplayedMessages(), false);
            totalMessageCount = totalMessageCount + uc02.getNumberOfMessages();
        }

        FakeFaultMasqueratorIED uc03;
        if (Attacks.ECF.masqueradeOutage) {
            uc03 = new FakeFaultMasqueratorIED(uc00);
            uc03.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc03.getMasqueradeMessages(), false);
            totalMessageCount = totalMessageCount + uc03.getNumberOfMessages();
        }

        FakeNormalMasqueratorIED uc04;
        if (Attacks.ECF.masqueradeDamage) {
            uc04 = new FakeNormalMasqueratorIED(uc00);
            uc04.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc04.getMasqueradeMessages(), false);
            totalMessageCount = totalMessageCount + uc04.getNumberOfMessages();
        }

        InjectorIED uc05;
        if (Attacks.ECF.randomInjection) {
            uc05 = new InjectorIED(uc00);
            uc05.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc05.getInjectedMessages(), false);
            totalMessageCount = totalMessageCount + uc05.getNumberOfMessages();
        }

        finishWriting();
        long endTime = System.currentTimeMillis();
        Logger.getLogger("Time").info("Tempo gasto para gerar "
                + Integer.valueOf(totalMessageCount) + " mensagens: "
                + (endTime - beginTime));
    }

    public static void scriptForSV(String[] svData, String datasetLocation) throws IOException { // Generates only SV data
        // String svData[] = new String[]{"webapp/sv_payload_files/second_1.csv"}
        // String datasetLocation = new "webapp/downloads/dataset_mu.arff"
        MergingUnit mu = new MergingUnit(svData);
        mu.run(4800); // setup here how many lines will be consumed by messages SV (setting a very large number will use all available SV data)
        startWriting(datasetLocation);
        writeSvMessagesToFile(mu.getMessages(), true, "sb");
        finishWriting();
    }

}
