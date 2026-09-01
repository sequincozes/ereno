package br.ufu.facom.ereno.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Provenance of one generation run, emitted into every dataset row.
 *
 * <p>The delivered gray-GOOSE dataset carried none of this: run identity, seed,
 * attack variant and attack parameters lived only in hardcoded Java fields and
 * in whatever the operator happened to edit before rebuilding. Four attack
 * variants meant four hand-edited builds, and nothing in the CSV said which row
 * came from which. Grouped validation is impossible on a dataset like that, and
 * so is reproducing any single result.</p>
 *
 * <p>This class centralises those parameters, reads them from
 * {@code params.properties}, seeds {@link Rng}, and exposes the columns the
 * writers append to every row. It also writes a JSON sidecar next to the
 * dataset so a run documents itself.</p>
 *
 * <p>The metadata columns are, in order:
 * {@code run_id, trace_id, batch_index, scenario_id, seed, attack_variant,
 * loss_rate, burst_size, traffic_rate, substation_config, impairment_mode,
 * impairment_rate, impairment_intensity_ms}. The last three describe the
 * card-C benign-degradation controls ({@link Impairment}) and are
 * {@code NONE}/0/0 for ordinary legitimate/attack runs. Downstream tooling
 * derives {@code event_id} and {@code split_group} from these.</p>
 *
 * @see Rng
 */
public final class RunContext {

    private static final String CONFIG_FILE = "params.properties";
    private static final Properties props = new Properties();
    private static final Logger logger = Logger.getLogger(RunContext.class.getName());

    /** Grayhole variants implemented by {@code OrientedGrayHoleCreator}. */
    public enum Variant {
        RANDOMIC_MESSAGE, RANDOMIC_BURST, DETERMINISTIC_BURST, FULLY_RANDOMIZED
    }

    /**
     * Non-malicious impairment mechanisms implemented by
     * {@code BenignImpairmentCreator} (major-revision card C: paired
     * {@code benign_degradation} controls, see
     * {@code experiments/revision_2026/benign_controls.md} §3). {@code NONE}
     * means this run is an ordinary legitimate/attack run, not a benign
     * control.
     */
    public enum Impairment {
        NONE, CONGESTION_LOSS, QUEUE_OVERLOAD_BURST, JITTER, DELAY, LINK_FLAP, DUPLICATION, REORDERING
    }

    public static long seed;
    public static String runId;
    public static String traceId;
    public static String scenarioId;
    public static Variant variant = Variant.FULLY_RANDOMIZED;
    public static int discardRate = 15;
    public static int burstSize = 5;
    public static double trafficRate = 1.0;
    public static String substationConfig;

    // Benign impairment (card C) parameters. `rate` is a Bernoulli percent
    // reused by every probabilistic mode (CONGESTION_LOSS, QUEUE_OVERLOAD_BURST,
    // DUPLICATION, REORDERING); `burst`/`period` gate the two deterministic-ish
    // burst modes; `jitterMs`/`delayMs` are magnitudes for the two no-loss modes.
    // See BenignImpairmentCreator for which mode reads which field.
    public static Impairment impairmentMode = Impairment.NONE;
    public static double impairmentRate = 15.0;
    public static int impairmentBurst = 5;
    public static int impairmentPeriod = 20;
    public static double impairmentJitterMs = 10.0;
    public static double impairmentDelayMs = 50.0;

    /** Index of the batch currently being generated; set by the scenario loop. */
    public static int batchIndex = 0;

    private static boolean loaded = false;

    private RunContext() {
    }

