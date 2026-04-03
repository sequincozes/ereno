package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/**
 * Generates ARFF (Attribute-Relation File Format) datasets from GOOSE and SV message flows for machine learning analysis.
 *
 * The {@code ARFFWritter} class is responsible for transforming raw network traffic data into structured ARFF files,
 * widely used in Weka and other ML tools. It supports:
 * - Writing detailed attribute headers with electrical, temporal, status, and protocol features
 * - Processing a sequence of GOOSE messages alongside corresponding SV frames
 * - Extracting cross-protocol correlations and intermessage consistency metrics
 * - Labeling each message with its respective class (e.g., normal, replay, masquerade)
 *
 * This class facilitates the creation of labeled datasets for training and evaluating intrusion detection systems (IDS)
 * in substation automation environments based on IEC 61850.
 *
 * @see br.ufu.facom.ereno.messages.Goose
 * @see br.ufu.facom.ereno.messages.Sv
 * @see br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation
 * @see br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation
 */


public class ARFFWritter {
    static BufferedWriter bw;
    public static String[] label = {"normal", "random_replay", "inverse_replay", "masquerade_fake_fault", "masquerade_fake_normal", "injection", "high_StNum", "poisoned_high_rate", "grayhole"};//, "stealthy_injection"};//,"poisoned_high_rate_consistent"};

    public static void processDataset(PriorityQueue<EthernetFrame> stationBusMessages, ArrayList<EthernetFrame> processBusMessages) throws IOException {
        // Writing Header
        writeDefaultHeader();

        // Writing Messages
        Goose previousGoose = null;
        Logger.getLogger("ARFFWritter").info(stationBusMessages.size() + " messages in the queue.");
        while (!stationBusMessages.isEmpty()) {
            Goose goose = (Goose) stationBusMessages.poll();
            if (previousGoose != null) {
                Sv sv = ProtocolCorrelation.getCorrespondingSVFrame(processBusMessages, goose);
                String svString = sv.asCsv();
                String cycleStrig = ProtocolCorrelation.getCorrespondingSVFrameCycle(processBusMessages, goose, 80).asCsv();
                String gooseString = goose.asCSVFull();
                String gooseConsistency = IntermessageCorrelation.getConsistencyFeaturesAsCSV(goose, previousGoose);
                double delay = goose.getTimestamp() - sv.getTime();
                write(svString + "," + cycleStrig + "," + gooseString + "," + gooseConsistency + "," + delay + "," + goose.getLabel());
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
        write("@relation ERENINHO");// time-based 1
        write("@attribute Time numeric");// time-based 1
        write("@attribute isbA numeric"); //SV-related 2
        write("@attribute isbB numeric"); //SV-related 3
        write("@attribute isbC numeric"); //SV-related 4
        write("@attribute vsbA numeric"); //SV-related 8
        write("@attribute vsbB numeric"); //SV-related 9
        write("@attribute vsbC numeric"); //SV-related 10

        write("@attribute isbARmsValue numeric"); //SV-related 14
        write("@attribute isbBRmsValue numeric"); //SV-related 15
        write("@attribute isbCRmsValue numeric"); //SV-related 16
        write("@attribute vsbARmsValue numeric"); //SV-related 20
        write("@attribute vsbBRmsValue numeric"); //SV-related 21
        write("@attribute vsbCRmsValue numeric"); //SV-related 22

        write("@attribute isbATrapAreaSum numeric"); //SV-related 26
        write("@attribute isbBTrapAreaSum numeric"); //SV-related 27
        write("@attribute isbCTrapAreaSum numeric"); //SV-related 28
        write("@attribute vsbATrapAreaSum numeric"); //SV-related 32
        write("@attribute vsbBTrapAreaSum numeric"); //SV-related 33
        write("@attribute vsbCTrapAreaSum numeric"); //SV-related 34

        write("@attribute t numeric"); // time-based  38
        write("@attribute GooseTimestamp numeric"); // time-based 39
        write("@attribute SqNum numeric"); // Status-based 40
        write("@attribute StNum numeric"); // Status-based 41
        write("@attribute cbStatus numeric"); // Status-based 42
        write("@attribute frameLen numeric"); //network-based 43
        write("@attribute ethDst {01:0c:cd:01:2f:80, 01:0c:cd:01:2f:81, 01:a0:f4:08:2f:77, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 44
        write("@attribute ethSrc {01:0c:cd:01:2f:80, 01:0c:cd:01:2f:81, 00:a0:f4:08:2f:77, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 45
        write("@attribute ethType {0x000077b7, 0x000088b8, 0x88B8}"); //network-based 46
        write("@attribute gooseTimeAllowedtoLive numeric"); //IED-based 47
        write("@attribute gooseAppid {0x00003002, 0x00003001}");  //IED-based 48
        write("@attribute gooseLen numeric");  //IED-based 49
        write("@attribute TPID {0x7101, 0x8100, 0x88B8}");  //IED-based 50
        write("@attribute gocbRef {LD/LLN0$IntLockB, LD/LLN0$GO$gcbA, LD/LLN0$GO$gcblA}");  //IED-based 51
        write("@attribute datSet {LD/LLN0$IntLockA, AA1C1Q01A1LD0/LLN0$InterlockingC}");  //IED-based 52
        write("@attribute goID {InterlockingF, InterlockingA, IntLockA}");  //IED-based 53
        write("@attribute test {TRUE, FALSE}");  //IED-based 54
        write("@attribute confRev numeric");  //IED-based 55
        write("@attribute ndsCom {TRUE, FALSE}");  //IED-based 56
        write("@attribute numDatSetEntries numeric");  //IED-based 57
        write("@attribute APDUSize numeric"); //network-based 58

        write("@attribute protocol {SV, GOOSE}"); //network-based 59
        write("@attribute stDiff numeric"); // temporal consistency 60
        write("@attribute sqDiff numeric"); // temporal consistency 61
        write("@attribute gooseLengthDiff numeric"); // temporal consistency 62
        write("@attribute cbStatusDiff numeric"); // temporal consistency 63
        write("@attribute apduSizeDiff numeric"); // temporal consistency 64
        write("@attribute frameLengthDiff numeric"); // temporal consistency 65
        write("@attribute timestampDiff numeric"); // temporal consistency 66
        write("@attribute tDiff numeric"); // temporal consistency 67
        write("@attribute timeFromLastChange numeric"); // temporal consistency 68
        write("@attribute delay numeric"); // temporal consistency 69
        String classLine = "@attribute @class@ {" + label[0] + ", " + label[1] + ", " + label[2] + ", " + label[3] + ", " + label[4] + ", " + label[5] + ", " + label[6] + ", " + label[7] + ", " + label[8]+ "}";

        write(classLine);
        write("@data");
    }

    public static void finishWriting() throws IOException {
        bw.close();
    }

}
