package br.ufu.facom.ereno.utils;

import br.ufu.facom.ereno.featureEngineering.SVCycle;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ProtocolCorrelation {

    public static Sv getCorrespondingSV(ArrayList<Sv> svs, Goose goose) {
        Logger.getLogger("getCorrespondingSV").info("There are " + svs.size() + " SV messages.");
        Logger.getLogger("getCorrespondingSV").info("SV time range: " + svs.get(0).getTime() + " to " + svs.get(svs.size() - 1).getTime() + " - GOOSE Timestamp: " + goose.getTimestamp());
        int low = 0;
        int high = svs.size() - 1;
        int index = -1;
        double gooseTimestamp = goose.getTimestamp();

        while (low <= high) {
            int mid = (low + high) >>> 1; // Equivalente a (low + high) / 2, mas mais eficiente.
            double svTime = svs.get(mid).getTime();

            if (svTime < gooseTimestamp) {
                index = mid; // Found a candidate, but continue searching for a closer candidate.
                low = mid + 1; // Try to find a SV time closer to GOOSE Timestamp.
            } else {
                // If the SV time is equal or higher, looks to the inferior part.
                high = mid - 1;
            }
        }

        Logger.getLogger("getCorrespondingSV").info("The last SV message was sent at " + svs.get(index).getTime() + ", before the GOOSE at " + goose.getTimestamp());


        return svs.get(index); // an invalid index will be returned if no SV messages are available before the given GOOSE
    }

    // Finds the last SV bofore the GOOSE
    public static SVCycle getCorrespondingSVCycle(ArrayList<Sv> svs, Goose goose, int numCycleMsgs) {
        int low = 0;
        int high = svs.size() - 1;
        int index = -1;
        double gooseTimestamp = goose.getTimestamp();

        while (low <= high) {
            int mid = (low + high) >>> 1; // equivalent to (low + high) / 2, but more efficient.
            double svTime = svs.get(mid).getTime();

            if (svTime < gooseTimestamp) {
                index = mid; // Found a candidate, but continue searching for a closer candidate.
                low = mid + 1; // Try to find a SV time closer to GOOSE Timestamp.
            } else {
                // If the SV time is equal or higher, looks to the inferior part.
                high = mid - 1;
            }
        }

        Sv[] cycleMsgs = new Sv[numCycleMsgs];
        index = index + 1; // to address a rounding bug
        for (int i = 0; i < numCycleMsgs; i++) {
            cycleMsgs[i] = svs.get(index - numCycleMsgs + i);
        }


        SVCycle cycle = new SVCycle(cycleMsgs);
        cycle.computeMetrics();


        return cycle; // an invalid index will be returned if no SV messages are available before the given GOOSE
    }

    public static int getCorrespondingGoose(ArrayList<Goose> gooses, Sv sv) {
        int low = 0;
        int high = gooses.size() - 1;
        int index = -1;
        double svTime = sv.getTime();
//        gooses.remove(0);
        Logger.getLogger("ProtocolCorrelation").info("Searching for a GOOSE sent after " + svTime + " in the range from " + gooses.get(0).getTimestamp() + " to " + gooses.get(gooses.size() - 1).getTimestamp());

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double gooseTimestamp = gooses.get(mid).getTimestamp();

            if (svTime > gooseTimestamp) {
                index = mid; // Found a candidate, but continue searching for a closer candidate.
                low = mid + 1; // Try to find a SV time closer to GOOSE Timestamp.
            } else {
                // If the SV time is equal or higher, looks to the inferior part.
                high = mid - 1;
            }
        }
        Logger.getLogger("ProtocolCorrelation").info("The last GOOSE sent before " + svTime + " in the range from " + gooses.get(0).getTimestamp() + " to " + gooses.get(gooses.size() - 1).getTimestamp());

        return index;
    }

}
