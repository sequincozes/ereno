/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufu.facom.ereno.useCases;

import br.ufu.facom.ereno.devices.ProtectionIED;
import br.ufu.facom.ereno.model.GooseMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author silvio
 */
public class UC00GooseOnly extends AbstractUseCase {

//    public static void main(String[] args) {
//        try {
//            UC00GooseOnly.run("test2.arff");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public static void run(String filename) throws IOException {
//        outputFile = outputLocation + filename;
//        UC00GooseOnly extractor = new UC00GooseOnly();
//        AbstractUseCase.printHeader = true;
//        extractor.startWriting();
//        extractor.runNormalUC00();
//        extractor.finishWriting();
//    }

    public ArrayList<GooseMessage> generateNormalSamples(int numberOfPeriodicMessages) throws IOException {
        float offset = 0;//restartCounters();
        double firstEvent = offset + numberOfPeriodicMessages + 0.5;
        double secondEvent = offset + numberOfPeriodicMessages + 0.6;
        ied = new ProtectionIED(false, initialStNum, initialSqNum, 0.00631, 0.01659, 6.33000000000011f, 4, 1000, offset);
        ied.generatePeriodicGooseMessages(numberOfPeriodicMessages);
        ied.reportEventAt(firstEvent);
        ied.reportEventAt(secondEvent);
        ied.generatePeriodicGooseMessages(numberOfPeriodicMessages);
        ied.reportEventAt(firstEvent);
        ied.reportEventAt(secondEvent);
        ied.generatePeriodicGooseMessages(numberOfPeriodicMessages);
        return ied.getGooseMessages();

    }

}
