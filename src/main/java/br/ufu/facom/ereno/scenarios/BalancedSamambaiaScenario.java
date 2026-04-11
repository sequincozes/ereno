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
import br.ufu.facom.ereno.messages.Goose;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Balanced dataset generation scenario that generates messages until a target number of attack messages is reached.
 *
 * This scenario extends SamambaiaScenario with balanced dataset generation capabilities:
 * - Iteratively generates batches of messages until target malicious message count is met
 * - Configurable target for attack messages via params.properties
 * - Prevents dataset imbalance caused by different attack discard rates
 * - Supports all attack types from the original scenario
 *
 * Configuration:
 * - scenario.targetMaliciousMessages: Target number of attack messages (default: 1000)
 * - scenario.batchSize: Number of messages to generate per iteration (default: 100)
 * - scenario.maxIterations: Maximum iterations to prevent infinite loops (default: 100)
 *
 * @see SamambaiaScenario
 */
public class BalancedSamambaiaScenario implements IScenario {

    public static void main(String[] args) throws Exception {
        BalancedSamambaiaScenario scenario = new BalancedSamambaiaScenario();
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

    // Balanced generation parameters
    private static int targetMaliciousMessages = 1000;
    private static int batchSize = 100;
    private static int maxIterations = 100;
    private static int svWindowSize = 10000;  // Keep only recent SV messages for correlation

    // State for incremental writing
    private Goose previousGoose = null;
    private boolean headerWritten = false;

    @Override
    public void run() {
        substationNetwork = new SubstationNetwork();
        loadAllConfigs();
        setupDevices();
        runDevicesUntilTargetWithIncrementalWrite();
        // exportDataset() not needed - already written incrementally
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

            // Balanced generation parameters
            targetMaliciousMessages = Integer.parseInt(props.getProperty("scenario.targetMaliciousMessages", "1000"));
            batchSize = Integer.parseInt(props.getProperty("scenario.batchSize", "100"));
            maxIterations = Integer.parseInt(props.getProperty("scenario.maxIterations", "100"));

            Logger.getLogger("BalancedSamambaiaScenario").info(
                    String.format("Balanced generation config: target=%d, batchSize=%d, maxIter=%d",
                            targetMaliciousMessages, batchSize, maxIterations));

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
                Logger.getLogger("BalancedSamambaiaScenario").info("Enabled: LegitimateProtectionIED (for dataset)");
            } else {
                Logger.getLogger("BalancedSamambaiaScenario").info("Enabled: LegitimateProtectionIED (baseline only, excluded from dataset)");
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
                Logger.getLogger("BalancedSamambaiaScenario").info("Enabled: " + attackIED.getClass().getSimpleName());
            }
        });

        Logger.getLogger("BalancedSamambaiaScenario").info("Devices setup complete! Total bay devices: " + substationNetwork.bayLevelDevices.size());
    }

    /**
     * Runs devices iteratively until the target number of malicious messages is reached.
     * Writes batches incrementally to disk to prevent OOM errors.
     */
    public void runDevicesUntilTargetWithIncrementalWrite() {
        int currentMaliciousCount = 0;
        int iteration = 0;

        Logger.getLogger("BalancedSamambaiaScenario").info(
                String.format("Starting balanced generation: target=%d malicious messages", targetMaliciousMessages));

        try {
            // Open CSV writer once at start
            String filename = path + datasetName + (generateArff ? ".arff" : ".csv");
            if (!debug) {
                if (generateArff) {
                    ARFFWritter.startWriting(filename);
                } else {
                    CSVWritter.startWriting(filename);
                }
            } else {
                DebugWritter.startWriting("debug.csv");
            }

            while (currentMaliciousCount < targetMaliciousMessages && iteration < maxIterations) {
                iteration++;

                Logger.getLogger("BalancedSamambaiaScenario").info(
                        String.format("Iteration %d: Current malicious count=%d, generating batch of %d messages",
                                iteration, currentMaliciousCount, batchSize));

                // Run one batch
                int newMaliciousCount = runDevicesBatch(batchSize);

                // Write batch to disk immediately
                writeBatchToDataset();

                // Clear memory after writing
                clearBatchMemory();

                currentMaliciousCount += newMaliciousCount;

                Logger.getLogger("BalancedSamambaiaScenario").info(
                        String.format("Iteration %d complete: Added %d malicious messages, total=%d, memory cleared",
                                iteration, newMaliciousCount, currentMaliciousCount));
            }

            // Close CSV writer at end
            if (!debug) {
                if (generateArff) {
                    ARFFWritter.finishWriting();
                } else {
                    CSVWritter.finishWriting();
                }
            } else {
                DebugWritter.finishWriting();
            }

            if (iteration >= maxIterations) {
                Logger.getLogger("BalancedSamambaiaScenario").warning(
                        String.format("Reached max iterations (%d) with only %d malicious messages (target: %d)",
                                maxIterations, currentMaliciousCount, targetMaliciousMessages));
            } else {
                Logger.getLogger("BalancedSamambaiaScenario").info(
                        String.format("Target reached! Generated %d malicious messages in %d iterations",
                                currentMaliciousCount, iteration));
            }

            Logger.getLogger("BalancedSamambaiaScenario").info("Dataset exported with incremental writing!");

        } catch (IOException e) {
            throw new RuntimeException("Failed to write dataset incrementally", e);
        }
    }

    /**
     * Runs devices for one batch and returns the number of malicious messages generated.
     */
    private int runDevicesBatch(int batchSize) {
        int maliciousMessagesInBatch = 0;

        // Update GooseFlow.numberOfMessages to match batch size to prevent warnings
        int originalNumberOfMessages = GooseFlow.numberOfMessages;
        GooseFlow.numberOfMessages = batchSize;

        // Run merging units for this batch
        for (MergingUnit mu : substationNetwork.processLevelDevices) {
            mu.run(batchSize);
            substationNetwork.processBusMessages.addAll(new ArrayList<>(mu.getMessages()));
        }

        // Run protection IEDs for this batch
        for (IED ied : substationNetwork.bayLevelDevices) {
            if (ied instanceof ProtectionIED) {
                ProtectionIED protectionIED = (ProtectionIED) ied;

                // Clear previous batch messages before running
                protectionIED.getMessages().clear();
                protectionIED.run(batchSize);

                // Determine if this IED's messages should be included in the dataset
                boolean includeInDataset = true;
                if (ied instanceof LegitimateProtectionIED && !Attacks.legitimate) {
                    includeInDataset = false;
                }

                if (includeInDataset) {
                    // Add messages to station bus
                    substationNetwork.stationBusMessages.addAll(protectionIED.getMessages());

                    // Count malicious messages (non-normal label)
                    if (!(ied instanceof LegitimateProtectionIED)) {
                        int maliciousInThisIED = countMaliciousMessages(protectionIED.getMessages());
                        maliciousMessagesInBatch += maliciousInThisIED;

                        Logger.getLogger("BalancedSamambaiaScenario").info(
                                String.format("  %s: %d messages (%d malicious)",
                                        ied.getClass().getSimpleName(),
                                        protectionIED.getMessages().size(),
                                        maliciousInThisIED));
                    }
                }
            }
        }

        // Restore original numberOfMessages value
        GooseFlow.numberOfMessages = originalNumberOfMessages;

        return maliciousMessagesInBatch;
    }

    /**
     * Counts the number of malicious (non-normal) messages in a list.
     */
    private int countMaliciousMessages(ArrayList<Goose> messages) {
        int count = 0;
        for (Goose message : messages) {
            String label = message.getLabel();
            if (label != null && !label.equalsIgnoreCase("normal")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Writes current batch of messages to the dataset file.
     * Processes GOOSE messages with SV correlation and writes incrementally.
     */
    private void writeBatchToDataset() throws IOException {
        // Write header on first batch only
        if (!headerWritten) {
            if (!debug) {
                if (generateArff) {
                    // ARFF header writing handled by ARFFWritter
                } else {
                    CSVWritter.writeDefaultHeader();
                }
            } else {
                DebugWritter.writeDefaultHeader();
            }
            headerWritten = true;
        }

        // Process messages from the priority queue
        int processedCount = 0;
        while (!substationNetwork.stationBusMessages.isEmpty()) {
            Goose goose = (Goose) substationNetwork.stationBusMessages.poll();

            if (previousGoose != null) {
                // Process this GOOSE message with SV correlation
                if (!debug) {
                    if (generateArff) {
                        ARFFWritter.processAndWriteSingleMessage(goose, previousGoose,
                                substationNetwork.processBusMessages);
                    } else {
                        CSVWritter.processAndWriteSingleMessage(goose, previousGoose,
                                substationNetwork.processBusMessages);
                    }
                } else {
                    DebugWritter.processAndWriteSingleMessage(goose, previousGoose,
                            substationNetwork.processBusMessages);
                }
                processedCount++;
            }

            previousGoose = goose.copy();
        }

        // Flush buffer to ensure data is written to disk
        if (!debug) {
            if (generateArff) {
                ARFFWritter.flushBuffer();
            } else {
                CSVWritter.flushBuffer();
            }
        } else {
            DebugWritter.flushBuffer();
        }

        Logger.getLogger("BalancedSamambaiaScenario").info(
                String.format("Wrote %d messages to dataset", processedCount));
    }

    /**
     * Clears memory after batch is written to disk.
     * Keeps only recent SV messages window for correlation.
     */
    private void clearBatchMemory() {
        // Station bus messages already cleared by poll() in writeBatchToDataset

        // Keep only recent SV messages for correlation (sliding window)
        int svSize = substationNetwork.processBusMessages.size();
        if (svSize > svWindowSize) {
            // Remove old SV messages, keep only recent window
            int toRemove = svSize - svWindowSize;
            substationNetwork.processBusMessages.subList(0, toRemove).clear();

            Logger.getLogger("BalancedSamambaiaScenario").info(
                    String.format("Trimmed SV messages: removed %d old messages, kept %d recent",
                            toRemove, substationNetwork.processBusMessages.size()));
        }

        // Explicitly suggest garbage collection
        System.gc();
    }

    @Override
    public void runDevices() {
        // This method is not used in balanced scenario - runDevicesUntilTarget is used instead
        throw new UnsupportedOperationException("Use runDevicesUntilTarget() instead");
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
                    CSVWritter.startWriting(path + datasetName + ".csv");
                    CSVWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                    CSVWritter.finishWriting();
                }
            } else {
                DebugWritter.startWriting("debug.csv");
                DebugWritter.processDataset(substationNetwork.stationBusMessages, substationNetwork.processBusMessages);
                DebugWritter.finishWriting();
            }

            Logger.getLogger("BalancedSamambaiaScenario").info("Dataset exported!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
