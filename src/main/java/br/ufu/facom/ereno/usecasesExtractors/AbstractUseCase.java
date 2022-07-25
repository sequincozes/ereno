/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.usecasesExtractors;

import br.ufu.facom.ereno.devices.ProtectionIED;
import br.ufu.facom.ereno.model.GooseMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author silvio
 */
public abstract class AbstractUseCase /*extends SVFeatureComposer*/ {
    public static boolean USE_OFFSET = true;
    static float offset;
    public static int runs = 132;
    public static boolean generateSingleRound = false;

    ProtectionIED ied;
    public static boolean printHeader = false;
    boolean defaultHeader = true;
    static String[] label = {"normal", "random_replay", "inverse_replay", "masquerade_fake_fault", "masquerade_fake_normal", "injection", "high_StNum", "poisoned_high_rate"};//,"poisoned_high_rate_consistent"};
    String columnsGOOSE[] = {"GooseTimestamp", "SqNum", "StNum", "cbStatus", "frameLen", "ethDst", "ethSrc", "ethType", "gooseTimeAllowedtoLive", "gooseAppid", "gooseLen", "TPID", "gocbRef", "datSet", "goID", "test", "confRev", "ndsCom", " numDatSetEntries", "APDUSize", "protocol"};

    static int initialStNum;
    static int initialSqNum;

    public AbstractUseCase() {
        initialStNum = randomBetween(0, 5000);
        initialSqNum = randomBetween(0, 5000);
        if (USE_OFFSET) {
            offset = randomBetween(0, 5000); // offset
        } else {
            offset = 0;
        }
    }

    float restartCounters() {
//        System.out.println("Last counters " + initialSqNum + "/" + initialStNum);
        initialStNum = randomBetween(0, 5000);
        initialSqNum = randomBetween(0, 5000);
//        System.out.println("New counters " + initialSqNum + "/" + initialStNum);
        if (USE_OFFSET) {
            offset = randomBetween(0, 5000); // offset
        } else {
            offset = 0;
        }
        return offset;
    }

    float defineCounters(int initialStNum, int initialSqNum, float offset) {
//        System.out.println("Last counters " + initialSqNum + "/" + initialStNum);
        this.initialStNum = initialStNum;
        this.initialSqNum = initialSqNum;
//        System.out.println("New counters " + initialSqNum + "/" + initialStNum);
        this.offset = offset;
        return offset;
    }

    public static int randomBetween(int min, int max) {
        return new Random(System.nanoTime()).nextInt(max - min) + min;
    }

    protected String joinColumns(ArrayList<Float[]> formatedCSVFile, ArrayList<Float[]> formatedCSVFile2, String columns[], String columns2[], int line) {
        String content = "";
        for (int i = 0; i < columns.length; i++) {
            float value = formatedCSVFile.get(line)[i];
            content = content.concat(value + ",");
        }

        for (int i = 1; i < columns2.length; i++) {
            float value = formatedCSVFile2.get(line)[i];
            if (columns2.length - 1 == i) {
                content = content.concat(String.valueOf(value));
            } else {
                content = content.concat(value + ",");
            }
        }
        return content;
    }

    protected String joinColumns(ArrayList<Float[]> formatedCSVFile, double timeGambi, ArrayList<Float[]> formatedCSVFile2, String columns[], String columns2[], int line) {
        String content = "";
        for (int i = 0; i < columns.length; i++) {
            float value = formatedCSVFile.get(line)[i];
            if (i == 0) {
                content = content.concat(value + timeGambi + ",");
            } else {
                content = content.concat(value + ",");
            }
        }

        for (int i = 1; i < columns2.length; i++) {
            float value = formatedCSVFile2.get(line)[i];
            if (columns2.length - 1 == i) {
                content = content.concat(String.valueOf(value));
            } else {
                content = content.concat(value + ",");
            }
        }
        return content;
    }

    protected ArrayList<Float[]> consumeFloat(String file, int scale, String columns[]) {
//        int offset = randomBetween(0,1000);
        ArrayList<Float[]> formatedCSVFile = new ArrayList<>();
        try {
            File myObj = new File(file);
            try (Scanner myReader = new Scanner(myObj)) {
                myReader.nextLine(); // Skip blank line
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    if (data.length() > 1) {
                        while (data.charAt(0) == ' ') {
                            data = data.substring(1, data.length());
                        }

                        while (data.trim().contains("  ")) {
                            data = data.replace("  ", " ");
                        }

                        data = data.replace(" ", ",");

                        StringTokenizer stringTokenizer = new StringTokenizer(data, ",", true);
                        int t = 0;
                        Float[] tokenLine = new Float[columns.length];
                        while (stringTokenizer.hasMoreTokens()) {
                            t++;
                            String next = stringTokenizer.nextToken();
                            if (!next.contains(",")) {
                                float feature = Float.valueOf(next) * scale;
                                int column = ((t + 1) / 2) - 1;
                                if (USE_OFFSET) {
                                    if (columns[column].equalsIgnoreCase("Time")) {
                                        feature = feature + offset;
                                    }
                                }
                                tokenLine[column] = feature;
                            }
                        }
                        formatedCSVFile.add(tokenLine);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Erro: " + e.getLocalizedMessage());
        }

        return formatedCSVFile;
    }




}
