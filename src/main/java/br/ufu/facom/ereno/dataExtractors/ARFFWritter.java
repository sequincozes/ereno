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

/** ARFF equivalent of CSVWritter, using the same causal extraction path. */
public class ARFFWritter {
    private static BufferedWriter bw;
    public static final String[] label = DatasetWriter.label;

    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages,
                                      ArrayList<EthernetFrame> processBusMessages) throws IOException {
        writeDefaultHeader();

        Goose previousGoose = null;
        Logger.getLogger("ARFFWritter").info(stationBusMessages.size() + " mensagens na fila.");
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
        write("@relation ERENO");
        write("@attribute Time numeric");
        write("@attribute isbA numeric");
        write("@attribute isbB numeric");
        write("@attribute isbC numeric");
        write("@attribute vsbA numeric");
        write("@attribute vsbB numeric");
        write("@attribute vsbC numeric");
        write("@attribute isbARmsValue numeric");
        write("@attribute isbBRmsValue numeric");
        write("@attribute isbCRmsValue numeric");
        write("@attribute vsbARmsValue numeric");
        write("@attribute vsbBRmsValue numeric");
        write("@attribute vsbCRmsValue numeric");
        write("@attribute isbATrapAreaSum numeric");
        write("@attribute isbBTrapAreaSum numeric");
        write("@attribute isbCTrapAreaSum numeric");
        write("@attribute vsbATrapAreaSum numeric");
        write("@attribute vsbBTrapAreaSum numeric");
        write("@attribute vsbCTrapAreaSum numeric");
        write("@attribute t numeric");
        write("@attribute GooseTimestamp numeric");
        write("@attribute SqNum numeric");
        write("@attribute StNum numeric");
        write("@attribute cbStatus numeric");
        write("@attribute frameLen numeric");
        write("@attribute ethDst string");
        write("@attribute ethSrc string");
        write("@attribute ethType string");
        write("@attribute gooseTimeAllowedtoLive numeric");
        write("@attribute gooseAppid string");
        write("@attribute gooseLen numeric");
        write("@attribute TPID string");
        write("@attribute gocbRef string");
        write("@attribute datSet string");
        write("@attribute goID string");
        write("@attribute test {TRUE,FALSE}");
        write("@attribute confRev numeric");
        write("@attribute ndsCom {TRUE,FALSE}");
        write("@attribute numDatSetEntries numeric");
        write("@attribute APDUSize numeric");
        write("@attribute protocol string");
        write("@attribute stDiff numeric");
        write("@attribute sqDiff numeric");
        write("@attribute gooseLengthDiff numeric");
        write("@attribute cbStatusDiff numeric");
        write("@attribute apduSizeDiff numeric");
        write("@attribute frameLengthDiff numeric");
        write("@attribute timestampDiff numeric");
        write("@attribute tDiff numeric");
        write("@attribute timeFromLastChange numeric");
        write("@attribute delay numeric");
        write("@attribute @class@ {" + String.join(",", label) + "}");
        write("@data");
    }

    public static void finishWriting() throws IOException {
        if (bw != null) {
            bw.close();
            bw = null;
        }
    }
}
