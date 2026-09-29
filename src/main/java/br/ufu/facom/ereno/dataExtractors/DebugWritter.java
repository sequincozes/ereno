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

/** Compact diagnostic writer using the same causal predecessor rules. */
public class DebugWritter {
    private static BufferedWriter bw;

    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages,
                                      ArrayList<EthernetFrame> processBusMessages) throws IOException {
        writeDefaultHeader();
        Goose previousGoose = null;
        Logger.getLogger("DebugWritter").info(stationBusMessages.size() + " mensagens na fila.");

        while (!stationBusMessages.isEmpty()) {
            EthernetFrame frame = stationBusMessages.poll();
            if (!(frame instanceof Goose)) {
                continue;
            }
            Goose goose = (Goose) frame;
            if (previousGoose != null) {
                Sv sv = ProtocolCorrelation.getCorrespondingSVFrame(processBusMessages, goose);
                if (sv != null) {
                    String[] f = IntermessageCorrelation.getConsistencyFeaturesAsCSV(goose, previousGoose).split(",");
                    String consistencyDebug = f[0] + "," + f[1] + "," + f[3] + "," + f[6] + "," + f[7] + "," + f[8];
                    double delay = goose.getTimestamp() - sv.getTime();
                    write(sv.getTime() + "," + goose.asDebug() + "," + consistencyDebug + "," + delay + "," + goose.getLabel());
                }
            }
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
                "svTime", "GooseTimestamp", "t", "SqNum", "StNum", "cbStatus",
                "stDiff", "sqDiff", "cbStatusDiff", "timestampDiff", "tDiff",
                "timeFromLastChange", "delay", "class"
        }));
    }

    public static void finishWriting() throws IOException {
        if (bw != null) {
            bw.close();
            bw = null;
        }
    }
}
