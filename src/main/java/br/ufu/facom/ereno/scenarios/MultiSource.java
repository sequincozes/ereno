package br.ufu.facom.ereno.scenarios;

import br.ufu.facom.ereno.api.Attacks;
import br.ufu.facom.ereno.api.GooseFlow;
import br.ufu.facom.ereno.api.SetupIED;
import br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED;
import br.ufu.facom.ereno.attacks.uc02.devices.InverseReplayerIED;
import br.ufu.facom.ereno.attacks.uc03.devices.FakeFaultMasqueratorIED;
import br.ufu.facom.ereno.attacks.uc04.devices.FakeNormalMasqueratorIED;
import br.ufu.facom.ereno.attacks.uc05.devices.InjectorIED;
import br.ufu.facom.ereno.attacks.uc06.devices.HighStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc07.devices.HighRateStNumInjectorIED;
import br.ufu.facom.ereno.attacks.uc08.devices.GrayHoleVictimIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.benign.uc00.devices.ProtectionIED;

import java.io.IOException;
import java.util.logging.Logger;

import static br.ufu.facom.ereno.utils.Util.*;

public class MultiSource {
    static String output = "E:\\ereno dataset\\" + SetupIED.ECF.iedName + ".arff";

    public static void main(String[] args) throws IOException {

        init();

        ProtectionIED uc00 = new ProtectionIED();
        ProtectionIED uc00forGrayhole = new ProtectionIED();
        RandomReplayerIED uc01 = new RandomReplayerIED(uc00);
        InverseReplayerIED uc02 = new InverseReplayerIED(uc00);
        FakeFaultMasqueratorIED uc03 = new FakeFaultMasqueratorIED(uc00);
        FakeNormalMasqueratorIED uc04 = new FakeNormalMasqueratorIED(uc00);
        InjectorIED uc05 = new InjectorIED(uc00);
        HighStNumInjectorIED uc06 = new HighStNumInjectorIED(uc00);
        HighRateStNumInjectorIED uc07 = new HighRateStNumInjectorIED(uc00);
        GrayHoleVictimIED uc08 = new GrayHoleVictimIED(uc00forGrayhole);

        MergingUnit mu = new MergingUnit(
                new String[]{
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00001_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00002_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00003_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00004_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00005_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00006_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00007_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00008_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00009_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00010_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00011_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00012_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00013_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00014_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00015_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00016_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00017_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00018_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00019_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00020_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00021_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00022_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00023_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00024_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00025_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00026_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00027_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00028_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00029_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00030_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00031_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00032_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00033_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00034_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00035_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00036_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00037_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00038_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00039_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00040_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00041_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00042_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00043_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00044_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00045_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00046_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00047_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00048_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00049_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00050_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00051_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00052_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00053_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00054_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00055_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00056_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00057_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00058_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00059_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00060_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00061_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00062_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00063_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00064_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00065_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00066_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00067_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00068_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00069_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00070_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00071_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00072_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00073_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00074_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00075_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00076_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00077_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00078_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00079_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00080_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00081_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00082_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00083_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00084_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00085_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00086_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00087_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00088_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00089_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00090_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00091_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00092_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00093_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00094_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00095_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00096_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00097_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00098_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00099_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00100_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00101_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00102_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00103_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00104_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00105_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00106_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00107_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00108_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00109_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00110_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00111_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00112_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00113_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00114_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00115_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00116_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00117_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00118_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00119_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00120_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00121_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00122_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00123_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00124_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00125_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00126_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00127_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00128_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00129_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00130_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00131_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia100\\SILVIO_r00132_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00001_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00002_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00003_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00004_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00005_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00006_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00007_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00008_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00009_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00010_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00011_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00012_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00013_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00014_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00015_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00016_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00017_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00018_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00019_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00020_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00021_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00022_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00023_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00024_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00025_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00026_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00027_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00028_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00029_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00030_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00031_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00032_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00033_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00034_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00035_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00036_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00037_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00038_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00039_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00040_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00041_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00042_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00043_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00044_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00045_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00046_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00047_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00048_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00049_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00050_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00051_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00052_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00053_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00054_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00055_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00056_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00057_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00058_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00059_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00060_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00061_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00062_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00063_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00064_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00065_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00066_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00067_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00068_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00069_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00070_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00071_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00072_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00073_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00074_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00075_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00076_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00077_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00078_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00079_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00080_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00081_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00082_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00083_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00084_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00085_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00086_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00087_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00088_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00089_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00090_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00091_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00092_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00093_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00094_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00095_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00096_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00097_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00098_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00099_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00100_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00101_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00102_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00103_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00104_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00105_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00106_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00107_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00108_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00109_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00110_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00111_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00112_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00113_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00114_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00115_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00116_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00117_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00118_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00119_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00120_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00121_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00122_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00123_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00124_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00125_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00126_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00127_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00128_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00129_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00130_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00131_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia50\\SILVIO_r00132_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00001_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00002_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00003_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00004_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00005_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00006_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00007_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00008_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00009_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00010_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00011_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00012_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00013_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00014_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00015_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00016_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00017_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00018_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00019_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00020_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00021_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00022_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00023_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00024_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00025_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00026_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00027_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00028_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00029_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00030_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00031_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00032_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00033_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00034_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00035_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00036_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00037_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00038_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00039_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00040_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00041_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00042_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00043_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00044_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00045_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00046_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00047_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00048_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00049_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00050_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00051_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00052_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00053_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00054_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00055_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00056_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00057_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00058_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00059_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00060_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00061_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00062_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00063_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00064_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00065_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00066_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00067_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00068_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00069_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00070_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00071_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00072_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00073_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00074_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00075_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00076_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00077_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00078_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00079_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00080_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00081_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00082_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00083_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00084_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00085_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00086_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00087_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00088_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00089_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00090_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00091_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00092_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00093_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00094_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00095_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00096_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00097_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00098_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00099_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00100_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00101_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00102_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00103_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00104_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00105_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00106_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00107_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00108_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00109_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00110_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00111_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00112_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00113_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00114_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00115_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00116_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00117_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00118_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00119_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00120_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00121_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00122_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00123_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00124_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00125_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00126_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00127_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00128_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00129_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00130_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00131_01.out",
                        "E:\\ereno dataset\\electrical-sources\\resistencia10\\SILVIO_r00132_01.out",


                }
        );


        startWriting("E:\\ereno dataset\\hibrid_dataset_GOOSE.arff");
        mu.run(10000 * 4800);

        int numSamplesPerClass = 10000;

        uc00.run(numSamplesPerClass);
        ProtectionIED.numMaxMessages = numSamplesPerClass + 5000;
        uc00forGrayhole.run(numSamplesPerClass + 5000); // generate more because it discards
        writeSVAndGOOSEMessagesToFile(uc00.getMessages(), mu.getMessages(), true, FOCUS.GOOSE);

        uc01.run(numSamplesPerClass);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc01.getReplayedMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc02.run(numSamplesPerClass);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc02.getReplayedMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc03.run(numSamplesPerClass - 1);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc03.getMasqueradeMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc04.run(numSamplesPerClass - 1);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc04.getMasqueradeMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc05.run(numSamplesPerClass + 1);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc05.getInjectedMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc06.run(numSamplesPerClass + 1);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc06.getInjectedMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc07.run(numSamplesPerClass + 1);
        writeSVAndAttackGOOSEMessagesToFile(uc00.getMessages(), uc07.getInjectedMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        uc08.run(80);
        writeSVAndAttackGOOSEMessagesToFile(uc00forGrayhole.getMessages(), uc08.getNonDiscardedMessages(), mu.getMessages(), false, FOCUS.GOOSE);

        Logger.getLogger("MultiSource").info("Writting " + uc00.getNumberOfMessages() + " legitimate (UC00) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc01.getNumberOfMessages() + " replayed (UC01) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc02.getReplayedMessages() + "  replayed (UC02) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc03.getNumberOfMessages() + "  masquerade (UC03)  messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc04.getNumberOfMessages() + " masquerade (UC04) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc05.getNumberOfMessages() + " injected (UC05) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc06.getNumberOfMessages() + " injected (UC06) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc07.getNumberOfMessages() + " injected (UC07) messages to dataset.");
        Logger.getLogger("MultiSource").info("Writting " + uc08.getNumberOfMessages() + " gryhole (UC08) messages to dataset.");

        finishWriting();
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

    public static void lightweightDataset() throws IOException { // Generates only GOOSE data
        long beginTime = System.currentTimeMillis();

        // Start writting
        Logger.getLogger("Extractor").info(output + " writting...");
        startWriting(output);
        int totalMessageCount = 0;
        write("@relation ereno_lightweight");

        // Generate and write samples for legitimate and attacks choosen in attacks.json
        ProtectionIED uc00 = null;
        if (Attacks.ECF.legitimate) {
            uc00 = new ProtectionIED();
            uc00.run(1000);
            writeGooseMessagesToFile(uc00.getMessages(), true);
            totalMessageCount = totalMessageCount + uc00.getNumberOfMessages();
        }

        RandomReplayerIED uc01;
        if (Attacks.ECF.randomReplay) {
            uc01 = new RandomReplayerIED(uc00);
            uc01.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc01.getReplayedMessages(), false);
            totalMessageCount = totalMessageCount + uc01.getNumberOfMessages();
        }

        InverseReplayerIED uc02;
        if (Attacks.ECF.inverseReplay) {
            uc02 = new InverseReplayerIED(uc00);
            uc02.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc02.getReplayedMessages(), false);
            totalMessageCount = totalMessageCount + uc02.getNumberOfMessages();
        }

        FakeFaultMasqueratorIED uc03;
        if (Attacks.ECF.masqueradeOutage) {
            uc03 = new FakeFaultMasqueratorIED(uc00);
            uc03.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc03.getMasqueradeMessages(), false);
            totalMessageCount = totalMessageCount + uc03.getNumberOfMessages();
        }

        FakeNormalMasqueratorIED uc04;
        if (Attacks.ECF.masqueradeDamage) {
            uc04 = new FakeNormalMasqueratorIED(uc00);
            uc04.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc04.getMasqueradeMessages(), false);
            totalMessageCount = totalMessageCount + uc04.getNumberOfMessages();
        }

