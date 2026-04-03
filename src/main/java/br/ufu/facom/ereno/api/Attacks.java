package br.ufu.facom.ereno.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Central configuration manager for power system attack scenarios.
 *
 * <p>This class loads and maintains activation flags for various attack types
 * from the {@code attacks.properties} file and provides static boolean fields
 * to toggle attacks in the simulation framework.</p>
 *
 * <p>Use {@link #loadConfigs()} to reload configuration and
 * {@link #saveConfigs()} to persist changes.</p>
 *
 */

public class Attacks {
    private static final Logger logger = Logger.getLogger(Attacks.class.getName());
    private static final Properties props = new Properties();

    public static boolean legitimate = true;
    public static boolean randomReplay = false;
    public static boolean masqueradeOutage = false;
    public static boolean masqueradeDamage = false;
    public static boolean randomInjection = false;
    public static boolean inverseReplay = false;
    public static boolean highStNum = false;
    public static boolean flooding = false;
    public static boolean grayhole = false;
    public static boolean stealthyInjection = false;

    static {
        loadConfigs();
    }

    public static void loadConfigs() {
        try (InputStream input = Attacks.class.getClassLoader()
                .getResourceAsStream("attacks.properties")) {

            if (input == null) {
                logger.warning("attacks.properties not found, using default configuration");
                return;
            }

            props.load(input);

            legitimate = Boolean.parseBoolean(props.getProperty("attacks.legitimate", "true"));
            randomReplay = Boolean.parseBoolean(props.getProperty("attacks.randomReplay", "false"));
            masqueradeOutage = Boolean.parseBoolean(props.getProperty("attacks.masqueradeOutage", "false"));
            masqueradeDamage = Boolean.parseBoolean(props.getProperty("attacks.masqueradeDamage", "false"));
            randomInjection = Boolean.parseBoolean(props.getProperty("attacks.randomInjection", "false"));
            inverseReplay = Boolean.parseBoolean(props.getProperty("attacks.inverseReplay", "false"));
            highStNum = Boolean.parseBoolean(props.getProperty("attacks.highStNum", "false"));
            flooding = Boolean.parseBoolean(props.getProperty("attacks.flooding", "false"));
            grayhole = Boolean.parseBoolean(props.getProperty("attacks.grayhole", "false"));
            stealthyInjection = Boolean.parseBoolean(props.getProperty("attacks.stealthyInjection", "false"));

            logConfiguration();

        } catch (IOException e) {
            logger.severe("Error loading attacks configuration: " + e.getMessage());
        }
    }

    public static void saveConfigs() {
        try {
            props.setProperty("attacks.legitimate", String.valueOf(legitimate));
            props.setProperty("attacks.randomReplay", String.valueOf(randomReplay));
            props.setProperty("attacks.masqueradeOutage", String.valueOf(masqueradeOutage));
            props.setProperty("attacks.masqueradeDamage", String.valueOf(masqueradeDamage));
            props.setProperty("attacks.randomInjection", String.valueOf(randomInjection));
            props.setProperty("attacks.inverseReplay", String.valueOf(inverseReplay));
            props.setProperty("attacks.highStNum", String.valueOf(highStNum));
            props.setProperty("attacks.flooding", String.valueOf(flooding));
            props.setProperty("attacks.grayhole", String.valueOf(grayhole));
            props.setProperty("attacks.stealthyInjection", String.valueOf(stealthyInjection));

            // Determine the path to the properties file
            Path configPath = Paths.get(System.getProperty("user.dir"),
                    "src", "main", "resources", "attacks.properties");

            // Create parent directories if needed
            Files.createDirectories(configPath.getParent());

            // Write the properties to file
            try (var writer = Files.newBufferedWriter(configPath)) {
                props.store(writer, "Attacks Configuration");
                logger.info("Attacks configuration saved successfully");
            }

        } catch (IOException e) {
            logger.severe("Failed to save attacks configuration: " + e.getMessage());
            throw new RuntimeException("Failed to save attacks configuration", e);
        }
    }

    private static void logConfiguration() {
        logger.info("Attacks Configuration:");
        logger.info("  Legitimate: " + legitimate);
        logger.info("  Random Replay: " + randomReplay);
        logger.info("  Masquerade Outage: " + masqueradeOutage);
        logger.info("  Masquerade Damage: " + masqueradeDamage);
        logger.info("  Random Injection: " + randomInjection);
        logger.info("  Inverse Replay: " + inverseReplay);
        logger.info("  High StNum: " + highStNum);
        logger.info("  Flooding: " + flooding);
        logger.info("  Grayhole: " + grayhole);
        logger.info("  Stealthy Injection: " + stealthyInjection);
    }
}
