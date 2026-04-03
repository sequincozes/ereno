package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;
import br.ufu.facom.ereno.featureEngineering.SVCycle;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/**
 * Generates labeled CSV datasets from GOOSE and SV message streams for data analysis and machine learning.
 *
 * The {@code CSVWritter} class transforms real-time communication data from IEC 61850-based substation
 * networks into a flat, delimited format (CSV). It supports:
 * - Writing structured CSV headers with electrical, status, network, and temporal features
 * - Efficient buffering for high-performance writing of large datasets
 * - Extracting intermessage consistency and SV-GOOSE correlations
 * - Labeling each instance with predefined attack or normal operation types
 *
 * This class is optimized for high-throughput dataset generation and is particularly useful
 * for evaluating IDS models or conducting offline protocol behavior analysis.
 *
 * @see br.ufu.facom.ereno.messages.Goose
 * @see br.ufu.facom.ereno.messages.Sv
 * @see br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation
 * @see br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation
 */

public class CSVWritter {
    public static String[] label = {"normal", "random_replay", "inverse_replay", "masquerade_fake_fault", "masquerade_fake_normal", "injection", "high_StNum", "poisoned_high_rate", "grayhole"};//, "stealthy_injection"};//,"poisoned_high_rate_consistent"};
    private static final int BUFFER_SIZE = 8192;
    private static BufferedWriter bw;
    private static StringBuilder buffer = new StringBuilder(BUFFER_SIZE);

    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages,
                                      ArrayList<EthernetFrame> processBusMessages) throws IOException {
        writeDefaultHeader();

        Goose previousGoose = null;
        int messageCount = 0;

        while (!stationBusMessages.isEmpty()) {
            Goose goose = (Goose) stationBusMessages.poll();
            if (previousGoose != null) {
                Sv sv = ProtocolCorrelation.getCorrespondingSVFrame(processBusMessages, goose);
                SVCycle cycle = ProtocolCorrelation.getCorrespondingSVFrameCycle(processBusMessages, goose, 80);
                String gooseConsistency = IntermessageCorrelation.getConsistencyFeaturesAsCSV(goose, previousGoose);

                // Use String.format for better performance
                buffer.append(String.format("%s,%s,%s,%s,%.6f,%s%n",
                        sv.asCsv(),
                        cycle.asCsv(),
                        goose.asCSVFull(),
                        gooseConsistency,
                        goose.getTimestamp() - sv.getTime(),
                        goose.getLabel()));

                // Flush buffer when full
                if (buffer.length() >= BUFFER_SIZE) {
                    bw.write(buffer.toString());
                    buffer.setLength(0);
                    messageCount++;
                    if (messageCount % 1000 == 0) {
                        Logger.getLogger("OptimizedCSVWriter")
                                .info("Processed " + messageCount + " messages");
                    }
                }
            }
            previousGoose = goose.copy();
        }

        if (buffer.length() > 0) {
            bw.write(buffer.toString());
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
        String header = String.join(",", new String[] {
                "Time", "isbA", "isbB", "isbC", "vsbA", "vsbB", "vsbC",
                "isbARmsValue", "isbBRmsValue", "isbCRmsValue", "vsbARmsValue", "vsbBRmsValue", "vsbCRmsValue",
                "isbATrapAreaSum", "isbBTrapAreaSum", "isbCTrapAreaSum", "vsbATrapAreaSum", "vsbBTrapAreaSum", "vsbCTrapAreaSum",
                "t", "GooseTimestamp", "SqNum", "StNum", "cbStatus", "frameLen", "ethDst", "ethSrc", "ethType",
                "gooseTimeAllowedtoLive", "gooseAppid", "gooseLen", "TPID", "gocbRef", "datSet", "goID", "test", "confRev", "ndsCom",
                "numDatSetEntries", "APDUSize", "protocol", "stDiff", "sqDiff", "gooseLengthDiff", "cbStatusDiff",
                "apduSizeDiff", "frameLengthDiff", "timestampDiff", "tDiff", "timeFromLastChange", "delay", "class"
        });
        write(header);
    }

    public static void finishWriting() throws IOException {
        bw.close();
    }

}
