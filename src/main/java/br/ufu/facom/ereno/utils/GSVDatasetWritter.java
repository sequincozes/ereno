package br.ufu.facom.ereno.utils;

import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.api.GooseFlow.ECF.numberOfMessages;

public class GSVDatasetWritter extends DatasetWritter{

    public static int writeNormal(ArrayList<Goose> gooseMessages, ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        /* Write Header and Columns */
        if (printHeader) {
            writeHeader();
        }

        /* Write Payload */
        Goose prev = null;
        int messagesWritten = 0;
        for (Goose gm : gooseMessages) {
            if (prev != null) { // skips the first message
                Sv sv = ProtocolCorrelation.getCorrespondingSV(svMessages, gm);
                writeToDataset(svMessages, prev, gm, sv);
                messagesWritten += 1;
            }
            prev = gm.copy();
        }
        return messagesWritten;
    }
    public static int writeAttack(ProtectionIED legitimateIED, ProtectionIED attacker, MergingUnit mu, boolean printHeader) throws IOException {
        /* Write Header and Columns */
        if (printHeader) {
            writeHeader();
        }

        /* Write Payload */
        for (int i = 0; i < attacker.getMessages().size(); i++) {
            if (i <= numberOfMessages) {
                Goose currentAttack = attacker.getMessages().get(i);
                Goose prevAttack = attacker.getLastGooseFromTime(currentAttack.getTimestamp());
                Goose prevNormal = legitimateIED.getLastGooseFromTime(currentAttack.getTimestamp());
                Sv sv = ProtocolCorrelation.getCorrespondingSV(mu.getMessages(), currentAttack);
                double timeFromLastNormal = currentAttack.getTimestamp() - prevNormal.getTimestamp();
                double timeFromLastAttack = currentAttack.getTimestamp() - prevAttack.getTimestamp();
                if (timeFromLastNormal > timeFromLastAttack) {
                    // Previous message is an attack
                    writeToDataset(mu.getMessages(), prevAttack, currentAttack, sv);
                } else {
                    // Previous message is a normal sample
                    writeToDataset(mu.getMessages(), prevNormal, currentAttack, sv);
                }
            } else {
                Logger.getLogger("Util").warning("Skipping " + (attacker.getNumberOfMessages() - numberOfMessages) + " malicious messages to make it balanced with normal class.");
                return i;
            }
        }
        return attacker.getMessages().size();
    }
    private static void writeToDataset(ArrayList<Sv> svMessages, Goose prev, Goose attack, Sv sv) throws IOException {
        String svString = sv.asCsv();
        String cycleStrig = ProtocolCorrelation.getCorrespondingSVCycle(svMessages, attack, 80).asCsv();
        String gooseString = attack.asCSVFull() + getConsistencyFeaturesAsCSV(attack, prev);
        double delay = attack.getTimestamp() - sv.getTime();
        write(svString + "," + cycleStrig + "," + gooseString + "," + delay + "," + attack.label);

//        System.out.println("SqNum: "+attack.getSqNum() + " / Label: "+attack.label);

        // debug
//        int stdiff = (attack.getStNum() - prev.getStNum());
//        int sqDiff = (attack.getSqNum() - prev.getSqNum());
//        int cbStatusDiff = (attack.isCbStatus() - prev.isCbStatus());
//        write(
//                "stDiff = " + stdiff + "sqDiff = " + sqDiff +
//                        "cbStatusDiff = " + cbStatusDiff
//        );
    }
    protected static void writeHeader() throws IOException {
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


        // + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", "
        //                + frameLenthDiff + ", " + timestampDiff + ", " + tDiff + ", " + delay;
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
    public static void finishWriting() throws IOException {
        bw.close();
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


        //ystem.out.println("Goose (st/sq/time): " + gm.getStNum() + "," + gm.getSqNum() + "," + time + ", Coisinhas:" + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", " + frameLenthDiff + ", " + timestampDiff + ", " + tDiff);
        return "," + stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", "
                + frameLenthDiff + ", " + timestampDiff + ", " + tDiff + ", " + timeFromLastChange;
    }
}
