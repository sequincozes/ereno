package br.ufu.facom.ereno.featureEngineering;

import br.ufu.facom.ereno.messages.Sv;

import java.util.ArrayList;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class SVCycle {

    private final Sv[] cycle;
    private double iASumTrapArea = 0;
    private double iBSumTrapArea = 0;
    private double iCSumTrapArea = 0;
    private double vASumTrapArea = 0;

    private double vBSumTrapArea = 0;
    private double vCSumTrapArea = 0;
    private double iARMS = 0;
    private double iBRMS = 0;
    private double iCRMS = 0;
    private double vARMS = 0;
    private double vBRMS = 0;
    double vCRMS = 0;

    private double iAmean = 0.0, iAroot = 0.0;
    private double iBmean = 0.0, iBroot = 0.0;
    private double iCmean = 0.0, iCroot = 0.0;
    private double vAmean = 0.0, vAroot = 0.0;
    private double vBmean = 0.0, vBroot = 0.0;
    private double vCmean = 0.0, vCroot = 0.0;

    public Sv[] getCycle() {
        return cycle;
    }

//    public String getCycleMetricsAsCSV() {
//
//    }

    public double getiASumTrapArea() {
        return iASumTrapArea;
    }

    public void setiASumTrapArea(double iASumTrapArea) {
        this.iASumTrapArea = iASumTrapArea;
    }

    public double getiBSumTrapArea() {
        return iBSumTrapArea;
    }

    public void setiBSumTrapArea(double iBSumTrapArea) {
        this.iBSumTrapArea = iBSumTrapArea;
    }

    public double getiCSumTrapArea() {
        return iCSumTrapArea;
    }

    public void setiCSumTrapArea(double iCSumTrapArea) {
        this.iCSumTrapArea = iCSumTrapArea;
    }

    public double getvASumTrapArea() {
        return vASumTrapArea;
    }

    public void setvASumTrapArea(double vASumTrapArea) {
        this.vASumTrapArea = vASumTrapArea;
    }

    public double getvBSumTrapArea() {
        return vBSumTrapArea;
    }

    public void setvBSumTrapArea(double vBSumTrapArea) {
        this.vBSumTrapArea = vBSumTrapArea;
    }

    public double getvCSumTrapArea() {
        return vCSumTrapArea;
    }

    public void setvCSumTrapArea(double vCSumTrapArea) {
        this.vCSumTrapArea = vCSumTrapArea;
    }

    public double getiARMS() {
        return iARMS;
    }

    public void setiARMS(double iARMS) {
        this.iARMS = iARMS;
    }

    public double getiBRMS() {
        return iBRMS;
    }

    public void setiBRMS(double iBRMS) {
        this.iBRMS = iBRMS;
    }

    public double getiCRMS() {
        return iCRMS;
    }

    public void setiCRMS(double iCRMS) {
        this.iCRMS = iCRMS;
    }

    public double getvARMS() {
        return vARMS;
    }

    public void setvARMS(double vARMS) {
        this.vARMS = vARMS;
    }

    public double getvBRMS() {
        return vBRMS;
    }

    public void setvBRMS(double vBRMS) {
        this.vBRMS = vBRMS;
    }

    public double getvCRMS() {
        return vCRMS;
    }

    public void setvCRMS(double vCRMS) {
        this.vCRMS = vCRMS;
    }

    public double getiAmean() {
        return iAmean;
    }

    public void setiAmean(double iAmean) {
        this.iAmean = iAmean;
    }

    public double getiAroot() {
        return iAroot;
    }

    public void setiAroot(double iAroot) {
        this.iAroot = iAroot;
    }

    public double getiBmean() {
        return iBmean;
    }

    public void setiBmean(double iBmean) {
        this.iBmean = iBmean;
    }

    public double getiBroot() {
        return iBroot;
    }

    public void setiBroot(double iBroot) {
        this.iBroot = iBroot;
    }

    public double getiCmean() {
        return iCmean;
    }

    public void setiCmean(double iCmean) {
        this.iCmean = iCmean;
    }

    public double getiCroot() {
        return iCroot;
    }

    public void setiCroot(double iCroot) {
        this.iCroot = iCroot;
    }

    public double getvAmean() {
        return vAmean;
    }

    public void setvAmean(double vAmean) {
        this.vAmean = vAmean;
    }

    public double getvAroot() {
        return vAroot;
    }

    public void setvAroot(double vAroot) {
        this.vAroot = vAroot;
    }

    public double getvBmean() {
        return vBmean;
    }

    public void setvBmean(double vBmean) {
        this.vBmean = vBmean;
    }

    public double getvBroot() {
        return vBroot;
    }

    public void setvBroot(double vBroot) {
        this.vBroot = vBroot;
    }

    public double getvCmean() {
        return vCmean;
    }

    public void setvCmean(double vCmean) {
        this.vCmean = vCmean;
    }

    public double getvCroot() {
        return vCroot;
    }

    public void setvCroot(double vCroot) {
        this.vCroot = vCroot;
    }

    public ArrayList<Double>[] getAreaAccumulators() {
        return areaAccumulators;
    }

    public void setAreaAccumulators(ArrayList<Double>[] areaAccumulators) {
        this.areaAccumulators = areaAccumulators;
    }

    public SVCycle(Sv[] cycle) {
        this.cycle = cycle;
    }

    public static SVCycle[] splitOnCycles(ArrayList<Sv> messages, int messagesPerCycle) {
        SVCycle[] cycles = new SVCycle[Integer.valueOf(messages.size() / messagesPerCycle)];
        int cycleIndex = 0;
        int messageIndex = 0;
        Sv[] cycleMessages = new Sv[messagesPerCycle];
        for (Sv sv : messages) {
            cycleMessages[messageIndex++] = sv;
            if (messageIndex < 80) {
                cycles[cycleIndex++] = new SVCycle(cycleMessages);
                cycleMessages = new Sv[messagesPerCycle];
            }
        }
        return cycles;
    }

    public void computeMetrics() {
        // Initialization of square sum metrics
        int iAsquare = 0;
        int iBsquare = 0;
        int iCsquare = 0;
        int vAsquare = 0;
        int vBsquare = 0;
        int vCsquare = 0;
        // End of square sum metrics

        for (int i = 0; i < cycle.length - 1; i++) {
            // Iterates through the cycle SV messages
            Sv sv = cycle[i];

            // Sum of the area under trapezio
            iASumTrapArea += getTrapezioArea(sv.getTime() - 1, sv.getTime(), sv.getiA() - 1, sv.getiA());
            iBSumTrapArea += getTrapezioArea(sv.getTime() - 1, sv.getTime(), sv.getiB() - 1, sv.getiB());
            iCSumTrapArea += getTrapezioArea(sv.getTime() - 1, sv.getTime(), sv.getiC() - 1, sv.getiC());
            vASumTrapArea += getTrapezioArea(sv.getTime() - 1, sv.getTime(), sv.getvA() - 1, sv.getvA());
            vBSumTrapArea += getTrapezioArea(sv.getTime() - 1, sv.getTime(), sv.getvB() - 1, sv.getvB());
            vCSumTrapArea += getTrapezioArea(sv.getTime() - 1, sv.getTime(), sv.getvC() - 1, sv.getvC());

            // RMS Calc
            iAsquare += pow(sv.getiA(), 2);
            iBsquare += pow(sv.getiB(), 2);
            iCsquare += pow(sv.getiC(), 2);
            vAsquare += pow(sv.getvA(), 2);
            vBsquare += pow(sv.getvB(), 2);
            vCsquare += pow(sv.getvC(), 2);


        }

        // Calculate iA Mean.
        iAmean = (iAsquare / (float) (cycle.length));

        // Calculate iA Root.
        iAroot = sqrt(iAmean);

        // Calculate iB Mean.
        iBmean = (iBsquare / (float) (cycle.length));

        // Calculate iB Root.
        iBroot = sqrt(iBmean);

        // Calculate iC Mean.
        iCmean = (iCsquare / (float) (cycle.length));

        // Calculate iC Root.
        iCroot = sqrt(iCmean);

        // Calculate vA Mean.
        vAmean = (vAsquare / (float) (cycle.length));

        // Calculate vA Root.
        vAroot = sqrt(vAmean);

        // Calculate vB Mean.
        vBmean = (vBsquare / (float) (cycle.length));

        // Calculate vB Root.
        vBroot = sqrt(vBmean);

        // Calculate vC Mean.
        vCmean = (vCsquare / (float) (cycle.length));

        // Calculate vC Root.
        vCroot = sqrt(vCmean);

    }

    private double getTrapezioArea(double x0, double x1, double fx0, double fx1) {
        double trap;
        trap = (fx1 + fx0) * (x1 - x0) / 2.00;
        return trap;
    }


    ArrayList<Double> areaAccumulators[] = new ArrayList[]{new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>(), new ArrayList<Double>()};

    public String asCsv() {
        return iARMS + "," + iBRMS + "," + iCRMS + "," +
                vARMS + "," + vBRMS + "," + vCRMS + "," +
                iASumTrapArea + "," + iBSumTrapArea + "," + iCSumTrapArea + "," +
                vASumTrapArea + "," + vBSumTrapArea + "," + vCSumTrapArea;

    }
}
