/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.messages;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;

import java.text.DecimalFormat;

/**
 * @author silvio
 */
public class Goose extends EthernetFrame {

    private String label;
    private int cbStatus;                   // DYNAMICALLY GENERATED 
    private int stNum;                      // DYNAMICALLY GENERATED 
    private int sqNum;                      // DYNAMICALLY GENERATED 
    private double t;                       // DYNAMICALLY GENERATED - Last Goose Change
    private int gooseTimeAllowedtoLive = 11000;
    private int numDatSetEntries = 25;
    private int confRev = 1;
    public static String ethDst = "01:0c:cd:01:2f:77";
    public static String ethSrc = "01:0c:cd:01:2f:78";
    public static String ethType = "0x000088b8";
    public static String gooseAppid = "0x00003001";
    public static String TPID = "0x8100";
    public static String gocbRef = "LD/LLN0$GO$gcbA";
    public static String datSet = "LD/LLN0$IntLockA";
    public static String goID = "IntLockA";
    public static String test = "FALSE";
    public static String ndsCom = "FALSE";
    public static String protocol = "GOOSE";

//    public static String label;

    public Goose(int cbStatus, int stNum, int sqNum, double timestamp, double t, String label) {
        fromECF();
        this.cbStatus = cbStatus;
        this.stNum = stNum;
        this.sqNum = sqNum;
        super.setTimestamp((timestamp));
        this.t = (t);
        this.label = label;
    }


    private void fromECF() {

        sqNum = Integer.parseInt(SetupIED.ECF.sqNum);
        stNum = Integer.parseInt(SetupIED.ECF.stNum);
        super.setTimestamp((Double.parseDouble(SetupIED.ECF.timestamp)));
        gocbRef = SetupIED.ECF.gocbRef;
        datSet = SetupIED.ECF.datSet;
        goID = GooseFlow.ECF.goID;
        ethDst = GooseFlow.ECF.ethDst;
        ethSrc = GooseFlow.ECF.ethSrc;
        ethType = GooseFlow.ECF.ethType;
        gooseAppid = GooseFlow.ECF.gooseAppid;
        TPID = GooseFlow.ECF.TPID;

        if (GooseFlow.ECF.ndsCom) {
            ndsCom = "TRUE";
        } else {
            ndsCom = "FALSE";
        }

        if (GooseFlow.ECF.cbstatus) {
            cbStatus = 1;
        } else {
            cbStatus = 0;
        }

        if (GooseFlow.ECF.test) {
            test = "TRUE";
        } else {
            test = "FALSE";
        }
    }

    public int isCbStatus() {
        return cbStatus;
    }

    public void setCbStatus(int cbStatus) {
        this.cbStatus = cbStatus;
    }

    public int getStNum() {
        return stNum;
    }

    public void setStNum(int stNum) {
        this.stNum = stNum;
    }

    public int getSqNum() {
        return sqNum;
    }

    public void setSqNum(int sqNum) {
        this.sqNum = sqNum;
    }

    public int getCbStatus() {
        return cbStatus;
    }

    public int getInverseCbStatus() {
        if (cbStatus == 1) {
            return 0;
        } else {
            return 1;
        }
    }


    public int getFrameLen() {
        return 200;/*String.valueOf(numDatSetEntries).length()
                + String.valueOf(gooseTimeAllowedtoLive).length()
                + String.valueOf(cbStatus).length()
                + String.valueOf(stNum).length()
                + String.valueOf(timestamp).length()
                + String.valueOf(t).length()
                + ethDst.length()
                + ethSrc.length()
                + ethType.length()
                + gooseAppid.length()
                + TPID.length()
                + gocbRef.length()
                + datSet.length()
                + goID.length()
                + test.length()
                + ndsCom.length()
                + 115;*/
    }

    public int getGooseLen() {
        return getFrameLen() - 14;
    }

    public int getApduSize() {
        return getGooseLen() - 11;
    }

    public String asCSVFull() {
        return getT() + "," + getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + cbStatus + ", " + getFrameLen()
                + ", " + ethDst + ", " + ethSrc + ", " + ethType + ", " + gooseTimeAllowedtoLive + ", " + gooseAppid
                + ", " + getGooseLen() + ", " + TPID + ", " + gocbRef + ", " + datSet
                + ", " + goID + ", " + test + ", " + confRev + ", " + ndsCom
                + ", " + numDatSetEntries + ", " + getGooseLen() + ", " + protocol;
    }

    public String asCSVCompact() {
        return /*getT() + "," + */getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + getCbStatus();
    }

    public String asCSVCompactHeader() {
        return "timestamp , t, SqNum, StNum, cbStatus";
    }


    public String asDebug() {
        System.out.println("TIMESTAMP:"+getTimestamp() +" T: "+getT());
        return getTimestamp() + "," + getT() + "," + getSqNum() + "," + getStNum() + "," + cbStatus;
    }

    public String asCSVinverseStatus() {
        if (cbStatus == 1) {
            return getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + 0;
        } else {
            return getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + 1;
        }
    }

    public String asCSVMasquerade(boolean resetSqNum) {
        if (resetSqNum) {
            setSqNum(0);
        }
        if (cbStatus == 1) {
            return getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + 0;
        } else {
            return getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + 1;
        }
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public Goose copy() {
        return new Goose(cbStatus, stNum, sqNum, getTimestamp(), t, label);
    }

    public int getGooseTimeAllowedtoLive() {
        return gooseTimeAllowedtoLive;
    }

    public void setGooseTimeAllowedtoLive(int gooseTimeAllowedtoLive) {
        this.gooseTimeAllowedtoLive = gooseTimeAllowedtoLive;
    }

    public int getConfRev() {
        return confRev;
    }

    public void setConfRev(int confRev) {
        this.confRev = confRev;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
