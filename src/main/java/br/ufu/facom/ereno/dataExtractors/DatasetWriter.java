package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Responsible for exporting SV and GOOSE messages into ARFF/CSV datasets for machine learning analysis.
 *
 * This class provides methods to:
 * - Write SV and GOOSE messages with rich feature sets, including time-based, signal-based, network, and status metrics.
 * - Compute inter-message consistency features (e.g., StNum/SqNum differences, delays, frame diffs).
 * - Support normal and attack datasets, labeling each instance with attack type.
 * - Create ARFF headers dynamically for different writing scenarios (e.g., hybrid, two devices, compact GOOSE-only).
 * - Generate downloadable file listings in HTML format for dataset export via web interface.
 *
 * Intended for use in offline training and evaluation of intrusion detection systems (IDS)
 * in IEC 61850-based smart grids. Designed to interoperate with {@link br.ufu.facom.ereno.general.ProtectionIED}, {@link br.ufu.facom.ereno.messages.Goose}, and {@link br.ufu.facom.ereno.messages.Sv} messages.
 *
 * @see br.ufu.facom.ereno.general.ProtectionIED
 * @see br.ufu.facom.ereno.messages.Goose
 * @see br.ufu.facom.ereno.messages.Sv
 */


public class DatasetWriter {
    static BufferedWriter bw;
    public static boolean english = false;
    static boolean replace = true;
    public static String[] label = {"normal", "random_replay", "inverse_replay", "masquerade_fake_fault", "masquerade_fake_normal", "injection", "high_StNum", "poisoned_high_rate", "grayhole", "orientedGrayhole"};//, "stealthy_injection"};//,"poisoned_high_rate_consistent"};

    public static class Debug {
        public static boolean gooseMessages = false;
        private static boolean printSignatures = true;
    }

    public static void write(String line) throws IOException {
        bw.write(line);
        bw.newLine();
    }

    public static void startWriting(String filename) throws IOException {
        File fout = new File(filename);
        if (!fout.exists()) {
            fout.getParentFile().mkdirs();
            System.out.println("Directory created at: " + filename);
        }
        FileOutputStream fos = new FileOutputStream(fout, !replace);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
    }

    public static void writeGooseMessagesToFile(ArrayList<Goose> gooseMessages, boolean printHeader) throws IOException {
        if (printHeader) {
            writeDefaulGooseHeader();
        }

        Goose prev = null;

        for (Goose gm : gooseMessages) {
            if (prev != null) {
                String gooseString = gm.asCSVFull() + getConsistencyFeaturesAsCSV(gm, prev) + "," + gm.getLabel();
                if (DatasetWriter.Debug.printSignatures) {
                    System.out.println(gooseString);
                }
                write(gooseString);
                if (DatasetWriter.Debug.gooseMessages) {
                    System.out.println(gm.getLabel() + "," + gm.asCSVCompact());
                }
            }
            prev = gm.copy();
        }
    }

    public enum FOCUS {SV, GOOSE}

    public static void writeSVwithGOOSENormalToFile(ProtectionIED ied, ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        Goose prev;
//            gooseMessages.remove(0);
        for (Sv sv : svMessages) {
            int correspondingGooseIndex = ProtocolCorrelation.getCorrespondingGoose(ied.getMessages(), sv);
            if (correspondingGooseIndex > 0) {
                Goose gm = ied.getMessages().get(correspondingGooseIndex);
                if (correspondingGooseIndex > 1) {
                    prev = ied.getMessages().get(correspondingGooseIndex - 1);
                } else {
                    prev = gm.copy();
                    prev.setTimestamp(gm.getTimestamp() + ied.getMaxTime());
                    prev.setSqNum(gm.getSqNum() + 1);
                }
                String svString = sv.asCsv();
                String cycleStrig = ProtocolCorrelation.getCorrespondingSVCycle(svMessages, gm, 80).asCsv();
                String gooseString = gm.asCSVFull() + getConsistencyFeaturesAsCSV(gm, prev) + "," + gm.getLabel();
                double delay = gm.getTimestamp() - sv.getTime();
                write(svString + "," + cycleStrig + "," + gooseString + "," + delay + "," + gm.getLabel());

            }
        }
    }

