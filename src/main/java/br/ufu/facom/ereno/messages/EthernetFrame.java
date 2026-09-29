package br.ufu.facom.ereno.messages;

/**
 * Base representation of a timestamped Ethernet frame.
 *
 * <p>The natural ordering is chronological and obeys the Comparable contract,
 * including equality for frames with the same timestamp. This is important
 * because ERENO uses PriorityQueue to build the observable station-bus stream.</p>
 */
public class EthernetFrame implements Comparable<EthernetFrame> {

    private double timestamp;

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public double getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(EthernetFrame other) {
        return Double.compare(this.timestamp, other.timestamp);
    }
}
