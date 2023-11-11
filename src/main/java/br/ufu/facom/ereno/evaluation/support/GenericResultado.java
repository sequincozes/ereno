/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.evaluation.support;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author sequi
 */
public class GenericResultado {

    String Cx;
    public float VP, FN, VN, FP;
    //long Time;
    double acuracia, recall, precision, f1score;
    double cpuLoad, memoryLoad;
    double varianceTime, stdDvTime, loConfIntTime, hiConfIntTime;
    ArrayList<Long> detailedClassificationTime;
    private float nanotime;
    private int testDatasetSize;
    double avgTime;
    @Deprecated
    float Time;
    public double microtime;
    public int[] usedFS;

    GenericResultado(String cx, float VP, float FN, float VN, float FP, double avgTime, double acuracia, double recall, double precision, double f1score) {
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
        this.Cx = cx;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.FP = FP;
        this.acuracia = acuracia;
        this.recall = recall;
        this.precision = precision;
        this.f1score = f1score;
        this.avgTime = avgTime;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
    }

    GenericResultado(String cx, float VP, float FN, float VN, float FP, double avgTime, double acuracia, double recall, double precision, double f1score, int[] usedFS) {
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
        this.usedFS = usedFS;
        this.Cx = cx;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.FP = FP;
        this.acuracia = acuracia;
        this.recall = recall;
        this.precision = precision;
        this.f1score = f1score;
        this.avgTime = avgTime;
    }

    public void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public int getTestDatasetSize() {
        return testDatasetSize;
    }

    public float getNanotime() {
        return nanotime;
    }

    public GenericResultado(double evaluation) {
        this.acuracia = evaluation;
        this.f1score = evaluation;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
    }

    public GenericResultado(GenericResultado evaluation) {
        this.Cx = evaluation.getCx();
        this.VP = evaluation.getVP();
        this.FN = evaluation.getFN();
        this.VN = evaluation.getVN();
        this.FP = evaluation.getFP();
        this.Time = evaluation.getTime();
        this.acuracia = evaluation.getAcuracia();
        this.recall = evaluation.getRecall();
        this.precision = evaluation.getPrecision();
        this.f1score = evaluation.getF1Score();
        this.cpuLoad = evaluation.getCpuLoad();
        this.memoryLoad = evaluation.getMemoryLoad();
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
    }

    GenericResultado() {

    }

