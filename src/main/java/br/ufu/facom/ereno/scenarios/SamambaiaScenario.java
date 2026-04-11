package br.ufu.facom.ereno.scenarios;

import br.ufu.facom.ereno.SubstationNetwork;
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
import br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED;
import br.ufu.facom.ereno.benign.uc00.devices.MergingUnit;
import br.ufu.facom.ereno.dataExtractors.ARFFWritter;
import br.ufu.facom.ereno.dataExtractors.CSVWritter;
import br.ufu.facom.ereno.dataExtractors.DebugWritter;
import br.ufu.facom.ereno.general.IED;
import br.ufu.facom.ereno.general.ProtectionIED;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Represents the Samambaia scenario for IEC 61850 communication simulation.
 *
 * This class sets up and runs a simulated substation network environment including merging units,
 * legitimate protection devices, and various attack devices, generating Sampled Values (SV) and GOOSE messages.
 *
 * Configuration parameters are loaded from 'params.properties', allowing customization of:
 * - Dataset generation path and name
 * - Number of messages to generate
 * - Whether to generate ARFF or CSV datasets
 * - Debug mode enabling detailed logging output
 *
 * The scenario supports a mix of legitimate and malicious devices, simulating attack behaviors such as:
 * - Random replay
 * - Inverse replay
 * - Masquerade fake fault and normal states
 * - Injection attacks
 * - Gray hole attacks
 *
 * The scenario executes device setup, message generation, and dataset export,
 * leveraging helper classes for writing output in ARFF, CSV, or debug formats.
 *
 * Usage example:
 * <pre>{@code
* substationNetwork.processLevelDevices.add(mu);
* substationNetwork.bayLevelDevices.add(uc00);
* substationNetwork.bayLevelDevices.add(attackIED);
 * }</pre>
 *
 * @see br.ufu.facom.ereno.SubstationNetwork
 * @see br.ufu.facom.ereno.general.ProtectionIED
 * @see br.ufu.facom.ereno.benign.uc00.devices.LegitimateProtectionIED
 * @see br.ufu.facom.ereno.attacks.uc01.devices.RandomReplayerIED
 * @see br.ufu.facom.ereno.dataExtractors.ARFFWritter
 * @see br.ufu.facom.ereno.dataExtractors.CSVWritter
 * @see br.ufu.facom.ereno.dataExtractors.DebugWritter
 */


public class SamambaiaScenario implements IScenario {

    public static void main(String[] args) throws Exception {
        SamambaiaScenario scenario = new SamambaiaScenario();
        scenario.run();
    }

    private static final String CONFIG_FILE = "params.properties";
    private static final Properties props = new Properties();
    SubstationNetwork substationNetwork;
    private static Boolean generateArff = false;
    private static Boolean debug = false;
    private static String path;
    private static String datasetName;
    private static int numberOfMessages;

    @Override
    public void run() {
        substationNetwork = new SubstationNetwork();
        loadAllConfigs();
        setupDevices();
        runDevices();
        exportDataset();
    }

