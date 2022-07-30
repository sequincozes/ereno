package br.ufu.facom.ereno.standard.devices;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MergingUnit extends IED {
    public static boolean generateSingleRound = false;
    public static int runs = 132;
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
                                        feature = feature + initialTimestamp;
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
