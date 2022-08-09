package br.ufu.facom.ereno;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.benign.devices.MergingUnit;
import br.ufu.facom.ereno.benign.devices.ProtectionIED;
import br.ufu.facom.ereno.infected.devices.ReplayerIED;

import java.io.*;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.Util.*;

public class Extractor {

    public static void main(String[] args) throws IOException {
//        scriptForGoose("webapp/download/goose.arff");

        // Load setup file
        Attacks.ECF.loadConfigs();

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
        write("@relation goose_traffic");

        // Generate and write samples for legitimate and attacks choosen in attacks.json
        ProtectionIED uc00 = null;
        if (Attacks.ECF.legitimate) {
            uc00 = new ProtectionIED();
            uc00.setNumberOfPeriodicMessages(90000);
            uc00.run();
            writeGooseMessagesToFile(uc00.getMessages(), label[0], true);
        }

        ReplayerIED uc01 = null;
        Attacks.ECF.randomReplay = true;
        if (Attacks.ECF.randomReplay) {
            uc01 = new ReplayerIED(uc00);
            uc01.setNumReplayMessages(10000);
            uc01.run();
            writeGooseMessagesToFile(uc01.getReplayedMessages(), label[1], false);
        }

        finishWriting();
        long endTime = System.currentTimeMillis();
        Logger.getLogger("Time").info("Tempo gasto para gerar "
                + Integer.valueOf(uc00.getMessages().size() + uc01.getReplayedMessages().size()) + " mensagens: "
                + (endTime - beginTime));

    }

    public static void scriptForSV(String[] svData, String datasetLocation) throws IOException { // Generates only SV data
        // String svData[] = new String[]{"webapp/sv_payload_files/second_1.csv"}
        // String datasetLocation = new "webapp/downloads/dataset_mu.arff"
        MergingUnit mu = new MergingUnit(svData);
        mu.run();
        startWriting(datasetLocation);
        writeSvMessagesToFile(mu.getMessages(), true, "sb");
        finishWriting();
    }

}
