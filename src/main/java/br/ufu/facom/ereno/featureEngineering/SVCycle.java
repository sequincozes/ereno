package br.ufu.facom.ereno.featureEngineering;

import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

/** Metrics computed over a causal window of SV samples. */
public class SVCycle {
    private final Sv[] cycle;

    private double iASumTrapArea;
    private double iBSumTrapArea;
    private double iCSumTrapArea;
    private double vASumTrapArea;
    private double vBSumTrapArea;
    private double vCSumTrapArea;

    private double iAmean;
    private double iAroot;
    private double iBmean;
    private double iBroot;
    private double iCmean;
    private double iCroot;
    private double vAmean;
    private double vAroot;
    private double vBmean;
    private double vBroot;
    private double vCmean;
    private double vCroot;

    @SuppressWarnings("unchecked")
    private ArrayList<Double>[] areaAccumulators = new ArrayList[]{
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
    };

    public SVCycle(Sv[] cycle) {
        if (cycle == null || cycle.length == 0) {
            throw new IllegalArgumentException("SVCycle requires at least one sample.");
        }
        for (Sv sv : cycle) {
            if (sv == null) {
                throw new IllegalArgumentException("SVCycle cannot contain null samples.");
            }
        }
        this.cycle = cycle;
    }

    public static SVCycle[] splitOnCycles(ArrayList<Sv> messages, int messagesPerCycle) {
        if (messagesPerCycle <= 0) {
            throw new IllegalArgumentException("messagesPerCycle must be positive.");
        }
        int completeCycles = messages.size() / messagesPerCycle;
        SVCycle[] cycles = new SVCycle[completeCycles];
        for (int c = 0; c < completeCycles; c++) {
            Sv[] samples = new Sv[messagesPerCycle];
            int base = c * messagesPerCycle;
            for (int i = 0; i < messagesPerCycle; i++) {
                samples[i] = messages.get(base + i);
            }
            cycles[c] = new SVCycle(samples);
        }
        return cycles;
    }

    public void computeMetrics() {
        resetMetrics();

        double iASquare = 0.0;
        double iBSquare = 0.0;
        double iCSquare = 0.0;
        double vASquare = 0.0;
        double vBSquare = 0.0;
        double vCSquare = 0.0;

        for (int i = 0; i < cycle.length; i++) {
            Sv current = cycle[i];
            iASquare += current.getiA() * current.getiA();
            iBSquare += current.getiB() * current.getiB();
            iCSquare += current.getiC() * current.getiC();
            vASquare += current.getvA() * current.getvA();
            vBSquare += current.getvB() * current.getvB();
            vCSquare += current.getvC() * current.getvC();

            if (i > 0) {
                Sv previous = cycle[i - 1];
                iASumTrapArea += trapezoid(previous.getTime(), current.getTime(), previous.getiA(), current.getiA());
                iBSumTrapArea += trapezoid(previous.getTime(), current.getTime(), previous.getiB(), current.getiB());
                iCSumTrapArea += trapezoid(previous.getTime(), current.getTime(), previous.getiC(), current.getiC());
                vASumTrapArea += trapezoid(previous.getTime(), current.getTime(), previous.getvA(), current.getvA());
                vBSumTrapArea += trapezoid(previous.getTime(), current.getTime(), previous.getvB(), current.getvB());
                vCSumTrapArea += trapezoid(previous.getTime(), current.getTime(), previous.getvC(), current.getvC());
            }
        }

        double n = cycle.length;
        iAmean = iASquare / n;
        iBmean = iBSquare / n;
        iCmean = iCSquare / n;
        vAmean = vASquare / n;
        vBmean = vBSquare / n;
        vCmean = vCSquare / n;

        iAroot = sqrt(iAmean);
        iBroot = sqrt(iBmean);
        iCroot = sqrt(iCmean);
        vAroot = sqrt(vAmean);
        vBroot = sqrt(vBmean);
        vCroot = sqrt(vCmean);
    }

    private void resetMetrics() {
        iASumTrapArea = iBSumTrapArea = iCSumTrapArea = 0.0;
        vASumTrapArea = vBSumTrapArea = vCSumTrapArea = 0.0;
        iAmean = iAroot = iBmean = iBroot = iCmean = iCroot = 0.0;
        vAmean = vAroot = vBmean = vBroot = vCmean = vCroot = 0.0;
    }

    private static double trapezoid(double x0, double x1, double y0, double y1) {
        return (y0 + y1) * (x1 - x0) / 2.0;
    }

    public String asCsv() {
        return iAroot + "," + iBroot + "," + iCroot + ","
                + vAroot + "," + vBroot + "," + vCroot + ","
                + iASumTrapArea + "," + iBSumTrapArea + "," + iCSumTrapArea + ","
                + vASumTrapArea + "," + vBSumTrapArea + "," + vCSumTrapArea;
    }

    public String getCsvHeader() {
        return "iARMS,iBRMS,iCRMS,vARMS,vBRMS,vCRMS,iASumTrapArea,iBSumTrapArea,iCSumTrapArea,vASumTrapArea,vBSumTrapArea,vCSumTrapArea";
    }

    public Sv[] getCycle() { return cycle; }
    public double getiASumTrapArea() { return iASumTrapArea; }
    public void setiASumTrapArea(double v) { iASumTrapArea = v; }
    public double getiBSumTrapArea() { return iBSumTrapArea; }
    public void setiBSumTrapArea(double v) { iBSumTrapArea = v; }
    public double getiCSumTrapArea() { return iCSumTrapArea; }
    public void setiCSumTrapArea(double v) { iCSumTrapArea = v; }
    public double getvASumTrapArea() { return vASumTrapArea; }
    public void setvASumTrapArea(double v) { vASumTrapArea = v; }
    public double getvBSumTrapArea() { return vBSumTrapArea; }
    public void setvBSumTrapArea(double v) { vBSumTrapArea = v; }
    public double getvCSumTrapArea() { return vCSumTrapArea; }
    public void setvCSumTrapArea(double v) { vCSumTrapArea = v; }
    public double getiAmean() { return iAmean; }
    public void setiAmean(double v) { iAmean = v; }
    public double getiAroot() { return iAroot; }
    public void setiAroot(double v) { iAroot = v; }
    public double getiBmean() { return iBmean; }
    public void setiBmean(double v) { iBmean = v; }
    public double getiBroot() { return iBroot; }
    public void setiBroot(double v) { iBroot = v; }
    public double getiCmean() { return iCmean; }
    public void setiCmean(double v) { iCmean = v; }
    public double getiCroot() { return iCroot; }
    public void setiCroot(double v) { iCroot = v; }
    public double getvAmean() { return vAmean; }
    public void setvAmean(double v) { vAmean = v; }
    public double getvAroot() { return vAroot; }
    public void setvAroot(double v) { vAroot = v; }
    public double getvBmean() { return vBmean; }
    public void setvBmean(double v) { vBmean = v; }
    public double getvBroot() { return vBroot; }
    public void setvBroot(double v) { vBroot = v; }
    public double getvCmean() { return vCmean; }
    public void setvCmean(double v) { vCmean = v; }
    public double getvCroot() { return vCroot; }
    public void setvCroot(double v) { vCroot = v; }
    public ArrayList<Double>[] getAreaAccumulators() { return areaAccumulators; }
    public void setAreaAccumulators(ArrayList<Double>[] v) { areaAccumulators = v; }
}
