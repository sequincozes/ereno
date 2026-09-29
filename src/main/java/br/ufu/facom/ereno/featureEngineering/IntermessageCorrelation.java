package br.ufu.facom.ereno.featureEngineering;

import br.ufu.facom.ereno.messages.Goose;

/** Features derived only from the current frame and its observable predecessor. */
public final class IntermessageCorrelation {
    private IntermessageCorrelation() {
    }

    public static String getConsistencyFeaturesAsCSV(Goose current, Goose previous) {
        if (current == null || previous == null) {
            throw new IllegalArgumentException("Current and previous GOOSE frames are required.");
        }

        int stDiff = current.getStNum() - previous.getStNum();
        int sqDiff = current.getSqNum() - previous.getSqNum();
        int gooseLengthDiff = current.getGooseLen() - previous.getGooseLen();
        int cbStatusDiff = current.getCbStatus() == previous.getCbStatus() ? 0 : 1;
        int apduSizeDiff = current.getApduSize() - previous.getApduSize();
        int frameLengthDiff = current.getFrameLen() - previous.getFrameLen();
        double timestampDiff = current.getTimestamp() - previous.getTimestamp();
        double tDiff = current.getT() - previous.getT();
        double timeFromLastChange = current.getTimestamp() - current.getT();

        return stDiff + "," + sqDiff + "," + gooseLengthDiff + "," + cbStatusDiff + ","
                + apduSizeDiff + "," + frameLengthDiff + "," + timestampDiff + ","
                + tDiff + "," + timeFromLastChange;
    }
}