    public void writeSVWithGOOSEAtkToFile(ProtectionIED ied, ArrayList<Goose> attackMessages, ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        Goose prev;
        for (Sv sv : svMessages) { //@TODO: maybe it will be necessary to update the below code accoridng to the above to skip the first GOOSE message
            int correspondingGooseIndex = ProtocolCorrelation.getCorrespondingGoose(attackMessages, sv);
            if (correspondingGooseIndex > 0) {
                Goose gm = attackMessages.get(correspondingGooseIndex);
                if (correspondingGooseIndex > 1) {
                    prev = attackMessages.get(correspondingGooseIndex - 1);
                } else {
                    prev = gm.copy();
                    prev.setTimestamp(gm.getTimestamp() + ied.getMaxTime());
                    prev.setSqNum(gm.getSqNum() + 1);
                }
                String svString = sv.asCsv();
                String cycleStrig = ProtocolCorrelation.getCorrespondingSVCycle(svMessages, gm, 80).asCsv();
                String gooseString = gm.asCSVFull() + getConsistencyFeaturesAsCSV(gm, prev) + "," + gm.getLabel();
                write(svString + "," + cycleStrig + "," + gooseString + "," + gm.getLabel());
            }
        }
    }

    private static void writeToDataset(ArrayList<Sv> svMessages, Goose prev, Goose attack, Sv sv) throws IOException {
        String svString = sv.asCsv();
        String cycleStrig = ProtocolCorrelation.getCorrespondingSVCycle(svMessages, attack, 80).asCsv();
        String gooseString = attack.asCSVFull() + getConsistencyFeaturesAsCSV(attack, prev);
        double delay = attack.getTimestamp() - sv.getTime();
        write(svString + "," + cycleStrig + "," + gooseString + "," + delay + "," + attack.getLabel());

    }

    public static void writeSvMessagesToFile(ArrayList<Sv> svMessages, boolean printHeader, String substation) throws IOException {
        if (printHeader) {
            writeDefaulSvHeader(substation);
        }

        for (Sv sv : svMessages) {
            write(sv.toString());
        }
    }

    private static void writeDefaulSvHeader(String substation) throws IOException {
        write("@attribute Time numeric");// time-based 1
        write("@attribute i" + substation + "A numeric"); //SV-related 2
        write("@attribute i" + substation + "B numeric"); //SV-related 3
        write("@attribute i" + substation + "C numeric"); //SV-related 4
        write("@attribute v" + substation + "A numeric"); //SV-related 8
        write("@attribute v" + substation + "B numeric"); //SV-related 9
        write("@attribute v" + substation + "C numeric"); //SV-related 10
    }

