package br.ufu.facom.ereno.benign.creator;

import br.ufu.facom.ereno.benign.devices.IED;
import br.ufu.facom.ereno.benign.devices.MergingUnit;
import br.ufu.facom.ereno.messages.Sv;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class SVCreator implements MessageCreator {
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
    public void generate(IED ied) {
        this.mu = (MergingUnit) ied;
        this.allElectricalMeassures = consumeFloat(payloadFile, 1, columnsTitle);
        for (Float[] line : allElectricalMeassures) {
            mu.addMessage(new Sv(line[0], line[1], line[2], line[3], line[7], line[8], line[9]));
        }
    }

    public ArrayList<Float[]> consumeFloat(String file, int scale, String columns[]) {
        ArrayList<Float[]> formatedCSVFile = new ArrayList<>();
        try {
            File myObj = new File(file);
            try (Scanner myReader = new Scanner(myObj)) {
                myReader.nextLine(); // Skip blank line
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    if (data.length() > 1) {
                        StringTokenizer stringTokenizer = new StringTokenizer(data, ",", true);
                        int tokenCount = 0;
                        Float[] tokenLine = new Float[columns.length];
                        while (stringTokenizer.hasMoreTokens()) {
                            tokenCount++;
                            String next = stringTokenizer.nextToken();
                            if (!next.contains(",")) {
                                if (!next.equals("normal") && !next.equals("fault")) {
                                    float token = Float.valueOf(next) * scale;
                                    int column = ((tokenCount + 1) / 2) - 1;
                                    tokenLine[column] = token;
                                }
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
