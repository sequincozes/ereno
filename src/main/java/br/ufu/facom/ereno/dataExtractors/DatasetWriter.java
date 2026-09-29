package br.ufu.facom.ereno.dataExtractors;

import br.ufu.facom.ereno.featureEngineering.IntermessageCorrelation;
import br.ufu.facom.ereno.featureEngineering.ProtocolCorrelation;
import br.ufu.facom.ereno.featureEngineering.SVCycle;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Legacy-compatible dataset writer with causal feature extraction. */
public class DatasetWriter {
    protected static BufferedWriter bw;
    public static boolean english = false;
    protected static boolean replace = true;

    public static final String[] label = {
            "normal", "random_replay", "inverse_replay", "masquerade_fake_fault",
            "masquerade_fake_normal", "injection", "high_StNum", "poisoned_high_rate", "grayhole"
    };

    public static class Debug {
        public static boolean gooseMessages = false;
        public static boolean printSignatures = false;
    }

    public static void write(String line) throws IOException {
        if (bw == null) {
            throw new IllegalStateException("startWriting() must be called before write().");
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
        FileOutputStream fos = new FileOutputStream(fout, !replace);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
    }

    /**
     * Writes one GOOSE-oriented dataset from a single observable stream.
     * The provided frames are deep-copied and chronologically sorted before
     * inter-message features are computed.
     */
    public static void writeGooseMessagesToFile(ArrayList<Goose> gooseMessages, boolean printHeader, boolean csv) throws IOException {
        if (printHeader) {
            writeDefaultGooseHeader(csv);
        }

        ArrayList<Goose> stream = deepSortedCopy(gooseMessages);
        Goose previous = null;
        for (Goose current : stream) {
            if (previous != null) {
                String row = current.asCSVFull() + ","
                        + IntermessageCorrelation.getConsistencyFeaturesAsCSV(current, previous)
                        + "," + current.getLabel();
                if (Debug.printSignatures) {
                    System.out.println(row);
                }
                write(row);
            }
            previous = current;
        }
    }

    /**
     * Writes only attack rows, while computing inter-message features from a
     * merged legitimate+attack scenario timeline. This is the GOOSE-only
     * counterpart of GSVDatasetWriter.writeAttack().
     */
    public static int writeGooseAttackMessagesToFile(ProtectionIED legitimateIED, ProtectionIED attacker,
                                                      boolean printHeader, boolean csv) throws IOException {
        if (printHeader) {
            writeDefaultGooseHeader(csv);
        }

        ArrayList<Goose> normal = deepSortedCopy(legitimateIED.getMessages());
        ArrayList<Goose> attacks = deepSortedCopy(attacker.getMessages());
        ArrayList<Goose> stream = new ArrayList<>(normal.size() + attacks.size());
        stream.addAll(normal);
        stream.addAll(attacks);
        stream.sort(Comparator.comparingDouble(Goose::getTimestamp));

        Goose previous = null;
        int written = 0;
        for (Goose current : stream) {
            if (previous != null && attacker.getLabel().equals(current.getLabel())) {
                String row = current.asCSVFull() + ","
                        + IntermessageCorrelation.getConsistencyFeaturesAsCSV(current, previous)
                        + "," + current.getLabel();
                write(row);
                written++;
            }
            previous = current;
        }
        return written;
    }

    public enum FOCUS {SV, GOOSE}

    public static void writeSVwithGOOSENormalToFile(ProtectionIED ied, ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        if (printHeader) {
            writeHybridHeader();
        }
        ArrayList<Goose> stream = deepSortedCopy(ied.getMessages());
        Goose previous = null;
        for (Goose current : stream) {
            if (previous != null) {
                writeHybridRow(svMessages, previous, current);
            }
            previous = current;
        }
    }

    /**
     * Writes attack rows using predecessor context from the merged normal+attack
     * observable stream. This prevents class-specific predecessor leakage.
     */
    public void writeSVWithGOOSEAtkToFile(ProtectionIED ied, ArrayList<Goose> attackMessages,
                                          ArrayList<Sv> svMessages, boolean printHeader) throws IOException {
        if (printHeader) {
            writeHybridHeader();
        }

        ArrayList<Goose> stream = deepSortedCopy(ied.getMessages());
        ArrayList<Goose> attacks = deepSortedCopy(attackMessages);
        stream.addAll(attacks);
        stream.sort(Comparator.comparingDouble(Goose::getTimestamp));

        Goose previous = null;
        for (Goose current : stream) {
            if (previous != null && !label[0].equals(current.getLabel())) {
                writeHybridRow(svMessages, previous, current);
            }
            previous = current;
        }
    }

    protected static boolean writeHybridRow(ArrayList<Sv> svMessages, Goose previous, Goose current) throws IOException {
        if (previous == null || current == null) {
            return false;
        }
        Sv sv = ProtocolCorrelation.getCorrespondingSV(svMessages, current);
        SVCycle cycle = ProtocolCorrelation.getCorrespondingSVCycle(svMessages, current, 80);

        // Do not pad with future SV samples. Early GOOSE rows without a full
        // causal SV cycle are intentionally skipped.
        if (sv == null || cycle == null) {
            return false;
        }

        double delay = current.getTimestamp() - sv.getTime();
        String row = sv.asCsv() + "," + cycle.asCsv() + "," + current.asCSVFull() + ","
                + IntermessageCorrelation.getConsistencyFeaturesAsCSV(current, previous)
                + "," + delay + "," + current.getLabel();
        write(row);
        return true;
    }

    public static void writeSvMessagesToFile(ArrayList<Sv> svMessages, boolean printHeader, String substation) throws IOException {
        if (printHeader) {
            writeDefaultSvHeader(substation);
        }
        for (Sv sv : svMessages) {
            write(sv.toString());
        }
    }

    private static void writeDefaultSvHeader(String substation) throws IOException {
        write("@attribute Time numeric");
        write("@attribute i" + substation + "A numeric");
        write("@attribute i" + substation + "B numeric");
        write("@attribute i" + substation + "C numeric");
        write("@attribute v" + substation + "A numeric");
        write("@attribute v" + substation + "B numeric");
        write("@attribute v" + substation + "C numeric");
    }

    protected static void writeHybridHeader() throws IOException {
        write("@relation hibrid");
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
        writeGooseArffAttributes(true);
    }

    private static void writeDefaultGooseHeader(boolean csv) throws IOException {
        if (csv) {
            write("t,GooseTimestamp,SqNum,StNum,cbStatus,frameLen,ethDst,ethSrc,ethType,gooseTimeAllowedtoLive,gooseAppid,gooseLen,TPID,gocbRef,datSet,goID,test,confRev,ndsCom,numDatSetEntries,APDUSize,protocol,stDiff,sqDiff,gooseLengthDiff,cbStatusDiff,apduSizeDiff,frameLengthDiff,timestampDiff,tDiff,timeFromLastChange,class");
            return;
        }
        writeGooseArffAttributes(false);
    }

    private static void writeGooseArffAttributes(boolean includeDelay) throws IOException {
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
        if (includeDelay) {
            write("@attribute delay numeric");
        }
        write("@attribute @class@ {" + String.join(",", label) + "}");
        write("@data");
    }

    public static void finishWriting() throws IOException {
        if (bw != null) {
            bw.close();
            bw = null;
        }
    }

    public static String listFiles(File file) throws IOException {
        StringBuilder html = new StringBuilder("<ul>");
        File[] files = file.listFiles();
        if (files == null) {
            return "<ul></ul>";
        }
        DecimalFormat df = new DecimalFormat("#.#");
        for (File f : files) {
            String downloadLink = "downloads/" + f.getName();
            long bytes = Files.size(f.toPath());
            html.append("<li> ");
            html.append(english ? "<a download href=\"../" : "<a download href=\"")
                    .append(downloadLink).append("\">")
                    .append(f.getName()).append("</a>");
            if (bytes >= 1024L * 1024L * 1024L) {
                html.append(" (").append(df.format(bytes / 1024.0 / 1024.0 / 1024.0)).append(" GB)");
            } else if (bytes >= 1024L * 1024L) {
                html.append(" (").append(df.format(bytes / 1024.0 / 1024.0)).append(" MB)");
            } else {
                html.append(" (").append(df.format(bytes / 1024.0)).append(" KB)");
            }
            html.append("</li>");
        }
        return html.append("</ul>").toString();
    }

    protected static ArrayList<Goose> deepSortedCopy(List<Goose> messages) {
        ArrayList<Goose> copy = new ArrayList<>(messages.size());
        for (Goose message : messages) {
            copy.add(message.copy());
        }
        copy.sort(Comparator.comparingDouble(Goose::getTimestamp));
        return copy;
    }
}
