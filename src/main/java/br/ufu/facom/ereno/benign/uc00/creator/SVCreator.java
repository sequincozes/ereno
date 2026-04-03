package br.ufu.facom.ereno.benign.uc00.creator;

import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Generator of Sampled Value (SV) messages based on electrical measurement CSV files.
 *
 * This class reads pre-recorded measurement data from CSV payloads and simulates a Merging Unit (MU)
 * behavior by generating SV messages with synchronized timestamps. It supports:
 * - Scalable reading and parsing of measurement data
 * - Offset-based timestamp alignment to support multi-cycle simulations
 * - Controlled generation of a specific number of SV messages
 *
 * Messages include selected current and voltage components and are injected into an IED instance
 * for use in benign test scenarios (UC00). Suitable for real-time simulations and system evaluation.
 *
 * @see br.ufu.facom.ereno.messages.Sv
 * @see br.ufu.facom.ereno.benign.uc00.devices.MergingUnit
 * @see br.ufu.facom.ereno.dataExtractors.DatasetWriter
 */

public class SVCreator implements MessageCreator {
    private float offset = 0;
    private final String[] payloadFiles;
    private MergingUnit mu;
    String[] columnsTitle = {
            "Time",
            "isbA", "isbB", "isbC",
            "ismA", "ismB", "ismC",
            "vsbA", "vsbB", "vsbC",
            "vsmA", "vsmB", "vsmC", " "};

    public SVCreator(String[] payloadFiles) {
        this.payloadFiles = payloadFiles;
    }

    @Override
    public void generate(IED ied, int numberOfSVMessages) {
        this.mu = (MergingUnit) ied;

        ArrayList<Float[]> allElectricalMeassures = parseElectricalMeasurementsFromCsv(payloadFiles, 1, columnsTitle);

        Logger.getLogger("SVCreator.generate()").info("Generating " + numberOfSVMessages + " SV message.");
        Logger.getLogger("SVCreator.generate()").info(+allElectricalMeassures.size() + " lines available.");
        int countMessages = 0;
        while (mu.getMessages().size() < numberOfSVMessages) {
            System.out.println(mu.getMessages().size()+" < "+numberOfSVMessages);

            for (Float[] lines : allElectricalMeassures) {
                countMessages = countMessages + 1;
                if (countMessages % 4763 == 0) {
                    offset = offset + 1;
//                    Logger.getLogger(" ").info(+countMessages+" messages SV. Finished one electrical file: 4763 multiple: " + countMessages / 4763);
                }
                if (mu.getMessages().size() < numberOfSVMessages) {
                    mu.addMessage(new Sv(offset + lines[0], lines[1], lines[2], lines[3], lines[7], lines[8], lines[9]));
                } else {
                    Logger.getLogger("SVCreator.generate()").info(+mu.getMessages().size() + " SV messages generated.");
                    break;
                }
            }

        }
    }

    protected ArrayList<Float[]> parseElectricalMeasurementsFromCsv(String[] files, int scale, String columns[]) {
        ArrayList<Float[]> formatedCSVFile = new ArrayList<>();

        for (String file : files) {
            try {
                File myObj = new File(file);
                try (Scanner myReader = new Scanner(myObj)) {
                    myReader.nextLine();
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
                                    if (mu.getInitialTimestamp()>0) {
                                        if (columns[column].equalsIgnoreCase("Time")) {
                                            feature = feature + mu.getInitialTimestamp();
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
        }
        return formatedCSVFile;
    }

}