        InjectorIED uc05;
        if (Attacks.ECF.randomInjection) {
            uc05 = new InjectorIED(uc00);
            uc05.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc05.getInjectedMessages(), false);
            totalMessageCount = totalMessageCount + uc05.getNumberOfMessages();
        }

        HighStNumInjectorIED uc06;
        if (Attacks.ECF.highStNum) {
            uc06 = new HighStNumInjectorIED(uc00);
            uc06.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc06.getInjectedMessages(), false);
            totalMessageCount = totalMessageCount + uc06.getNumberOfMessages();
        }

        HighRateStNumInjectorIED uc07;
        if (Attacks.ECF.flooding) {
            uc07 = new HighRateStNumInjectorIED(uc00);
            uc07.run(GooseFlow.ECF.numberOfMessages);
            writeGooseMessagesToFile(uc07.getInjectedMessages(), false);
            totalMessageCount = totalMessageCount + uc07.getNumberOfMessages();
        }

        GrayHoleVictimIED uc08;
        if (Attacks.ECF.grayhole) {
            uc08 = new GrayHoleVictimIED(uc00);
//            uc08.run(GooseFlow.ECF.numberOfMessages);
            uc08.run(20);
            writeGooseMessagesToFile(uc08.getNonDiscardedMessages(), false);
            totalMessageCount = totalMessageCount + uc08.getNumberOfMessages();
        }

        finishWriting();
        long endTime = System.currentTimeMillis();
        Logger.getLogger("Time").info("Tempo gasto para gerar "
                + Integer.valueOf(totalMessageCount) + " mensagens: "
                + (endTime - beginTime));
    }

}
