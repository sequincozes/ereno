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

/** Writes one sample per GOOSE from a chronologically merged station-bus stream. */
public class CSVWritter {
    private static BufferedWriter bw;

    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages,
                                      ArrayList<EthernetFrame> processBusMessages) throws IOException {
        writeDefaultHeader();

        Goose previousGoose = null;
        Logger.getLogger("CSVWritter").info(stationBusMessages.size() + " mensagens na fila.");
        while (!stationBusMessages.isEmpty()) {
            EthernetFrame frame = stationBusMessages.poll();
            if (!(frame instanceof Goose)) {
                continue;
            }
            Goose goose = (Goose) frame;

            if (previousGoose != null) {
                Sv sv = ProtocolCorrelation.getCorrespondingSVFrame(processBusMessages, goose);
                SVCycle cycle = ProtocolCorrelation.getCorrespondingSVFrameCycle(processBusMessages, goose, 80);
                if (sv != null && cycle != null) {
                    String consistency = IntermessageCorrelation.getConsistencyFeaturesAsCSV(goose, previousGoose);
                    double delay = goose.getTimestamp() - sv.getTime();
                    write(sv.asCsv() + "," + cycle.asCsv() + "," + goose.asCSVFull() + ","
                            + consistency + "," + delay + "," + goose.getLabel());
                }
            }
            // Even if an early row cannot be written because 80 past SV samples
            // are unavailable, it remains part of the observable predecessor chain.
            previousGoose = goose.copy();
        }
    }

    private static void write(String line) throws IOException {
        if (bw == null) {
            throw new IllegalStateException("startWriting() must be called first.");
        }
        bw.write(line);
        bw.newLine();
    }

    public static void startWriting(String filename) throws IOException {
        File fout = new File(filename);
        File parent = fout.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout, false)));
    }

    public static void writeDefaultHeader() throws IOException {
        write(String.join(",", new String[]{
                "Time", "isbA", "isbB", "isbC", "vsbA", "vsbB", "vsbC",
                "isbARmsValue", "isbBRmsValue", "isbCRmsValue", "vsbARmsValue", "vsbBRmsValue", "vsbCRmsValue",
                "isbATrapAreaSum", "isbBTrapAreaSum", "isbCTrapAreaSum", "vsbATrapAreaSum", "vsbBTrapAreaSum", "vsbCTrapAreaSum",
                "t", "GooseTimestamp", "SqNum", "StNum", "cbStatus", "frameLen", "ethDst", "ethSrc", "ethType",
                "gooseTimeAllowedtoLive", "gooseAppid", "gooseLen", "TPID", "gocbRef", "datSet", "goID", "test", "confRev", "ndsCom",
                "numDatSetEntries", "APDUSize", "protocol", "stDiff", "sqDiff", "gooseLengthDiff", "cbStatusDiff",
                "apduSizeDiff", "frameLengthDiff", "timestampDiff", "tDiff", "timeFromLastChange", "delay", "class"
        }));
    }

    public static void finishWriting() throws IOException {
        if (bw != null) {
            bw.close();
            bw = null;
        }
    }
}
