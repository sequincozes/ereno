package br.ufu.facom.ereno.messages;

import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;

/**
 * In-memory representation of one GOOSE frame.
 *
 * <p>Protocol/network attributes are instance fields. Older ERENO revisions
 * stored them as mutable static fields, which meant that changing one generated
 * attack frame silently changed every other frame. That behavior also made
 * copy() lose the original packet metadata. This implementation snapshots the
 * current ECF values when the frame is created and deep-copies every field.</p>
 */
public class Goose extends EthernetFrame {

    private static final int DEFAULT_TTL = 11000;
    private static final int DEFAULT_NUM_DATASET_ENTRIES = 25;
    private static final int DEFAULT_CONF_REV = 1;

    private String label;
    private int cbStatus;
    private int stNum;
    private int sqNum;
    private double t;

    private int gooseTimeAllowedtoLive = DEFAULT_TTL;
    private int numDatSetEntries = DEFAULT_NUM_DATASET_ENTRIES;
    private int confRev = DEFAULT_CONF_REV;

    private String ethDst;
    private String ethSrc;
    private String ethType;
    private String gooseAppid;
    private String tpid;
    private String gocbRef;
    private String datSet;
    private String goID;
    private String test;
    private String ndsCom;
    private String protocol = "GOOSE";

    public Goose(int cbStatus, int stNum, int sqNum, double timestamp, double t, String label) {
        snapshotMetadataFromECF();
        this.cbStatus = cbStatus;
        this.stNum = stNum;
        this.sqNum = sqNum;
        setTimestamp(timestamp);
        this.t = t;
        this.label = label;
    }

    private void snapshotMetadataFromECF() {
        this.gocbRef = nonBlank(SetupIED.ECF.gocbRef, "LD/LLN0$GO$gcbA");
        this.datSet = nonBlank(SetupIED.ECF.datSet, "LD/LLN0$IntLockA");
        this.goID = nonBlank(GooseFlow.ECF.goID, "IntLockA");
        this.ethDst = nonBlank(GooseFlow.ECF.ethDst, "01:0c:cd:01:2f:77");
        this.ethSrc = nonBlank(GooseFlow.ECF.ethSrc, "01:0c:cd:01:2f:78");
        this.ethType = nonBlank(GooseFlow.ECF.ethType, "0x000088b8");
        this.gooseAppid = nonBlank(GooseFlow.ECF.gooseAppid, "0x00003001");
        this.tpid = nonBlank(GooseFlow.ECF.TPID, "0x8100");
        this.ndsCom = GooseFlow.ECF.ndsCom ? "TRUE" : "FALSE";
        this.test = GooseFlow.ECF.test ? "TRUE" : "FALSE";
    }

    private static String nonBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    /**
     * Creates a GOOSE frame from an existing frame while preserving every
     * protocol/network attribute. Dynamic fields can then be changed safely.
     */
    public Goose copy() {
        Goose copy = new Goose(cbStatus, stNum, sqNum, getTimestamp(), t, label);
        copy.gooseTimeAllowedtoLive = this.gooseTimeAllowedtoLive;
        copy.numDatSetEntries = this.numDatSetEntries;
        copy.confRev = this.confRev;
        copy.ethDst = this.ethDst;
        copy.ethSrc = this.ethSrc;
        copy.ethType = this.ethType;
        copy.gooseAppid = this.gooseAppid;
        copy.tpid = this.tpid;
        copy.gocbRef = this.gocbRef;
        copy.datSet = this.datSet;
        copy.goID = this.goID;
        copy.test = this.test;
        copy.ndsCom = this.ndsCom;
        copy.protocol = this.protocol;
        return copy;
    }

    public int isCbStatus() {
        return cbStatus;
    }

    public int getCbStatus() {
        return cbStatus;
    }

    public void setCbStatus(int cbStatus) {
        if (cbStatus != 0 && cbStatus != 1) {
            throw new IllegalArgumentException("cbStatus must be 0 or 1, got " + cbStatus);
        }
        this.cbStatus = cbStatus;
    }

    public int getInverseCbStatus() {
        return cbStatus == 1 ? 0 : 1;
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

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public int getFrameLen() {
        // Kept constant to preserve the current ERENO feature schema.
        return 200;
    }

    public int getGooseLen() {
        return getFrameLen() - 14;
    }

    public int getApduSize() {
        return getGooseLen() - 11;
    }

    public String asCSVFull() {
        return getT() + "," + getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + cbStatus + "," + getFrameLen()
                + "," + ethDst + "," + ethSrc + "," + ethType + "," + gooseTimeAllowedtoLive + "," + gooseAppid
                + "," + getGooseLen() + "," + tpid + "," + gocbRef + "," + datSet
                + "," + goID + "," + test + "," + confRev + "," + ndsCom
                + "," + numDatSetEntries + "," + getApduSize() + "," + protocol;
    }

    public String asCSVCompact() {
        return getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + getCbStatus();
    }

    public String asCSVCompactHeader() {
        return "timestamp,SqNum,StNum,cbStatus";
    }

    public String asDebug() {
        return getTimestamp() + "," + getT() + "," + getSqNum() + "," + getStNum() + "," + cbStatus;
    }

    public String asCSVinverseStatus() {
        return getTimestamp() + "," + getSqNum() + "," + getStNum() + "," + getInverseCbStatus();
    }

    /**
     * Serializes a masquerade view without mutating the underlying frame.
     */
    public String asCSVMasquerade(boolean resetSqNum) {
        int outputSqNum = resetSqNum ? 0 : getSqNum();
        return getTimestamp() + "," + outputSqNum + "," + getStNum() + "," + getInverseCbStatus();
    }

    public int getGooseTimeAllowedtoLive() {
        return gooseTimeAllowedtoLive;
    }

    public void setGooseTimeAllowedtoLive(int gooseTimeAllowedtoLive) {
        this.gooseTimeAllowedtoLive = gooseTimeAllowedtoLive;
    }

    public int getNumDatSetEntries() {
        return numDatSetEntries;
    }

    public void setNumDatSetEntries(int numDatSetEntries) {
        this.numDatSetEntries = numDatSetEntries;
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

    public String getEthDst() {
        return ethDst;
    }

    public void setEthDst(String ethDst) {
        this.ethDst = ethDst;
    }

    public String getEthSrc() {
        return ethSrc;
    }

    public void setEthSrc(String ethSrc) {
        this.ethSrc = ethSrc;
    }

    public String getEthType() {
        return ethType;
    }

    public void setEthType(String ethType) {
        this.ethType = ethType;
    }

    public String getGooseAppid() {
        return gooseAppid;
    }

    public void setGooseAppid(String gooseAppid) {
        this.gooseAppid = gooseAppid;
    }

    public String getTPID() {
        return tpid;
    }

    public void setTPID(String tpid) {
        this.tpid = tpid;
    }

    public String getGocbRef() {
        return gocbRef;
    }

    public void setGocbRef(String gocbRef) {
        this.gocbRef = gocbRef;
    }

    public String getDatSet() {
        return datSet;
    }

    public void setDatSet(String datSet) {
        this.datSet = datSet;
    }

    public String getGoID() {
        return goID;
    }

    public void setGoID(String goID) {
        this.goID = goID;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getNdsCom() {
        return ndsCom;
    }

    public void setNdsCom(String ndsCom) {
        this.ndsCom = ndsCom;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
