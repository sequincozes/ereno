package br.ufu.facom.ereno.featureEngineering;

import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ProtocolCorrelation {


    /**
     * Provides correlation utilities between {@link br.ufu.facom.ereno.messages.Goose} and
     * {@link br.ufu.facom.ereno.messages.Sv} messages based on timestamp alignment.
     *
     * Supports finding corresponding SV frames or cycles for a given GOOSE message,
     * and vice versa, using efficient search algorithms and timestamp adjustments.
     *
     * Useful for synchronizing datasets and extracting features involving
     * multi-protocol temporal relationships.
     *
     * @see br.ufu.facom.ereno.messages.Goose
     * @see br.ufu.facom.ereno.messages.Sv
     * @see br.ufu.facom.ereno.messages.EthernetFrame
     * @see br.ufu.facom.ereno.featureEngineering.SVCycle
     */


    public static Sv getCorrespondingSVFrame(ArrayList<EthernetFrame> svs, Goose goose) {
        if (svs == null || goose == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }
        if (svs.isEmpty()) {
            Logger.getLogger(ProtocolCorrelation.class.getName())
                    .warning("Empty SV messages list");
            return null;
        }

        double gooseTimestamp = goose.getTimestamp();
        int intGTS = (int) gooseTimestamp;

        Sv closestSv = null;
        double minTimeDiff = Double.MAX_VALUE;

        for (EthernetFrame frame : svs) {
            if (!(frame instanceof Sv)) continue;

            Sv sv = (Sv) frame;
            sv.setRelativeness(intGTS);
            double timeDiff = Math.abs(sv.getTime() - gooseTimestamp);

            if (timeDiff < minTimeDiff) {
                minTimeDiff = timeDiff;
                closestSv = sv;
            }
        }

        if (closestSv == null) {
            Logger.getLogger(ProtocolCorrelation.class.getName())
                    .warning("No valid SV messages found for GOOSE timestamp: " + gooseTimestamp);
        }

        return closestSv;
    }
    public static SVCycle getCorrespondingSVFrameCycle(ArrayList<EthernetFrame> svs, Goose goose, int numCycleMsgs) {
        int low = 0;
        int high = svs.size() - 1;
        int index = -1;
        double gooseTimestamp = goose.getTimestamp();


        int intGTS = (int) gooseTimestamp;
        double relativeGooseTimestamp = gooseTimestamp - intGTS;
        double svTime = 0;


        while (low <= high) {
            int mid = (low + high) >>> 1;
            Sv svMessage = (Sv) svs.get(mid);
            svMessage.setRelativeness(intGTS);
            svTime = (svMessage).getTime();

            if (svTime < gooseTimestamp) {
                index = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        Sv[] cycleMsgs = new Sv[numCycleMsgs];
        index = index + 1;
        if (index < 80) {
            index = 80;
        }
        for (int i = 0; i < numCycleMsgs; i++) {
            cycleMsgs[i] = ((Sv) svs.get(index - numCycleMsgs + i));
        }


        SVCycle cycle = new SVCycle(cycleMsgs);
        cycle.computeMetrics();

        return cycle;
    }


    public static Sv getCorrespondingSV(ArrayList<Sv> svs, Goose goose) {
        Logger.getLogger("getCorrespondingSV").info("There are " + svs.size() + " SV messages.");
        Logger.getLogger("getCorrespondingSV").info("SV time range: " + svs.get(0).getTime() + " to " + svs.get(svs.size() - 1).getTime() + " - GOOSE Timestamp: " + goose.getTimestamp());
        int low = 0;
        int high = svs.size() - 1;
        int index = -1;
        double gooseTimestamp = goose.getTimestamp();

        int intGTS = (int) gooseTimestamp;
        double relativeGooseTimestamp = gooseTimestamp - intGTS;
        double svTime = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            svTime = svs.get(mid).getTime();

            if (svTime < relativeGooseTimestamp) {
                index = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        if (index > 0) {
            Logger.getLogger("getCorrespondingSV").info("The last SV message was sent at " + svs.get(index).getTime() + ", before the GOOSE at " + goose.getTimestamp());
            return svs.get(index);
        }
        return null;
    }

    public static SVCycle getCorrespondingSVCycle(ArrayList<Sv> svs, Goose goose, int numCycleMsgs) {
        int low = 0;
        int high = svs.size() - 1;
        int index = -1;
        double gooseTimestamp = goose.getTimestamp();

        int intGTS = (int) gooseTimestamp;
        double relativeGooseTimestamp = gooseTimestamp - intGTS;
        double svTime = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            svTime = svs.get(mid).getTime();

            if (svTime < relativeGooseTimestamp) {
                index = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        Sv[] cycleMsgs = new Sv[numCycleMsgs];
        index = index + 1;
        if (index < 80) {
            index = 80;
        }
        for (int i = 0; i < numCycleMsgs; i++) {
//            System.out.println("index(" + index + ") - numCycleMsgs(" + numCycleMsgs + ") + i(" + i + ") = (" + (index - numCycleMsgs + i) + ")");
            cycleMsgs[i] = svs.get(index - numCycleMsgs + i);
        }


        SVCycle cycle = new SVCycle(cycleMsgs);
        cycle.computeMetrics();


        return cycle;
    }

    public static int getCorrespondingGoose(ArrayList<Goose> gooses, Sv sv) {
        int low = 0;
        int high = gooses.size() - 1;
        int index = -1;
        double svTime = sv.getTime();
        Logger.getLogger("ProtocolCorrelation").info("Searching for a GOOSE sent after " + svTime + " in the range from " + gooses.get(0).getTimestamp() + " to " + gooses.get(gooses.size() - 1).getTimestamp());

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double gooseTimestamp = gooses.get(mid).getTimestamp();

            if (svTime > gooseTimestamp) {
                index = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        Logger.getLogger("ProtocolCorrelation").info("The last GOOSE sent before " + svTime + " in the range from " + gooses.get(0).getTimestamp() + " to " + gooses.get(gooses.size() - 1).getTimestamp());

        return index;
    }

}