    GenericResultado(String cx, float VP, float FN, float VN, float FP, double avgTime, double acuracia, double recall, double f1score, double varianceTime, double stdDvTime, double loConfIntTime, double hiConfIntTime) {
        this.Cx = cx;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.FP = FP;
        this.avgTime = avgTime;
        this.acuracia = acuracia;
        this.recall = recall;
        this.f1score = f1score;
        this.varianceTime = varianceTime;
        this.stdDvTime = stdDvTime;
        this.loConfIntTime = loConfIntTime;
        this.hiConfIntTime = hiConfIntTime;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
    }

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }
    public int[][] confusionMatrix = new int[GeneralParameters.NUM_CLASSES][GeneralParameters.NUM_CLASSES];

    public GenericResultado(String Cx, float VP, float FN, float VN, float FP, long Time, double acuracia, double txDet, double txAFal, double cpuLoad, double memoryLoad) {
        this.Cx = Cx;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.FP = FP;
        this.Time = Time;
        this.acuracia = acuracia;
        this.recall = txDet;
        this.precision = txAFal;
        this.cpuLoad = cpuLoad;
        this.memoryLoad = memoryLoad;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
    }

    public GenericResultado(String descricao, float VP, float FN, float VN, float FP, double acuracia, double recall, double precision, double f1score) {
        this.Cx = descricao;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.FP = FP;
        this.acuracia = acuracia;
        this.recall = recall;
        this.precision = precision;
        this.f1score = f1score;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
    }

    public GenericResultado(String descricao, float VP, float FN, float VN, float FP, float time) {
        this.Cx = descricao;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.FP = FP;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        this.microtime = time;
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
        recalcular();
    }

    public GenericResultado(String descricao, float VP, float FN, float VN, float FP, float nanotime, int[][] confusionMatrix) {
        this.Cx = descricao;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        this.FP = FP;
        this.nanotime = nanotime;
        this.microtime = nanotime / 1000;
        this.Time = nanotime / 1000;
        this.confusionMatrix = confusionMatrix;
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
        recalcular();
    }

    public GenericResultado(String descricao, float VP, float FN, float VN, float FP) {
        this.Cx = descricao;
        this.VP = VP;
        this.FN = FN;
        this.VN = VN;
        this.usedFS = GeneralParameters.FEATURE_SELECTION.clone();
        this.FP = FP;
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("New result = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + ")");
        }
    }

    public GenericResultado recalcular() {

        this.acuracia = Float.valueOf(((getVP() + getVN()) * 100) / (getVP() + getVN() + getFP() + getFN())) / 100;
        this.recall = Float.valueOf((getVP() * 100) / (getVP() + getFN())) / 100;
        this.precision = Float.valueOf((getVP() * 100) / (getVP() + getFP())) / 100;

        this.f1score = Float.valueOf(
                (float) (2 * ((recall * precision) / (recall + precision)))
        );
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("Recalcular = VP (" + VP + "), VN (" + VN + "), FP (" + FP + "), FN (" + FN + "), "
                    + "Acc (" + getAcuracia() + "), F1 (" + getF1Score() + ")"
                    + "Recalll (" + getRecall() + ")," + "Precision (" + getPrecision() + ")");
            System.out.println("Soma: " + getRecall() + getPrecision());
            System.out.println("Multi: " + getRecall() * getPrecision());
            System.out.println("Multi/Soma: " + (getRecall() * getPrecision()) / (getRecall() + getPrecision()));
            System.out.println("Multi/Soma X 2: " + 2 * (getRecall() * getPrecision()) / (getRecall() + getPrecision()));

        }
        return this;
    }

    public String getCx() {
        return Cx;
    }

    public void setCx(String Cx) {
        this.Cx = Cx;
    }

    public float getVP() {
        return VP;
    }

    public void setVP(float VP) {
        this.VP = VP;
    }

    public float getFN() {
        return FN;
    }

    public void setFN(float FN) {
        this.FN = FN;
    }

    public float getVN() {
        return VN;
    }

    public void setVN(float VN) {
        this.VN = VN;
    }

    public float getFP() {
        return FP;
    }

    public void setFP(float FP) {
        this.FP = FP;
    }

    public float getTime() {
        return Time;
    }

    public void setTime(long Time) {
        this.Time = Time;
    }

    public double getAcuracia() {
        /*  String acc = String.valueOf(acuracia);
        if (acc.length() > 6) {
            acc = acc.substring(0, 7);
        }
        acuracia = Double.valueOf(acc);
        return acuracia;
         */
        DecimalFormat numberFormat = new DecimalFormat("#.#######");
        double cuttedAccuracy = Double.valueOf(numberFormat.format(acuracia).replace(",", "."));
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("ACC: " + acuracia + " = " + cuttedAccuracy);
        }

        return cuttedAccuracy;
    }

    public void printResults(String nome, boolean persistOnDisk) throws IOException {
        System.out.print(nome + "|");
        double cpu = getCpuLoad();
        double memory = getMemoryLoad();

        String cpuS = "";
        String memoryS = "";

        try {
            cpuS = String.valueOf(cpu).substring(0, 4);
        } catch (Exception e) {
            System.out.println("Errinho: " + e.getLocalizedMessage());
        }

        try {
            memory = memory / 1000;
            memoryS = String.valueOf(memory).substring(0, 4) + "K";
        } catch (Exception e) {
            System.out.println("Errinho: " + e.getLocalizedMessage());
        }

//        System.out.print("CPU: (" + cpuS + "%), Memória: " + memoryS + " Tempo: " + getTime() + ", "
        System.out.print("Acc: " + acuracia);
        try {
            System.out.print(" Tx. VP: " + String.valueOf(recall).substring(0, 7).replace(".", ","));
            System.out.print(" Tx. FP: " + String.valueOf(precision).substring(0, 7).replace(".", ","));
        } catch (Exception e) {
            System.out.print(" Tx. VP: " + String.valueOf(recall).replace(".", ","));
            System.out.print(" Tx. FP: " + String.valueOf(precision).replace(".", ","));
        }
        System.out.print(" (VN: " + VN);
        System.out.print(" VP: " + VP);
        System.out.print(" FN: " + FN);
        System.out.println(" FP: " + FP + ")");
        if (persistOnDisk) {
            FileWriter arq = new FileWriter("d:\\resultados.txt", true);
            PrintWriter gravarArq = new PrintWriter(arq);
            gravarArq.append(Cx + "| CPU: " + cpuS + "%|, Memória: " + memoryS + " |Tempo: " + getTime() + "|, "
                    + "Acc: " + acuracia + "%s" + ""
                    + " (VN: " + VN + " VP: " + VP + " FN: " + FN + " FP: " + FP + ")\r\n");
            arq.close();
        }
    }

    public void printResultsHeader() {
        System.out.println("\nClassifier;Accuracy;Precision;Recall;F1Score;VP;VN;FP;FN;time");
    }

    public void printResults() {
        recalcular();
        System.out.println(
                getCx() + ";"
                + getAcuracia() + ";"
                + getPrecision() + ";"
                + getRecall() + ";"
                + getF1Score() + ";"
                + getVP() + ";"
                + getVN() + ";"
                + getFP() + ";"
                + getFN() + ";"
                + getAvgTime() + ";"
                + Arrays.toString(usedFS)
        );
    }

    public String printResultsGetString() {
        recalcular();
        return getCx() + ";"
                + getAcuracia() + ";"
                + getPrecision() + ";"
                + getRecall() + ";"
                + getF1Score() + ";"
                + getVP() + ";"
                + getVN() + ";"
                + getFP() + ";"
                + getFN() + ";"
                + getAvgTime() + ";"
                + Arrays.toString(usedFS) + ";";

    }

    public void printDetailedResults() {
        recalcular();
        System.out.println(
                getCx() + ";"
                + getAcuracia() + ";"
                + getPrecision() + ";"
                + getRecall() + ";"
                + getF1Score() + ";"
                + getVP() + ";"
                + getVN() + ";"
                + getFP() + ";"
                + getFN() + ";"
                + getTime() + ";"
                + stdDvTime + ";"
                + hiConfIntTime + ";"
                + loConfIntTime + ";"
        );
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public void setMemoryLoad(double memoryLoad) {
        this.memoryLoad = memoryLoad;
    }

    public double getMemoryLoad() {
        return memoryLoad;
    }

    public void printIterations(String nome, String dirietorioParaGravar) throws IOException {
        System.out.print("CLASSIFICADOR" + "	");
        System.out.print("ACURÁCIA" + "	");
        System.out.print("DETECÇÃO" + "	");
        System.out.print("ALARMES FALSOS" + "	");

        FileWriter arq = new FileWriter(dirietorioParaGravar + ":\\resultados_" + nome + "_.txt", true);
        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.append(nome + "	"
                + acuracia + "	"
                + (VP / (VP + FN)) + "	"
                + (FP / (FP + VN)) + "	"
                + "\r\n");
        arq.close();
    }

    public double getRecall() {
        return recall;
    }

    public double getF1Score() {
        if (!(f1score > 0)) {
            return 0;
        }
        DecimalFormat numberFormat = new DecimalFormat("#.#######");
        double cuttedF1 = Double.valueOf(numberFormat.format(f1score).replace(",", "."));
        if (GeneralParameters.DEBUG_MODE) {
            System.out.println("F1: " + f1score + " = " + cuttedF1);
        }

        return cuttedF1;
    }

    public void setRecall(double taxaDeteccao) {
        this.recall = taxaDeteccao;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double taxaAlarmeFalsos) {
        this.precision = taxaAlarmeFalsos;
    }

    public void printDetailedTime() {
        System.out.println(
                getCx() + ";"
                + getAvgTime() + ";"
                + hiConfIntTime + ";"
                + loConfIntTime + ";"
        );
    }

    public void printDetailedTime(String details) {
        System.out.println(
                details + ";"
                + getAvgTime() + ";"
                + hiConfIntTime + ";"
                + loConfIntTime + ";"
        );
    }

    public void setTestSize(int size) {
        this.testDatasetSize = size;
    }
}
