package br.ufu.facom.ereno.utils;

import br.ufu.facom.ereno.featureEngineering.SVCycle;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;

public class ProtocolCorrelation {

    public static Sv getCorrespondingSV(ArrayList<Sv> svs, Goose goose) {
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

        return svs.get(index); // an invalid index will be returned if no SV messages are available before the given GOOSE
    }

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
        while (low <= high) {
            int mid = (low + high) >>> 1;
            double gooseTimestamp = gooses.get(mid).getTimestamp();

            if (gooseTimestamp < svTime) {
                low = mid + 1;
            } else {
                index = mid; // Found a GOOSE with timestamp higher or equal to SV time.
                high = mid - 1; // Search for a previous GOOSE with a timestamp still higher or equal to SV time.
            }
        }

        return index; //  // an invalid index will be returned if no SV messages are available before the given GOOSE
    }

}
