package br.ufu.facom.ereno.featureEngineering;

import br.ufu.facom.ereno.messages.EthernetFrame;
import br.ufu.facom.ereno.messages.Goose;
import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;

/**
 * Causal correlation between GOOSE and Sampled Values.
 *
 * <p>Only SV samples strictly earlier than the target GOOSE are used. If a full
 * requested SV cycle is not yet available, the cycle method returns {@code null}
 * instead of padding with future samples. This removes look-ahead leakage.</p>
 */
public final class ProtocolCorrelation {
    private ProtocolCorrelation() {
    }

    public static Sv getCorrespondingSVFrame(ArrayList<EthernetFrame> svs, Goose goose) {
        int index = lastSvBeforeFrameList(svs, goose.getTimestamp());
        return index >= 0 ? (Sv) svs.get(index) : null;
    }

    public static SVCycle getCorrespondingSVFrameCycle(ArrayList<EthernetFrame> svs, Goose goose, int numCycleMsgs) {
        validateCycleSize(numCycleMsgs);
        int endIndex = lastSvBeforeFrameList(svs, goose.getTimestamp());
        if (endIndex < 0 || endIndex + 1 < numCycleMsgs) {
            return null;
        }

        Sv[] cycle = new Sv[numCycleMsgs];
        int start = endIndex - numCycleMsgs + 1;
        for (int i = 0; i < numCycleMsgs; i++) {
            cycle[i] = (Sv) svs.get(start + i);
        }
        SVCycle result = new SVCycle(cycle);
        result.computeMetrics();
        return result;
    }

    public static Sv getCorrespondingSV(ArrayList<Sv> svs, Goose goose) {
        int index = lastSvBefore(svs, goose.getTimestamp());
        return index >= 0 ? svs.get(index) : null;
    }

    public static SVCycle getCorrespondingSVCycle(ArrayList<Sv> svs, Goose goose, int numCycleMsgs) {
        validateCycleSize(numCycleMsgs);
        int endIndex = lastSvBefore(svs, goose.getTimestamp());
        if (endIndex < 0 || endIndex + 1 < numCycleMsgs) {
            return null;
        }

        Sv[] cycle = new Sv[numCycleMsgs];
        int start = endIndex - numCycleMsgs + 1;
        for (int i = 0; i < numCycleMsgs; i++) {
            cycle[i] = svs.get(start + i);
        }
        SVCycle result = new SVCycle(cycle);
        result.computeMetrics();
        return result;
    }

    /** Returns the original-list index of the last GOOSE strictly before the SV. */
    public static int getCorrespondingGoose(ArrayList<Goose> gooses, Sv sv) {
        int bestIndex = -1;
        double bestTimestamp = Double.NEGATIVE_INFINITY;
        double target = sv.getTime();
        for (int i = 0; i < gooses.size(); i++) {
            double ts = gooses.get(i).getTimestamp();
            if (ts < target && ts > bestTimestamp) {
                bestTimestamp = ts;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static int lastSvBefore(ArrayList<Sv> svs, double gooseTimestamp) {
        int low = 0;
        int high = svs.size() - 1;
        int result = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            double svTime = svs.get(mid).getTime();
            if (svTime < gooseTimestamp) {
                result = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }

    private static int lastSvBeforeFrameList(ArrayList<EthernetFrame> svs, double gooseTimestamp) {
        int low = 0;
        int high = svs.size() - 1;
        int result = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            EthernetFrame frame = svs.get(mid);
            if (!(frame instanceof Sv)) {
                throw new IllegalArgumentException("processBusMessages must contain only Sv frames.");
            }
            double svTime = ((Sv) frame).getTime();
            if (svTime < gooseTimestamp) {
                result = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }

    private static void validateCycleSize(int numCycleMsgs) {
        if (numCycleMsgs <= 0) {
            throw new IllegalArgumentException("numCycleMsgs must be positive.");
        }
    }
}