    public static synchronized void loadConfigs() {
        try (InputStream input = RunContext.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException(CONFIG_FILE + " not found in classpath");
            }
            props.load(input);

            String rawSeed = props.getProperty("run.seed", "auto").trim();
            if (rawSeed.isEmpty() || "auto".equalsIgnoreCase(rawSeed)) {
                // Draw one, then keep it: the run is still reproducible because
                // the value is logged and written into every row and the sidecar.
                seed = System.nanoTime();
                logger.warning("run.seed=auto - drew seed " + seed
                        + ". Put this value in params.properties to repeat this run exactly.");
            } else {
                seed = Long.parseLong(rawSeed);
            }
            Rng.seed(seed);

            variant = Variant.valueOf(
                    props.getProperty("attack.orientedGrayhole.variant", "FULLY_RANDOMIZED").trim().toUpperCase(Locale.ROOT));
            discardRate = Integer.parseInt(props.getProperty("attack.orientedGrayhole.discardRate", "15").trim());
            burstSize = Integer.parseInt(props.getProperty("attack.orientedGrayhole.burstSize", "5").trim());

            impairmentMode = Impairment.valueOf(
                    props.getProperty("attack.benignImpairment.mode", "NONE").trim().toUpperCase(Locale.ROOT));
            impairmentRate = Double.parseDouble(props.getProperty("attack.benignImpairment.rate", "15").trim());
            impairmentBurst = Integer.parseInt(props.getProperty("attack.benignImpairment.burst", "5").trim());
            impairmentPeriod = Integer.parseInt(props.getProperty("attack.benignImpairment.period", "20").trim());
            impairmentJitterMs = Double.parseDouble(props.getProperty("attack.benignImpairment.jitterMs", "10").trim());
            impairmentDelayMs = Double.parseDouble(props.getProperty("attack.benignImpairment.delayMs", "50").trim());

            // Steady-state GOOSE republication rate, in messages per second.
            double maxTimeMs = Double.parseDouble(props.getProperty("goose.timing.maxTime", "1000").trim());
            trafficRate = maxTimeMs > 0 ? 1000.0 / maxTimeMs : Double.NaN;

            substationConfig = props.getProperty("run.substationConfig", "").trim();
            if (substationConfig.isEmpty()) {
                substationConfig = "SUB-" + props.getProperty("goose.protocol.goID", "unknown").trim();
            }

            scenarioId = props.getProperty("run.scenarioId", "").trim();
            if (scenarioId.isEmpty()) {
                scenarioId = (impairmentMode == Impairment.NONE)
                        ? "SC-" + variant.name()
                        : String.format(Locale.ROOT, "SC-BENIGN_%s-l%.0f-b%d",
                                impairmentMode.name(), impairmentRate, impairmentBurst);
            }

            runId = props.getProperty("run.id", "").trim();
            if (runId.isEmpty()) {
                runId = (impairmentMode == Impairment.NONE)
                        ? scenarioId + "-s" + seed
                        : String.format(Locale.ROOT, "BENIGN_%s-l%.0f-b%d-s%d",
                                impairmentMode.name(), impairmentRate, impairmentBurst, seed);
            }

            // One publisher stream per run, so the trace is the run. Kept as a
            // separate column because that stops being true the moment more
            // than one publisher is simulated.
            traceId = props.getProperty("run.traceId", "").trim();
            if (traceId.isEmpty()) {
                traceId = runId;
            }

            loaded = true;
            logger.info(summary());

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from " + CONFIG_FILE, e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format in " + CONFIG_FILE, e);
        }
    }

    public static void requireLoaded() {
        if (!loaded) {
            throw new IllegalStateException("RunContext.loadConfigs() must run before message generation.");
        }
    }

    /**
     * Loss rate actually applied by the selected variant, in percent.
     *
     * <p>{@code DETERMINISTIC_BURST} never draws against {@code discardRate}: it
     * drops on every state change. Reporting 15 for it would put a number in the
     * dataset that the code does not use, so it reports 100.</p>
     */
    public static double effectiveLossRate() {
        if (impairmentMode != Impairment.NONE) {
            switch (impairmentMode) {
                case CONGESTION_LOSS:
                case QUEUE_OVERLOAD_BURST:
                    return impairmentRate;
                case LINK_FLAP:
                    return 100.0; // unconditional drop-on-trigger, same convention as DETERMINISTIC_BURST
                default:
                    return 0.0; // JITTER, DELAY, DUPLICATION, REORDERING drop nothing
            }
        }
        return variant == Variant.DETERMINISTIC_BURST ? 100.0 : discardRate;
    }

    /**
     * Burst length actually applied by the selected variant, in messages.
     *
     * <p>{@code FULLY_RANDOMIZED} evaluates each message independently and never
     * reads {@code burstSize}, so its bursts are single messages.</p>
     */
    public static int effectiveBurstSize() {
        if (impairmentMode != Impairment.NONE) {
            switch (impairmentMode) {
                case QUEUE_OVERLOAD_BURST:
                case LINK_FLAP:
                    return impairmentBurst;
                default:
                    return 1;
            }
        }
        return variant == Variant.FULLY_RANDOMIZED ? 1 : burstSize;
    }

    /**
     * Bernoulli/period trigger probability actually used by the selected
     * impairment mode, in percent. Kept separate from {@link #effectiveLossRate()}
     * because DUPLICATION/REORDERING trigger on this rate without losing any
     * packet, so folding it into {@code loss_rate} would misreport them.
     */
    public static double impairmentEffectiveRate() {
        switch (impairmentMode) {
            case CONGESTION_LOSS:
            case QUEUE_OVERLOAD_BURST:
            case DUPLICATION:
            case REORDERING:
                return impairmentRate;
            default:
                return 0.0;
        }
    }

    /** Jitter/delay magnitude actually applied, in milliseconds; 0 for every other mode. */
    public static double impairmentEffectiveIntensityMs() {
        switch (impairmentMode) {
            case JITTER:
                return impairmentJitterMs;
            case DELAY:
                return impairmentDelayMs;
            default:
                return 0.0;
        }
    }

    /** Header fragment appended to the dataset header, without a trailing comma. */
    public static String csvHeader() {
        return "run_id,trace_id,batch_index,scenario_id,seed,attack_variant,"
                + "loss_rate,burst_size,traffic_rate,substation_config,"
                + "impairment_mode,impairment_rate,impairment_intensity_ms";
    }

    /** Row fragment appended to every dataset row, without a trailing comma. */
    public static String csvRow() {
        // attack_variant describes the grayhole variant, which does not run
        // during a benign-impairment run - report "NONE" rather than whatever
        // attack.orientedGrayhole.variant happens to be configured to.
        String attackVariantField = (impairmentMode == Impairment.NONE) ? variant.name() : "NONE";
        return String.format(Locale.ROOT, "%s,%s,%d,%s,%d,%s,%.4f,%d,%.4f,%s,%s,%.4f,%.4f",
                runId, traceId, batchIndex, scenarioId, seed, attackVariantField,
                effectiveLossRate(), effectiveBurstSize(), trafficRate, substationConfig,
                impairmentMode.name(), impairmentEffectiveRate(), impairmentEffectiveIntensityMs());
    }

    public static String summary() {
        return String.format(Locale.ROOT,
                "Run %s | trace=%s | scenario=%s | seed=%d | variant=%s | loss_rate=%.1f%% | burst_size=%d | traffic_rate=%.3f msg/s | substation=%s | impairment=%s",
                runId, traceId, scenarioId, seed, variant.name(),
                effectiveLossRate(), effectiveBurstSize(), trafficRate, substationConfig, impairmentMode.name());
    }

    /**
     * Writes a JSON sidecar describing the run, next to the dataset file.
     * Named after the dataset with a {@code .run.json} suffix.
     */
    public static void writeManifest(String datasetPath) {
        Path out = Paths.get(datasetPath + ".run.json");
        String json = String.format(Locale.ROOT,
                "{%n"
                        + "  \"run_id\": \"%s\",%n"
                        + "  \"trace_id\": \"%s\",%n"
                        + "  \"scenario_id\": \"%s\",%n"
                        + "  \"seed\": %d,%n"
                        + "  \"attack_variant\": \"%s\",%n"
                        + "  \"discard_rate_config\": %d,%n"
                        + "  \"burst_size_config\": %d,%n"
                        + "  \"loss_rate_effective\": %.4f,%n"
                        + "  \"burst_size_effective\": %d,%n"
                        + "  \"traffic_rate_msgs_per_s\": %.4f,%n"
                        + "  \"substation_config\": \"%s\",%n"
                        + "  \"batches\": %d,%n"
                        + "  \"impairment_mode\": \"%s\",%n"
                        + "  \"impairment_rate_config\": %.4f,%n"
                        + "  \"impairment_burst_config\": %d,%n"
                        + "  \"impairment_period_config\": %d,%n"
                        + "  \"impairment_jitter_ms_config\": %.4f,%n"
                        + "  \"impairment_delay_ms_config\": %.4f,%n"
                        + "  \"goose\": {%n"
                        + "    \"gocbRef\": \"%s\",%n"
                        + "    \"datSet\": \"%s\",%n"
                        + "    \"goID\": \"%s\",%n"
                        + "    \"appId\": \"%s\",%n"
                        + "    \"ethSrc\": \"%s\",%n"
                        + "    \"ethDst\": \"%s\",%n"
                        + "    \"minTime_ms\": \"%s\",%n"
                        + "    \"maxTime_ms\": \"%s\"%n"
                        + "  }%n"
                        + "}%n",
                runId, traceId, scenarioId, seed, variant.name(),
                discardRate, burstSize, effectiveLossRate(), effectiveBurstSize(),
                trafficRate, substationConfig, batchIndex,
                impairmentMode.name(), impairmentRate, impairmentBurst, impairmentPeriod,
                impairmentJitterMs, impairmentDelayMs,
                props.getProperty("goose.protocol.gocbRef", ""),
                props.getProperty("goose.protocol.datSet", ""),
                props.getProperty("goose.protocol.goID", ""),
                props.getProperty("goose.protocol.appId", ""),
                props.getProperty("goose.network.ethSrc", ""),
                props.getProperty("goose.network.ethDst", ""),
                props.getProperty("goose.timing.minTime", ""),
                props.getProperty("goose.timing.maxTime", ""));

        try {
            if (out.getParent() != null) {
                Files.createDirectories(out.getParent());
            }
            try (Writer w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
                w.write(json);
            }
            logger.info("Run manifest written: " + out);
        } catch (IOException e) {
            logger.severe("Failed to write run manifest: " + e.getMessage());
        }
    }
}
