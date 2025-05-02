package br.ufu.facom.ereno.scenarios;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.MasqueradeFakeFaultIED;
import br.ufu.facom.ereno.attacks.uc04.devices.MasqueradeFakeNormalED;
import br.ufu.facom.ereno.attacks.uc05.devices.InjectorIED;
import br.ufu.facom.ereno.attacks.uc06.devices.HighStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc07.devices.HighRateStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc08.devices.GrayHoleVictimIED;
import br.ufu.facom.ereno.attacks.uc09.devices.OrientedGrayHoleIED;
import br.ufu.facom.ereno.benign.uc00.Input;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.SubstationNetwork;
import br.ufu.facom.ereno.dataExtractors.ARFFWritter;
import br.ufu.facom.ereno.dataExtractors.CSVWritter;
import br.ufu.facom.ereno.dataExtractors.DebugWritter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.messages.Goose;

import java.io.IOException;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.api.GooseFlow.ECF.numberOfMessages;

public class SamambaiaScenario implements IScenario {

    public static void main(String[] args) throws Exception {
        SamambaiaScenario scenario = new SamambaiaScenario();
        scenario.run();
//         DatasetEval.main(new String[]{});
    }

    SubstationNetwork substationNetwork;

    @Override
    public void run() {
        substationNetwork = new SubstationNetwork();
        loadAllConfigs();
        setupDevices();
        runDevices();
        exportDataset();
    }

    public void loadAllConfigs() {
        Attacks.ECF.loadConfigs();
        GooseFlow.ECF.loadConfigs();
        SetupIED.ECF.loadConfigs();
        Attacks.ECF.legitimate = true;
        Attacks.ECF.randomReplay = false;
        Attacks.ECF.masqueradeOutage = false;
        Attacks.ECF.masqueradeDamage = false;
        Attacks.ECF.randomInjection = false;
        Attacks.ECF.inverseReplay = false;
        Attacks.ECF.highStNum = false;
        Attacks.ECF.flooding = false;
        Attacks.ECF.grayhole = false;
        Attacks.ECF.orientedGrayhole = true;
    }

    @Override
    public void setupDevices() {
        MergingUnit mu = new MergingUnit(Input.electricalSourceFiles);
        substationNetwork.processLevelDevices.add(mu);

        System.out.println("-----------------");
        LegitimateProtectionIED uc00 = new LegitimateProtectionIED();

        RandomReplayerIED uc01 = new RandomReplayerIED(uc00);
        InverseReplayerIED uc02 = new InverseReplayerIED(uc00);
        MasqueradeFakeFaultIED uc03 = new MasqueradeFakeFaultIED(uc00);
        MasqueradeFakeNormalED uc04 = new MasqueradeFakeNormalED();
        InjectorIED uc05 = new InjectorIED(uc00);
        HighStNumInjectorIED uc06 = new HighStNumInjectorIED(uc00);
        HighRateStNumInjectorIED uc07 = new HighRateStNumInjectorIED(uc00);
        GrayHoleVictimIED uc08 = new GrayHoleVictimIED(uc00);
        OrientedGrayHoleIED uc09 = new OrientedGrayHoleIED(uc00);

        uc00.setSubstationNetwork(substationNetwork);
        uc01.setSubstationNetwork(substationNetwork);
        uc02.setSubstationNetwork(substationNetwork);
        uc03.setSubstationNetwork(substationNetwork);
        uc04.setSubstationNetwork(substationNetwork);
        uc05.setSubstationNetwork(substationNetwork);
        uc06.setSubstationNetwork(substationNetwork);
        uc07.setSubstationNetwork(substationNetwork);
        uc08.setSubstationNetwork(substationNetwork);
        uc09.setSubstationNetwork(substationNetwork);

        substationNetwork.processLevelDevices.add(mu);
        substationNetwork.bayLevelDevices.add(uc00);
        substationNetwork.bayLevelDevices.add(uc08);

        Logger.getLogger("SamambaiaScenario").info("Devices set up!");
    }

    @Override
    public void runDevices() {

            for (MergingUnit mu : substationNetwork.processLevelDevices) {
                mu.run(numberOfMessages * 4763);
                substationNetwork.processBusMessages.addAll(mu.getMessages());
            }


            for (IED ied : substationNetwork.bayLevelDevices) {
                Logger.getLogger("SambaiaScenario").info(substationNetwork.bayLevelDevices.size() + " devices connected to the substation network.");
                ied.run(numberOfMessages);

                int numAddedMessages = 0;


                for (Goose goose : ((ProtectionIED) ied).getMessages()) {
                    numAddedMessages++;
                    if (numAddedMessages < numberOfMessages && !(ied instanceof LegitimateProtectionIED)) {
                        substationNetwork.stationBusMessages.add(goose);
                    } else {
                        break;
                    }
                }
                Logger.getLogger("SamambaiaScenario").info("Generated " + numAddedMessages + " for IED " + ((ProtectionIED) ied).getLabel());
            }

            Logger.getLogger("SamambaiaScenario").info("Devices run successfully!");

    }

    @Override
    public void exportDataset() {
        boolean generate_arff = false;
        boolean debug = false;
        try {
            if (!debug) {
                // Export dataset as ARFF or CSV
                if (generate_arff) {
                    ARFFWritter.startWriting("C:\\Users\\zomca\\IdeaProjects\\ereno-uc09\\datasets\\oriented_grayhole\\8000_5_5.arff");
                    ARFFWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                    ARFFWritter.finishWriting();
                } else {
                    CSVWritter.startWriting("C:\\Users\\zomca\\IdeaProjects\\ereno-uc09\\datasets\\datasets_novos\\13_1.csv");
                    CSVWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                    CSVWritter.finishWriting();
                }
            } else {
                // Debug mode: write to debug CSV file
                DebugWritter.startWriting("debug.csv");
                DebugWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                DebugWritter.finishWriting();
            }

            Logger.getLogger("SamambaiaScenario").info("Dataset exported!");  // Log successful export

        } catch (IOException e) {
            throw new RuntimeException(e);  // Handle any IOExceptions during file writing
        }
    }
}
