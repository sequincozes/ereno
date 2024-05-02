package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/**
 * This extractor writes the generated messages to an ARFF file.
 * It generates a GOOSE-oriented dataset (one sample per GOOSE).
 */
public class DebugWritter {
    static BufferedWriter bw;
    public static String[] label = {"normal", "random_replay", "inverse_replay", "masquerade_fake_fault", "masquerade_fake_normal", "injection", "high_StNum", "poisoned_high_rate", "grayhole"};//, "stealthy_injection"};//,"poisoned_high_rate_consistent"};

    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages, ArrayList<EthernetFrame> processBusMessages) throws IOException {
        // Writing Header
        writeDefaultHeader();

        // Writing Messages
        Goose previousGoose = null;
//        for (EthernetFrame gooseFrame : stationBusMessages) {
//            Goose goose = (Goose) gooseFrame;

        Logger.getLogger("DebugWritter").info(stationBusMessages.size() + " mensagens na fila.");
        while (!stationBusMessages.isEmpty()) {
            Goose goose = (Goose) stationBusMessages.poll();
            if (previousGoose != null) { // skips the first message
                Sv sv = ProtocolCorrelation.getCorrespondingSVFrame(processBusMessages, goose);
                String svString = String.valueOf(sv.getTime());
                String gooseString = goose.asDebug();
                String gooseConsistency = IntermessageCorrelation.getConsistencyFeaturesAsCSV(goose, previousGoose);

                String[] f = gooseConsistency.split(",");
                String consistencyDebug = f[0] + "," + f[1] + "," + f[2] + "," + f[6] + "," + f[7];

                double delay = goose.getTimestamp() - sv.getTime();
                write(svString + "," + gooseString + "," + consistencyDebug + "," + delay + "," + goose.getLabel());
            }
            previousGoose = goose.copy();
        }


    }

    private static void write(String line) throws IOException {
        bw.write(line);
        bw.newLine();
    }

    public static void startWriting(String filename) throws IOException {
        File fout = new File(filename);
        if (!fout.exists()) {
            fout.getParentFile().mkdirs();
            System.out.println("Directory created at: " + filename);
        }
        FileOutputStream fos = new FileOutputStream(fout, true);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
    }

    public static void writeDefaultHeader() throws IOException {
        String header = String.join(",", new String[]{
                "svTime","GooseTimestamp", "t", "SqNum", "StNum", "cbStatus",
                "stDiff", "sqDiff", "cbStatusDiff",
                "timestampDiff", "tDiff", "timeFromLastChange", "delay", "class"
        });
        write(header);
    }

    public static void finishWriting() throws IOException {
        bw.close();
    }

}
