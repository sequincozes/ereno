package br.ufu.facom.ereno.messages;

/**
 * Sampled Value sample. The timestamp stored in {@code time} is absolute in the
 * ERENO simulation timeline. Correlation code must never mutate it to align a
 * GOOSE sample, otherwise future/duplicated offsets can leak into features.
 */
public class Sv extends EthernetFrame {
    private float time;
    private float iA;
    private float iB;
    private float iC;
    private float vA;
    private float vB;
    private float vC;
    private String status;

    public Sv(float time, float iA, float iB, float iC, float vA, float vB, float vC) {
        this(time, iA, iB, iC, vA, vB, vC, null);
    }

    public Sv(float time, float iA, float iB, float iC, float vA, float vB, float vC, String status) {
        this.time = time;
        this.iA = iA;
        this.iB = iB;
        this.iC = iC;
        this.vA = vA;
        this.vB = vB;
        this.vC = vC;
        this.status = status;
        setTimestamp(time);
    }

    public String asCsv() {
        return getTime() + "," + getiA() + "," + getiB() + "," + getiC() + "," + getvA() + "," + getvB() + "," + getvC();
    }

    public String getCSVHeader() {
        return "time,iA,iB,iC,vA,vB,vC";
    }

    @Override
    public String toString() {
        if (status == null) {
            return getTime() + "," + iA + "," + iB + "," + iC + "," + vA + "," + vB + "," + vC;
        }
        return getTime() + "," + iA + "," + iB + "," + iC + "," + vA + "," + vB + "," + status;
    }

    public float getTime() {
        return time;
    }

    /**
     * Retained only for source compatibility with older code. SV timestamps are
     * already absolute, so correlation must not inject an additional offset.
     */
    @Deprecated
    public void setRelativeness(int ignored) {
        // Intentionally no-op.
    }

    public void setTime(float time) {
        this.time = time;
        setTimestamp(time);
    }

    public float getiA() {
        return iA;
    }

    public void setiA(float iA) {
        this.iA = iA;
    }

    public float getiB() {
        return iB;
    }

    public void setiB(float iB) {
        this.iB = iB;
    }

    public float getiC() {
        return iC;
    }

    public void setiC(float iC) {
        this.iC = iC;
    }

    public float getvA() {
        return vA;
    }

    public void setvA(float vA) {
        this.vA = vA;
    }

    public float getvB() {
        return vB;
    }

    public void setvB(float vB) {
        this.vB = vB;
    }

    public float getvC() {
        return vC;
    }

    public void setvC(float vC) {
        this.vC = vC;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
