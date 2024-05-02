package br.ufu.facom.ereno.messages;

public class EthernetFrame implements Comparable<EthernetFrame> {

    private double timestamp;               // DYNAMICALLY GENERATED
    public void setTimestamp(double timestamp) {
        this.timestamp = (timestamp);
    }
    public double getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int compareTo(EthernetFrame frame) {
        if (frame.getTimestamp() >= getTimestamp()) {
            return -1;
        } else {
            return 1;
        }
    }

}