    protected static void writeDefaultHeader() throws IOException {
        write("@attribute Time numeric");// time-based 1
        write("@attribute isbA numeric"); //SV-related 2
        write("@attribute isbB numeric"); //SV-related 3
        write("@attribute isbC numeric"); //SV-related 4
        write("@attribute ismA numeric"); //SV-related 5
        write("@attribute ismB numeric"); //SV-related 6
        write("@attribute ismC numeric"); //SV-related 7
        write("@attribute vsbA numeric"); //SV-related 8
        write("@attribute vsbB numeric"); //SV-related 9
        write("@attribute vsbC numeric"); //SV-related 10
        write("@attribute vsmA numeric"); //SV-related 11
        write("@attribute vsmB numeric"); //SV-related 12
        write("@attribute vsmC numeric"); //SV-related 13

        write("@attribute isbARmsValue numeric"); //SV-related 14
        write("@attribute isbBRmsValue numeric"); //SV-related 15
        write("@attribute isbCRmsValue numeric"); //SV-related 16
        write("@attribute ismARmsValue numeric"); //SV-related 17
        write("@attribute ismBRmsValue numeric"); //SV-related 18/
        write("@attribute ismCRmsValue numeric"); //SV-related 19
        write("@attribute vsbARmsValue numeric"); //SV-related 20
        write("@attribute vsbBRmsValue numeric"); //SV-related 21
        write("@attribute vsbCRmsValue numeric"); //SV-related 22
        write("@attribute vsmARmsValue numeric"); //SV-related 23
        write("@attribute vsmBRmsValue numeric"); //SV-related 24
        write("@attribute vsmCRmsValue numeric"); //SV-related 25

        write("@attribute isbATrapAreaSum numeric"); //SV-related 26
        write("@attribute isbBTrapAreaSum numeric"); //SV-related 27
        write("@attribute isbCTrapAreaSum numeric"); //SV-related 28
        write("@attribute ismATrapAreaSum numeric"); //SV-related 29
        write("@attribute ismBTrapAreaSum numeric"); //SV-related 30
        write("@attribute ismCTrapAreaSum numeric"); //SV-related 31
        write("@attribute vsbATrapAreaSum numeric"); //SV-related 32
        write("@attribute vsbBTrapAreaSum numeric"); //SV-related 33
        write("@attribute vsbCTrapAreaSum numeric"); //SV-related 34
        write("@attribute vsmATrapAreaSum numeric"); //SV-related 35
        write("@attribute vsmBTrapAreaSum numeric"); //SV-related 36
        write("@attribute vsmCTrapAreaSum numeric"); //SV-related 37

        write("@attribute t numeric"); // time-based  38
        write("@attribute GooseTimestamp numeric"); // time-based 39
        write("@attribute SqNum numeric"); // Status-based 40
        write("@attribute StNum numeric"); // Status-based 41
        write("@attribute cbStatus numeric"); // Status-based 42
        write("@attribute frameLen numeric"); //network-based 43
        write("@attribute ethDst {01:a0:f4:08:2f:77, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 44
        write("@attribute ethSrc {00:a0:f4:08:2f:77, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 45
        write("@attribute ethType {0x000077b7, 0x000088b8}"); //network-based 46
        write("@attribute gooseTimeAllowedtoLive numeric"); //IED-based 47
        write("@attribute gooseAppid {0x00003002, 0x00003001}");  //IED-based 48
        write("@attribute gooseLen numeric");  //IED-based 49
        write("@attribute TPID {0x7101, 0x8100}");  //IED-based 50
        write("@attribute gocbRef {LD/LLN0$IntLockB, LD/LLN0$GO$gcbA}");  //IED-based 51
        write("@attribute datSet {LD/LLN0$IntLockA, AA1C1Q01A1LD0/LLN0$InterlockingC}");  //IED-based 52
        write("@attribute goID {InterlockingF, InterlockingA}");  //IED-based 53
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
        String classLine = "@attribute @class@ {" + label[0] + ", " + label[1] + ", " + label[2] + ", " + label[3] + ", " + label[4] + ", " + label[5] + ", " + label[6] + ", " + label[7] + "}";

        write(classLine);
        write("@data");
    }

    protected static void writeTwoDevicesHeader() throws IOException {
        write("@relation hibrid");// time-based 1
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

        write("@attribute ethDst {01:a0:f4:08:2f:77, 01:0c:cd:01:2f:80, 01:0c:cd:01:2f:81, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 44
        write("@attribute ethSrc {00:a0:f4:08:2f:77,01:0c:cd:01:2f:80, 01:0c:cd:01:2f:81, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 45
        write("@attribute ethType {0x000077b7, 0x000088b8, 0x88B8}"); //network-based 46
        write("@attribute gooseTimeAllowedtoLive numeric"); //IED-based 47
        write("@attribute gooseAppid {0x00003002, 0x00003001, 0x00003092, 0x00003011, 0x0043002, 0x00233001}");  //IED-based 48
        write("@attribute gooseLen numeric");  //IED-based 49
        write("@attribute TPID {0x7101, 0x8100, 0x88B8}");  //IED-based 50
        write("@attribute gocbRef {LD/LLN0$IntLockB, LD/LLN0$GO$gcbA, LD/LLN0$GO$gcblA}");  //IED-based 51
        write("@attribute datSet {LD/LLN0$IntLockA, AA1C1Q01A1LD0/LLN0$InterlockingC, LD/LLN0$GO$gcblA}");  //IED-based 52
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
        String classLine = "@attribute @class@ {" + label[0] + ", " + label[1] + ", " + label[2] + ", " + label[3] + ", " + label[4] + ", " + label[5] + ", " + label[6] + ", " + label[7] + "," + label[8] + "}";

        write(classLine);
        write("@data");
    }

    private static void writeDefaulGooseHeader() throws IOException {
        write("@attribute t numeric"); // time-based  38
        write("@attribute GooseTimestamp numeric"); // time-based 39
        write("@attribute SqNum numeric"); // Status-based 40
        write("@attribute StNum numeric"); // Status-based 41
        write("@attribute cbStatus numeric"); // Status-based 42
        write("@attribute frameLen numeric"); //network-based 43
        write("@attribute ethDst {01:0c:cd:01:2f:80, 01:0c:cd:01:2f:81, 01:0c:cd:01:2f:77, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 44
        write("@attribute ethSrc {01:0c:cd:01:2f:80, 01:0c:cd:01:2f:81, 01:0c:cd:01:2f:77, FF:FF:FF:FF:FF:11, FF:FF:FF:FF:FF:22, FF:FF:FF:FF:FF:33, FF:FF:FF:FF:FF:44, FF:FF:FF:FF:FF:55, FF:FF:FF:FF:FF:66, FF:FF:FF:FF:FF:FF, FF:FF:FF:FF:FF:77, FF:FF:FF:FF:FF:AA, FF:FF:FF:FF:FF:BB, FF:FF:FF:FF:FF:CC, FF:FF:FF:FF:FF:DD, FF:FF:FF:FF:FF:EE, FF:FF:FF:FF:FF:AB, FF:FF:FF:FF:FF:AC}"); //network-based 45
        write("@attribute ethType {0x88B8, 0x000077b7, 0x000088b8}"); //network-based 46
        write("@attribute gooseTimeAllowedtoLive numeric"); //IED-based 47
        write("@attribute gooseAppid {0x00003002, 0x00003001}");  //IED-based 48
        write("@attribute gooseLen numeric");  //IED-based 49
        write("@attribute TPID {0x7101, 0x8100}");  //IED-based 50
        write("@attribute gocbRef {LD/LLN0$IntLockB, LD/LLN0$GO$gcbA, LD/LLN0$GO$gcblA}");  //IED-based 51
        write("@attribute datSet {LD/LLN0$IntLockA, AA1C1Q01A1LD0/LLN0$InterlockingC, LD/LLN0$GO$gcblA}");  //IED-based 52
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
        String classLine = "@attribute @class@ {" + label[0] + ", "
//                + label[1] + ", "
//                + label[2] + ", "
//                + label[3] + ", "
//                + label[4] + ", "
//                + label[5] + ", "
//                + label[6] + ", "
//                + label[7] + ", "
                + label[8] + "}";

        write(classLine);
        write("@data");
    }

    public static void finishWriting() throws IOException {
        bw.close();
    }

    public static String listFiles(File file) throws IOException {
        String html = "<ul>";

        for (File f : file.listFiles()) {
            String downloadLink = "downloads" + "/" + f.getName();
            System.out.println(downloadLink);
            float filesize = Files.size(f.toPath());
            DecimalFormat df = new DecimalFormat("#.#");

            html = html + "<li> ";
            if (DatasetWriter.english) {
                html = html + "<a download href=\"../" + downloadLink + "\">";
            } else {
                html = html + "<a download href=\"" + downloadLink + "\">";
            }
            html = html + f.getName() + "<a/>";
            if (filesize > 1) {
                html = html + " (" + df.format(filesize / 1024) + " KB)";
            } else if (filesize / 1024 > 1) {
                html = html + " (" + df.format(filesize / 1024 / 1024) + " MB)";
            } else if (filesize / 1024 / 1024 > 1) {
                html = html + " (" + df.format(filesize / 1024 / 1024 / 1024) + " GB)";
            }
            html = html + "</li>";
        }
        return html + "</ul>";
    }

    @Deprecated
    private String getConsistencyFeaturesAsCSV(Goose gm, ProtectionIED protectionIED, double currentSVTime) {
        Goose prev = protectionIED.getPreviousGoose(gm, protectionIED);
        int stDiff = gm.getStNum() - prev.getStNum();
        int sqDiff = gm.getSqNum() - prev.getSqNum();
        int gooseLenghtDiff = gm.getGooseLen() - prev.getGooseLen();
        int cbStatusDiff = gm.isCbStatus() - prev.isCbStatus();
        int apduSizeDiff = gm.getApduSize() - prev.getApduSize();
        int frameLenthDiff = gm.getFrameLen() - prev.getFrameLen();
        double timestampDiff = gm.getTimestamp() - prev.getTimestamp();
        double tDiff = (Double.valueOf(gm.getT()) - Double.valueOf(prev.getT()));
        double delay = currentSVTime - gm.getTimestamp();

        return "," + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", " + frameLenthDiff + ", " + timestampDiff + ", " + tDiff + ", " + (gm.getTimestamp() - gm.getT()) + ", " + delay;
    }

    private static String getConsistencyFeaturesAsCSV(Goose gm, Goose prev) {
        int stDiff = gm.getStNum() - prev.getStNum();
        int sqDiff = gm.getSqNum() - prev.getSqNum();
        int gooseLenghtDiff = gm.getGooseLen() - prev.getGooseLen();
        int cbStatusDiff = 0;
        if (gm.isCbStatus() != prev.isCbStatus()) {
            cbStatusDiff = 1; // has a status change
        }
        int apduSizeDiff = gm.getApduSize() - prev.getApduSize();
        int frameLenthDiff = gm.getFrameLen() - prev.getFrameLen();
        double timestampDiff = gm.getTimestamp() - prev.getTimestamp();
        double tDiff = (Double.valueOf(gm.getT()) - Double.valueOf(prev.getT()));
        double timeFromLastChange = (gm.getTimestamp() - gm.getT());


        return "," + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", "
                + frameLenthDiff + ", " + timestampDiff + ", " + tDiff + ", " + timeFromLastChange;
    }
}
