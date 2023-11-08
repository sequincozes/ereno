///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package br.ufu.facom.ereno.featureEngineering;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import uff.midiacom.controller.GooseEventManager;
//
///**
// * @author silvio
// */
//public class UC00 extends AbstractUseCase {
//
//    public static void run(String filename) throws IOException {
//        outputFile = outputLocation + filename;
//        UC00 extractor = new UC00();
//        extractor.startWriting();
//
//        int[] resistences = {10, 50, 100};
//
//        for (int resistence : resistences) {
//            for (int run = 1; run <= AbstractUseCase.runs; run++) {
//                switch (String.valueOf(run).length()) {
//                    case 1:
//                        extractor.runNormalUC00(resistence, "00" + run);
//                        break;
//                    case 2:
//                        extractor.runNormalUC00(resistence, "0" + run);
//                        break;
//                    default:
//                        extractor.runNormalUC00(resistence, String.valueOf(run));
//                        break;
//                }
//            }
//        }
//
//        extractor.finishWriting();
//    }
//
//
//    private void runNormalUC00(int res, String num) throws IOException {
//        float offset = restartCounters();
////        System.out.println("OFFSET: "+offset);
//        gooseEventManager = new GooseEventManager(false, initialStNum, initialSqNum, new double[]{offset + 0.5, offset + 0.6}, 0.00631, offset + 0.01659, 6.33000000000011f, 4, 1000);
//
//        /* Extract First Part */
//        String columns[] = {"Time", "isbA", "isbB", "isbC", "ismA", "ismB", "ismC", "vsbA", "vsbB", "vsbC", "vsmA"};
//        ArrayList<Float[]> formatedCSVFile =
//                consumeFloat("/home/silvio/datasets/Full_SV_2020/resistencia" + res + "/SILVIO_r00" + num + "_01.out",
//                        1, columns);
//
//        /* Extract Second Part */
//        String columns2[] = {"Time", "vsmB", "vsmC"};
//        ArrayList<Float[]> formatedCSVFile2 =
//                consumeFloat("/home/silvio/datasets/Full_SV_2020/resistencia" + res + "/SILVIO_r00" + num + "_02.out",
//                        1, columns2);
//
//        double[] labelRange = {0.5, 0.6};
//
//        /* Write Header and Columns */
//        if (printHeader) {
//            writeDefaultHeader();
//            printHeader = false;
//        }
//
//        /* Write Payload */
//        double lastGooseTimestamp = -1;
//        int numLines = formatedCSVFile2.size() - 1;
//        for (int i = 0; i < numLines; i++) {
//            float time = formatedCSVFile2.get(i)[0];
//
////            DBUG
////            System.out.println(gooseEventManager.getLastGooseFromSV(time).asCSVFull());
////            if (i == 1) {
////                System.exit(0);
////                System.out.println(gooseEventManager.getLastGooseFromSV(time).getStNum() + "/" + gooseEventManager.getLastGooseFromSV(time).getSqNum());
////            }
//
//            String svHist;
//            if (i > 0) {
//                svHist = getSVHistorical(
//                        formatedCSVFile.get(i - 1),
//                        formatedCSVFile.get(i),
//                        formatedCSVFile2.get(i - 1),
//                        formatedCSVFile2.get(i));
//            } else {
//                svHist = getSVHistorical(
//                        formatedCSVFile.get(i),
//                        formatedCSVFile.get(i),
//                        formatedCSVFile2.get(i),
//                        formatedCSVFile2.get(i)); // just to initialize
//            }
//
//            String line = "";
//
//            if (time < labelRange[0] & time < labelRange[1]) {
//                line = joinColumns(formatedCSVFile, formatedCSVFile2, columns, columns2, i) + ","
//                        + svHist + "," + gooseEventManager.getLastGooseFromSV(time).asCSVFull() +
//                        getConsistencyFeaturesAsCSV(gooseEventManager.getLastGooseFromSV(time), time) + "," + label[0];
//            } else if (time < labelRange[1]) {
//                line = joinColumns(formatedCSVFile, formatedCSVFile2, columns, columns2, i) + "," + svHist + "," + gooseEventManager.getLastGooseFromSV(time).asCSVFull() + getConsistencyFeaturesAsCSV(gooseEventManager.getLastGooseFromSV(time), time) + "," + label[0];
//            } else {
//                line = joinColumns(formatedCSVFile, formatedCSVFile2, columns, columns2, i) + "," + svHist + "," + gooseEventManager.getLastGooseFromSV(time).asCSVFull() + getConsistencyFeaturesAsCSV(gooseEventManager.getLastGooseFromSV(time), time) + "," + label[0];
//            }
//            write(line);
//            if (lastGooseTimestamp != gooseEventManager.getLastGooseFromSV(time).getTimestamp()) {
//                lastGooseTimestamp = gooseEventManager.getLastGooseFromSV(time).getTimestamp();
//                System.out.println("[0] "+gooseEventManager.getLastGooseFromSV(time).getT()+" / "+gooseEventManager.getLastGooseFromSV(time).getFrameLen());
//            }
//        }
//    }
//
//
//}
