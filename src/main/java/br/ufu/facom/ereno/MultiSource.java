package br.ufu.facom.ereno;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.benign.uc00.Input;
import br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
import br.ufu.facom.ereno.attacks.uc04.devices.FakeNormalMasqueratorIED;
import br.ufu.facom.ereno.attacks.uc05.devices.InjectorIED;
import br.ufu.facom.ereno.attacks.uc06.devices.HighStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc07.devices.HighRateStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc08.devices.GrayHoleVictimIED;
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.general.ProtectionIED;
import br.ufu.facom.ereno.evaluation.DatasetEval;
import br.ufu.facom.ereno.utils.GSVDatasetWritter;

import java.io.IOException;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.api.GooseFlow.ECF.numberOfMessages;
import static br.ufu.facom.ereno.utils.GSVDatasetWritter.*;

public class MultiSource {

    public static void main(String[] args) throws Exception {
        init();
        numberOfMessages = 100;
        twoDevices("train", numberOfMessages);
        numberOfMessages = 100;
        twoDevices("test", numberOfMessages);
        DatasetEval.runWithoutCV();

    }


    public static void twoDevices(String datasetName, int n) throws IOException {
        numberOfMessages = n;
        startWriting("E:\\ereno dataset\\hibrid_dataset_GOOSE_" + datasetName + ".arff");

        // Generating SV messages
        MergingUnit mu = runMU();
        LegitimateProtectionIED protectionIED = runUC00(mu);
        // Generating GOOSE attacks
        System.out.println("-----------------");
        runUC01(protectionIED, mu);
//        runUC02(protectionIED, mu);
//        runUC03(protectionIED, mu); // parei aqui
//        runUC04(protectionIED, mu);
//        runUC05(protectionIED, mu);
//        runUC06(protectionIED, mu);
//        runUC07(protectionIED, mu);
//        runUC08(protectionIED, mu);

        finishWriting();

    }

    public static MergingUnit runMU() {
//        MergingUnit mu = new MergingUnit(Input.electricalSourceFiles);
        MergingUnit mu = new MergingUnit(Input.singleElectricalSourceFile);
        mu.enableRandomOffsets(numberOfMessages);
        mu.run(numberOfMessages * 4763);
        return mu;
    }

    public static LegitimateProtectionIED runUC00(MergingUnit mu) throws IOException {
        LegitimateProtectionIED uc00 = new LegitimateProtectionIED();
        uc00.setInitialTimestamp(mu.getInitialTimestamp());
        Logger.getLogger("RunUC00").info("mu.getOffset(): " + mu.getInitialTimestamp());
        uc00.run(numberOfMessages);
        int qtdNormal00 = writeNormal(uc00.copyMessages(), mu.getMessages(), true);
        Logger.getLogger("MultiSource").info("Writting " + qtdNormal00 + " legitimate (UC00) messages to dataset.");
        return uc00;
    }

    public static void runUC01(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        RandomReplayerIED uc01 = new RandomReplayerIED(uc00);
        uc01.run(numberOfMessages);
        int qtdReplay01 = writeAttack(uc00, uc01, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdReplay01 + " replayed (UC01) messages to dataset.");
    }

    public static void runUC02(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        InverseReplayerIED uc02 = new InverseReplayerIED(uc00);
        uc02.run(numberOfMessages);
        int qtdReplay02 = writeAttack(uc00, uc02, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdReplay02 + "  replayed (UC02) messages to dataset.");
    }

    public static void runUC03(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        FakeFaultMasqueratorIED uc03 = new FakeFaultMasqueratorIED(uc00);
        uc03.run(numberOfMessages);
//        int qtdMarquerade03 = writeGOOSEAtkWithSVToFile(uc00, uc03.getMasqueradeMessages(), mu.getMessages(), false);
        int qtdMarquerade03 = writeNormal(uc03.getMessages(), mu.getMessages(), false);
        Logger.getLogger("MultiSource").info("Writting " + qtdMarquerade03 + "  masquerade (UC03)  messages to dataset.");
    }

    public static void runUC04(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        FakeNormalMasqueratorIED uc04 = new FakeNormalMasqueratorIED(uc00);
        uc04.run(numberOfMessages - 1);
        int qtdMarquerade04 = writeAttack(uc00, uc04, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdMarquerade04 + " masquerade (UC04) messages to dataset.");
    }

    public static void runUC05(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        InjectorIED uc05 = new InjectorIED(uc00);
        uc05.run(numberOfMessages);
        int qtdInjection05 = writeAttack(uc00, uc05, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdInjection05 + " injected (UC05) messages to dataset.");
    }

    public static void runUC06(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        HighStNumInjectorIED uc06 = new HighStNumInjectorIED(uc00);
        uc06.run(numberOfMessages);
        int qtdInjection06 = writeAttack(uc00, uc06, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdInjection06 + " injected (UC06) messages to dataset.");
    }

    public static void runUC07(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        HighRateStNumInjectorIED uc07 = new HighRateStNumInjectorIED(uc00);
        uc07.run(numberOfMessages);
        int qtdInjection07 = writeAttack(uc00, uc07, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdInjection07 + " injected (UC07) messages to dataset.");
    }

    public static void runUC08(LegitimateProtectionIED uc00, MergingUnit mu) throws IOException {
        ProtectionIED uc00forGrayhole = new LegitimateProtectionIED();
        uc00forGrayhole.setInitialTimestamp(mu.getInitialTimestamp());
        uc00forGrayhole.run((int) (numberOfMessages * 1.2)); // generate more because it discards
        GrayHoleVictimIED uc08 = new GrayHoleVictimIED(uc00forGrayhole);
        uc08.run(80);
        int qtdGrayhole08 = writeAttack(uc00, uc08, mu, false);
        Logger.getLogger("MultiSource").info("Writting " + qtdGrayhole08 + " gryhole (UC08) messages to dataset.");
    }


    public static void init() {
        Attacks.ECF.loadConfigs();
        GooseFlow.ECF.loadConfigs();
        SetupIED.ECF.loadConfigs();
        Attacks.ECF.legitimate = true;
        Attacks.ECF.randomReplay = true;
        Attacks.ECF.masqueradeOutage = true;
        Attacks.ECF.masqueradeDamage = true;
        Attacks.ECF.randomInjection = true;
        Attacks.ECF.inverseReplay = true;
        Attacks.ECF.highStNum = true;
        Attacks.ECF.flooding = true;
        Attacks.ECF.grayhole = false;
    }


}
