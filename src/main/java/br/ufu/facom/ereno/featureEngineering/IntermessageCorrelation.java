package br.ufu.facom.ereno.featureEngineering;

import br.ufu.facom.ereno.messages.Goose;

/**
 * Computes inter-message consistency features between two {@link br.ufu.facom.ereno.messages.Goose} messages.
 *
 * These features capture differences in sequence numbers, status, length, timestamps,
 * and control block status, which are useful for detecting anomalies or inconsistencies.
 *
 * @see br.ufu.facom.ereno.messages.Goose
 */

public class IntermessageCorrelation {
    public static String getConsistencyFeaturesAsCSV(Goose gm, Goose prev) {
        int stDiff = gm.getStNum() - prev.getStNum();
        int sqDiff = gm.getSqNum() - prev.getSqNum();
        int gooseLenghtDiff = gm.getGooseLen() - prev.getGooseLen();
        int cbStatusDiff = 0;
        if (gm.isCbStatus() != prev.isCbStatus()) {
            cbStatusDiff = 1;
        }
        int apduSizeDiff = gm.getApduSize() - prev.getApduSize();
        int frameLenthDiff = gm.getFrameLen() - prev.getFrameLen();
        double timestampDiff = gm.getTimestamp() - prev.getTimestamp();
        double tDiff = (Double.valueOf(gm.getT()) - Double.valueOf(prev.getT()));
        double timeFromLastChange = (gm.getTimestamp() - gm.getT());


        return stDiff + ", " + sqDiff + ", " + gooseLenghtDiff + ", " + cbStatusDiff + ", " + apduSizeDiff + ", "
                + frameLenthDiff + ", " + timestampDiff + ", " + tDiff + ", " + timeFromLastChange;
    }

}
