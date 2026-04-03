package br.ufu.facom.ereno.messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * This class extends {@code EthernetFrame} and encapsulates the parameters and behaviors of a GOOSE message
 * used in substation communication protocols.
 *
 * It manages dynamically generated fields such as {@code cbStatus}, {@code stNum}, {@code sqNum}, and timestamps.
 * Static configuration parameters are loaded from a properties file or defaulted otherwise.
 *
 * Provides methods to output message data in various CSV formats for analysis or processing, including
 * full detailed output, compact versions, and masquerade attack simulations.
 *
 * Also supports copying instances, retrieving inverse statuses, and retrieving various length and size
 * parameters related to the message frame.
 *
 * @see EthernetFrame
 */

public class Goose extends EthernetFrame {
    private static final Properties config = new Properties();

    private String label;
    private int cbStatus;                   // DYNAMICALLY GENERATED 
    private int stNum;                      // DYNAMICALLY GENERATED 
    private int sqNum;                      // DYNAMICALLY GENERATED 
    private double t;                       // DYNAMICALLY GENERATED - Last Goose Change
    private int gooseTimeAllowedtoLive = 11000;
    private int numDatSetEntries = 25;
    private int confRev = 1;

    // Static configuration with defaults
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

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        try (InputStream input = Goose.class.getClassLoader()
                .getResourceAsStream("params.properties")) {

            if (input != null) {
                config.load(input);

                ethDst = config.getProperty("goose.ethDst", ethDst);
                ethSrc = config.getProperty("goose.ethSrc", ethSrc);
                ethType = config.getProperty("goose.ethType", ethType);
                gooseAppid = config.getProperty("goose.appid", gooseAppid);
                TPID = config.getProperty("goose.TPID", TPID);
                gocbRef = config.getProperty("goose.gocbRef", gocbRef);
                datSet = config.getProperty("goose.datSet", datSet);
                goID = config.getProperty("goose.goID", goID);
                test = config.getProperty("goose.test", test);
                ndsCom = config.getProperty("goose.ndsCom", ndsCom);
            }

        } catch (IOException e) {
            System.err.println("Warning: Could not load goose configuration, using defaults");
        }
    }

    public Goose(int cbStatus, int stNum, int sqNum, double timestamp, double t, String label) {
        this.cbStatus = cbStatus;
        this.stNum = stNum;
        this.sqNum = sqNum;
        super.setTimestamp(timestamp);
        this.t = t;
        this.label = label;
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
        return cbStatus == 1 ? 0 : 1;
    }

    public int getFrameLen() {
        return 200; // Simplified fixed length
    }

    public int getGooseLen() {
        return getFrameLen() - 14;
    }

    public int getApduSize() {
        return getGooseLen() - 11;
    }

    public String asCSVFull() {
        return String.join(",",
                Double.toString(getT()),
                Double.toString(getTimestamp()),
                Integer.toString(getSqNum()),
                Integer.toString(getStNum()),
                Integer.toString(cbStatus),
                Integer.toString(getFrameLen()),
                ethDst, ethSrc, ethType,
                Integer.toString(gooseTimeAllowedtoLive),
                gooseAppid,
                Integer.toString(getGooseLen()),
                TPID, gocbRef, datSet,
                goID, test,
                Integer.toString(confRev),
                ndsCom,
                Integer.toString(numDatSetEntries),
                Integer.toString(getGooseLen()),
                protocol
        );
    }

    public String asCSVCompact() {
        return String.join(",",
                Double.toString(getTimestamp()),
                Integer.toString(getSqNum()),
                Integer.toString(getStNum()),
                Integer.toString(getCbStatus())
        );
    }

    public static String getCSVCompactHeader() {
        return "timestamp,SqNum,StNum,cbStatus";
    }

    public String asDebug() {
        System.out.printf("TIMESTAMP:%.6f T:%.6f%n", getTimestamp(), getT());
        return String.join(",",
                Double.toString(getTimestamp()),
                Double.toString(getT()),
                Integer.toString(getSqNum()),
                Integer.toString(getStNum()),
                Integer.toString(cbStatus)
        );
    }

    public String asCSVinverseStatus() {
        return String.join(",",
                Double.toString(getTimestamp()),
                Integer.toString(getSqNum()),
                Integer.toString(getStNum()),
                Integer.toString(getInverseCbStatus())
        );
    }

    public String asCSVMasquerade(boolean resetSqNum) {
        if (resetSqNum) {
            setSqNum(0);
        }
        return asCSVinverseStatus();
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