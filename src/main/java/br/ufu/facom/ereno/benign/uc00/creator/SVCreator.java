package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class SVCreator implements MessageCreator {
    private float offset = 0;
    private final String[] payloadFiles;
    private MergingUnit mu; // This is the samambaia (sb) substation MU
    private ArrayList<Float[]> allElectricalMeassures;
    String columnsTitle[] = {
            "Time",
            "isbA", "isbB", "isbC",  // Current substation Samambaia
            "ismA", "ismB", "ismC",  // Current substation Serra da mesa
            "vsbA", "vsbB", "vsbC",  // Voltage Samambaia
            "vsmA", "vsmB", "vsmC", " "}; // Voltage substation Serra da mesa

    public SVCreator(String[] payloadFiles) {
        this.payloadFiles = payloadFiles;
    }

    @Override
    public void generate(IED ied, int numberOfSVMessages) {
        this.mu = (MergingUnit) ied;

        this.allElectricalMeassures = consumeFloatFiles(payloadFiles, columnsTitle);

        Logger.getLogger("SVCreator.generate()").info("Generating " + numberOfSVMessages + " SV message.");
        Logger.getLogger("SVCreator.generate()").info(+allElectricalMeassures.size() + " lines available.");
        int countMessages = 0;
        while (mu.getMessages().size() < numberOfSVMessages) {
            for (Float[] lines : allElectricalMeassures) {
                countMessages = countMessages + 1;
                if (countMessages % 4763 == 0) {
                    offset = offset + 1;
//                    Logger.getLogger(" ").info(+countMessages+" messages SV. Finished one electrical file: 4763 multiple: " + countMessages / 4763);
                }
                if (mu.getMessages().size() < numberOfSVMessages) {
                    mu.addMessage(new Sv(offset + lines[0], lines[1], lines[2], lines[3], lines[7], lines[8], lines[9]));
                } else {
//                    Logger.getLogger("SVCreator.generate()").info(+mu.getMessages().size() + " SV messages generated.");
                    break;
                }
            }

        }
    }

    protected ArrayList<Float[]> consumeFloatFiles(String[] files, String[] columns) {
        ArrayList<Float[]> formatedCSVFile = new ArrayList<>();

        for (String file : files) {
            try {
                File myObj = new File(file);
                System.out.println("ANALISANDO " + myObj.getName());
                try (Scanner myReader = new Scanner(myObj)) {
                    myReader.nextLine(); // Skip blank line
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        if (data.length() > 1) {
                            // Trim leading spaces
                            data = data.trim();

                            // Replace multiple spaces with a single space
                            data = data.replaceAll("\\s+", " ");

                            // Tokenize the line by splitting it based on space
                            String[] tokens = data.split(" ");

                            // Ensure the number of tokens corresponds to columns length
                            if (tokens.length == columns.length) {
                                Float[] tokenLine = new Float[columns.length];

                                // Process each token
                                for (int i = 0; i < tokens.length; i++) {
                                    String next = tokens[i].trim();
                                    if (!next.isEmpty()) {
                                        try {
                                            // Parse the token into a float
                                            float feature = getFeature(columns, next, i);

                                            // Assign to the correct column
                                            tokenLine[i] = feature;
                                        } catch (NumberFormatException e) {
                                            System.out.println("Warning: Invalid float value '" + next + "' in file " + file);
                                        }
                                    }
                                }

                                // Add the processed line to the result
                                formatedCSVFile.add(tokenLine);
                            } else {
                                System.out.println("Warning: Line has an incorrect number of tokens (" + tokens.length + ") in file " + file);
                                System.out.println(Arrays.toString(tokens));

                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Erro: " + e.getLocalizedMessage());
                }
            } catch (Exception e) {
                System.out.println("Error processing file " + file + ": " + e.getMessage());
            }
        }

        return formatedCSVFile;
    }

    private float getFeature(String[] columns, String next, int i) {
        float feature = Float.parseFloat(next) * 1; // Adjust scale if needed

        // Apply timestamp adjustment if necessary
        if (mu.getInitialTimestamp() > 0) {
            if (columns[i].equalsIgnoreCase("Time")) {
                feature = feature + mu.getInitialTimestamp();
            }
        }
        return feature;
    }

}
