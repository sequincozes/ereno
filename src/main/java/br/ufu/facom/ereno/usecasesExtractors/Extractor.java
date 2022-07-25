package br.ufu.facom.ereno.usecasesExtractors;

import br.ufu.facom.ereno.model.GooseMessage;

import java.io.*;
import java.util.ArrayList;

public class Extractor {
    static BufferedWriter bw;
    static String filename = "/home/silvio/datasets/ereno/dataset.arff";
    double maxTime = 1000;
    static boolean replace = true;


    public static void main(String[] args) {
        UC00GooseOnly uc00 = new UC00GooseOnly();
        UC01GooseOnly uc01 = new UC01GooseOnly();
        try {
            startWriting();

            ArrayList<GooseMessage> gooseMessages = uc00.generateNormalSamples(20);
            writeGooseMessagesToFile(gooseMessages, AbstractUseCase.label[0], true);

            ArrayList<GooseMessage> randomReplayMessages = uc01.generateReplayAttacksUC1(gooseMessages, 10);
            writeGooseMessagesToFile(randomReplayMessages, AbstractUseCase.label[1], false);

            finishWriting();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void writeGooseMessagesToFile(ArrayList<GooseMessage> gooseMessages, String attackType, boolean printHeader) throws IOException {
        /* Write Header and Columns */
        if (printHeader) {
            writeDefaulGooseHeader();
        }

        /* Write Payload */
        GooseMessage prev = null;

        for (GooseMessage gm : gooseMessages) {
            if (prev != null) {
                write(gm.asCSVFull() + getConsistencyFeaturesAsCSV(gm, prev) + "," + attackType);
            }
            prev = gm.copy();
        }
    }

    protected void writeDefaultHeader() throws IOException {
        write("@relation compiledtraffic");
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
        String classLine = "@attribute @class@ {"
                + AbstractUseCase.label[0] + ", "
                + AbstractUseCase.label[1] + ", "
                + AbstractUseCase.label[2] + ", "
                + AbstractUseCase.label[3] + ", "
                + AbstractUseCase.label[4] + ", "
                + AbstractUseCase.label[5] + ", "
                + AbstractUseCase.label[6] + ", "
                + AbstractUseCase.label[7]
                + "}";

        write(classLine);
        write("@data");
    }

    protected static void write(String line) throws FileNotFoundException, IOException {
        bw.write(line);
        bw.newLine();
    }

    private static void startWriting() throws FileNotFoundException, IOException {
        File fout = new File(filename);
        FileOutputStream fos = new FileOutputStream(fout, !replace);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
    }

    protected static void finishWriting() throws FileNotFoundException, IOException {
        bw.close();
    }

    private static void writeDefaulGooseHeader() throws IOException {
        write("@relation compiledtraffic");
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
        String classLine = "@attribute @class@ {"
                + AbstractUseCase.label[0] + ", "
                + AbstractUseCase.label[1] + ", "
                + AbstractUseCase.label[2] + ", "
                + AbstractUseCase.label[3] + ", "
                + AbstractUseCase.label[4] + ", "
                + AbstractUseCase.label[5] + ", "
                + AbstractUseCase.label[6] + ", "
                + AbstractUseCase.label[7]
                + "}";

        write(classLine);
        write("@data");
    }

    protected String getConsistencyFeaturesAsCSV(GooseMessage gm, ArrayList<GooseMessage> gooseMessages, double currentSVTime) {

//        if (gm.getStNum() == 0) {
//            gm.setSqNum(initialSqNum);
//            gm.setStNum(initialStNum);
//        }
        GooseMessage prev = getPreviousGoose(gm, gooseMessages);
        int stDiff = gm.getStNum() - prev.getStNum();
        int sqDiff = gm.getSqNum() - prev.getSqNum();
        int gooseLenghtDiff = gm.getGooseLen() - prev.getGooseLen();
        int cbStatusDiff = gm.isCbStatus() - prev.isCbStatus();
        int apduSizeDiff = gm.getApduSize() - prev.getApduSize();
        int frameLenthDiff = gm.getFrameLen() - prev.getFrameLen();
        double timestampDiff = gm.getTimestamp() - prev.getTimestamp();
        double tDiff = (Double.valueOf(gm.getT()) - Double.valueOf(prev.getT()));
        double delay = currentSVTime - gm.getTimestamp();

        //ystem.out.println("Goose (st/sq/time): " + gm.getStNum() + "," + gm.getSqNum() + "," + time + ", Coisinhas:" + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", " + frameLenthDiff + ", " + timestampDiff + ", " + tDiff);
        return "," + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", "
                + cbStatusDiff + ", " + apduSizeDiff + ", " + frameLenthDiff + ", "
                + timestampDiff + ", " + tDiff + ", " + (gm.getTimestamp() - gm.getT()) + ", " + delay;
    }

    protected static String getConsistencyFeaturesAsCSV(GooseMessage gm, GooseMessage prev) {

        int stDiff = gm.getStNum() - prev.getStNum();
        int sqDiff = gm.getSqNum() - prev.getSqNum();
        int gooseLenghtDiff = gm.getGooseLen() - prev.getGooseLen();
        int cbStatusDiff = gm.isCbStatus() - prev.isCbStatus();
        int apduSizeDiff = gm.getApduSize() - prev.getApduSize();
        int frameLenthDiff = gm.getFrameLen() - prev.getFrameLen();
        double timestampDiff = gm.getTimestamp() - prev.getTimestamp();
        double tDiff = (Double.valueOf(gm.getT()) - Double.valueOf(prev.getT()));

        //ystem.out.println("Goose (st/sq/time): " + gm.getStNum() + "," + gm.getSqNum() + "," + time + ", Coisinhas:" + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", " + frameLenthDiff + ", " + timestampDiff + ", " + tDiff);
        return "," + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", "
                + cbStatusDiff + ", " + apduSizeDiff + ", " + frameLenthDiff + ", "
                + timestampDiff + ", " + tDiff + ", " + (gm.getTimestamp() - gm.getT());
    }

    public GooseMessage getLastGooseFromSV(double timestamp, ArrayList<GooseMessage> gooseMessages) {
        GooseMessage lastGooseMessage = gooseMessages.get(0);
        for (GooseMessage gooseMessage : gooseMessages) {
//            System.out.println("Buscando: SV "+timestamp+" em GOOSE: "+gooseMessage.getTimestamp());
            if (gooseMessage.getTimestamp() > timestamp) {
//                System.out.println("ACHOU: depois de "+timestamp+" veio "+gooseMessage.getTimestamp());
//                System.exit(0);
                return lastGooseMessage;
            } else {
                lastGooseMessage = gooseMessage;
            }
        }
        return lastGooseMessage;
    }

    public GooseMessage getPreviousGoose(GooseMessage gooseMessage, ArrayList<GooseMessage> gooseMessages) {
        for (int i = 0; i < gooseMessages.size(); i++) {
            if (gooseMessage.equals(gooseMessages.get(i))) {
                if (i == 0) {
                    GooseMessage pseudoPast = gooseMessages.get(0).copy(); // Pseudo past
                    double pseudoPastTimestamp = gooseMessages.get(0).getTimestamp() - maxTime;
                    pseudoPast.setTimestamp(pseudoPastTimestamp); //Assume the last message wast sent at now - maxtime
                    pseudoPast.setSqNum(pseudoPast.getSqNum() - 1);
                    return pseudoPast;
                } else {
                    return gooseMessages.get(i - 1);
                }
            }
        }
        return null;
    }

}
