package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.benign.uc00.devices.IED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class SVCreator implements MessageCreator {
    private static final boolean USE_OFFSET = true;
    private float offset = 0;
    private final String payloadFile;
    private MergingUnit mu; // This is the samambaia (sb) substation MU
    private ArrayList<Float[]> allElectricalMeassures;
    String columnsTitle[] = {
            "Time",
            "isbA", "isbB", "isbC",  // Current substation Samambaia
            "ismA", "ismB", "ismC",  // Current substation Serra da mesa
            "vsbA", "vsbB", "vsbC",  // Voltage Samambaia
            "vsmA", "vsmB", "vsmC"}; // Voltage substation Serra da mesa

    public SVCreator(String payloadFile) {
        this.payloadFile = payloadFile;
    }

    @Override
    public void generate(IED ied, int numberOfSVMessages) {
        this.mu = (MergingUnit) ied;
        this.allElectricalMeassures = consumeFloat(payloadFile, 1, columnsTitle);

        Logger.getLogger("SVCreator.generate()").info("Generating " + numberOfSVMessages + " SV message.");

        while (mu.getMessages().size() < numberOfSVMessages) {
            for (Float[] lines : allElectricalMeassures) {
                if (mu.getMessages().size() < numberOfSVMessages) {
                    mu.addMessage(new Sv(offset + lines[0], lines[1], lines[2], lines[3], lines[7], lines[8], lines[9]));
                } else {
                    Logger.getLogger("SVCreator.generate()").info(+mu.getMessages().size() + " SV messages generated.");
                    break;
                }
            }
            offset = offset + 1;

        }
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