    public void loadAllConfigs() {
        // Load all configuration files
        Attacks.loadConfigs();
        GooseFlow.loadConfigs();
        SetupIED.loadConfigs();

        // Load scenario-specific configuration
        try (InputStream input = GooseFlow.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException(CONFIG_FILE + " not found in classpath");
            }
            props.load(input);

            generateArff = Boolean.parseBoolean(props.getProperty("scenario.generateArff", "false"));
            debug = Boolean.parseBoolean(props.getProperty("scenario.debug", "false"));
            path = props.getProperty("scenario.path");
            datasetName = props.getProperty("scenario.datasetName");
            numberOfMessages = Integer.parseInt(props.getProperty("goose.flow.numberOfMessages", "0"));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from " + CONFIG_FILE, e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format in configuration", e);
        }
    }

    @Override
    public void setupDevices() {
        // Setup merging unit
        MergingUnit mu = new MergingUnit(InputFilesForSV.getElectricalSourceFiles());
        substationNetwork.processLevelDevices.add(mu);

        // Setup legitimate IED (always created as baseline for attacks that depend on it)
        LegitimateProtectionIED uc00 = new LegitimateProtectionIED();
        uc00.setSubstationNetwork(substationNetwork);

        // Check if any attack that depends on legitimate IED is enabled
        boolean needsLegitimateBaseline = Attacks.randomReplay || Attacks.inverseReplay ||
                                          Attacks.masqueradeOutage || Attacks.randomInjection ||
                                          Attacks.highStNum || Attacks.flooding ||
                                          Attacks.grayhole || Attacks.orientedGrayhole;

        // Add legitimate IED to bay devices if explicitly enabled OR needed as baseline
        if (Attacks.legitimate || needsLegitimateBaseline) {
            substationNetwork.bayLevelDevices.add(uc00);
            if (Attacks.legitimate) {
                Logger.getLogger("SamambaiaScenario").info("Enabled: LegitimateProtectionIED (for dataset)");
            } else {
                Logger.getLogger("SamambaiaScenario").info("Enabled: LegitimateProtectionIED (baseline only, excluded from dataset)");
            }
        }

        // Attack IED registry - maps flag to IED factory
        Map<Boolean, java.util.function.Supplier<ProtectionIED>> attackRegistry = new LinkedHashMap<>();
        attackRegistry.put(Attacks.randomReplay, () -> new RandomReplayerIED(uc00));
        attackRegistry.put(Attacks.inverseReplay, () -> new InverseReplayerIED(uc00));
        attackRegistry.put(Attacks.masqueradeOutage, () -> new MasqueradeFakeFaultIED(uc00));
        attackRegistry.put(Attacks.masqueradeDamage, () -> new MasqueradeFakeNormalED());
        attackRegistry.put(Attacks.randomInjection, () -> new InjectorIED(uc00));
        attackRegistry.put(Attacks.highStNum, () -> new HighStNumInjectorIED(uc00));
        attackRegistry.put(Attacks.flooding, () -> new HighRateStNumInjectorIED(uc00));
        attackRegistry.put(Attacks.grayhole, () -> new GrayHoleVictimIED(uc00));
        attackRegistry.put(Attacks.orientedGrayhole, () -> new OrientedGrayHoleIED(uc00));

        // Instantiate and register enabled attacks
        attackRegistry.forEach((enabled, factory) -> {
            if (enabled) {
                ProtectionIED attackIED = factory.get();
                attackIED.setSubstationNetwork(substationNetwork);
                substationNetwork.bayLevelDevices.add(attackIED);
                Logger.getLogger("SamambaiaScenario").info("Enabled: " + attackIED.getClass().getSimpleName());
            }
        });

        Logger.getLogger("SamambaiaScenario").info("Devices setup complete! Total bay devices: " + substationNetwork.bayLevelDevices.size());
    }

    @Override
    public void runDevices() {

        substationNetwork.processBusMessages.clear();
        substationNetwork.stationBusMessages.clear();

        for (MergingUnit mu : substationNetwork.processLevelDevices) {
            mu.getMessages().clear();
            mu.run(numberOfMessages);
            substationNetwork.processBusMessages.addAll(
                    new ArrayList<>(mu.getMessages())
            );
        }

        for (IED ied : substationNetwork.bayLevelDevices) {
            if (ied instanceof ProtectionIED) {
                ProtectionIED protectionIED = (ProtectionIED) ied;
                protectionIED.getMessages().clear();
                protectionIED.run(numberOfMessages);

                // Determine if this IED's messages should be included in the dataset
                boolean includeInDataset = true;
                if (ied instanceof LegitimateProtectionIED && !Attacks.legitimate) {
                    // Exclude legitimate messages if the flag is disabled
                    // (they're only generated as baseline for attacks)
                    includeInDataset = false;
                }

                if (includeInDataset) {
                    substationNetwork.stationBusMessages.addAll(protectionIED.getMessages());
                    Logger.getLogger("SamambaiaScenario").info(
                            String.format("%s generated %d messages (included in dataset)",
                                    ied.getClass().getSimpleName(),
                                    protectionIED.getMessages().size())
                    );
                } else {
                    Logger.getLogger("SamambaiaScenario").info(
                            String.format("%s generated %d messages (baseline only, excluded from dataset)",
                                    ied.getClass().getSimpleName(),
                                    protectionIED.getMessages().size())
                    );
                }
            }
        }

        Logger.getLogger("SamambaiaScenario").info(
                String.format("Generated %d SV and %d GOOSE messages",
                        substationNetwork.processBusMessages.size(),
                        substationNetwork.stationBusMessages.size())
        );
    }

    @Override
    public void exportDataset() {
        try {
            if (!debug) {
                if (generateArff) {
                    ARFFWritter.startWriting(path + datasetName + ".arff");
                    ARFFWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                    ARFFWritter.finishWriting();
                } else {
                    CSVWritter.startWriting(path+datasetName+".csv");
                    CSVWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                    CSVWritter.finishWriting();
                }
            } else {
                DebugWritter.startWriting("debug.csv");
                DebugWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                DebugWritter.finishWriting();
            }

            Logger.getLogger("SamambaiaScenario").info("Dataset exported!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}