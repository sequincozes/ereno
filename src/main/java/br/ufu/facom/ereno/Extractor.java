package br.ufu.facom.ereno;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
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
    }

    /**
     * Below you can write your scripts for data generation
     * (e.g., you can specify which devices and how they will interate each other)
     */
    public static void scriptForGoose(String datasetLocation) throws IOException { // Generates only GOOSE data
        long beginTime = System.currentTimeMillis();

        // Start writting
        Logger.getLogger("Extractor").info(datasetLocation + " writting...");
        startWriting(datasetLocation);
        int totalMessageCount = 0;
        write("@relation goose_traffic");

        // Generate and write samples for legitimate and attacks choosen in attacks.json
        ProtectionIED uc00 = null;
        Attacks.ECF.legitimate = true;
        if (Attacks.ECF.legitimate) {
            uc00 = new ProtectionIED();
            uc00.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc00.getMessages(), true);
            totalMessageCount = totalMessageCount + uc00.getNumberOfMessages();
        }

        RandomReplayerIED uc01;
        Attacks.ECF.randomReplay = true;
        if (Attacks.ECF.randomReplay) {
            uc01 = new RandomReplayerIED(uc00);
            uc01.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc01.getReplayedMessages(), false);
            totalMessageCount = totalMessageCount + uc01.getNumberOfMessages();
        }

        InverseReplayerIED uc02;
        Attacks.ECF.inverseReplay = true;
        if (Attacks.ECF.randomReplay) {
            uc02 = new InverseReplayerIED(uc00);
            uc02.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc02.getReplayedMessages(), false);
            totalMessageCount = totalMessageCount + uc02.getNumberOfMessages();
        }

        FakeFaultMasqueratorIED uc03;
        Attacks.ECF.randomReplay = true;
        if (Attacks.ECF.randomReplay) {
            uc03 = new FakeFaultMasqueratorIED(uc00);
            uc03.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc03.getMessages(), false);
            totalMessageCount = totalMessageCount + uc03.getNumberOfMessages();
            System.out.println(uc03.getNumberOfMessages() + "<<<");
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
