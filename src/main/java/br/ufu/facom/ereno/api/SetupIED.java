package br.ufu.facom.ereno.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Central configuration handler for Intelligent Electronic Device (IED) settings.
 *
 * Manages core parameters for IED operation including:
 * - Device identification (name, references)
 * - Timing controls (min/max intervals)
 * - Message sequencing (stNum, sqNum)
 * - Dataset configurations
 *
 * Automatically loads defaults from {@code params.properties} and provides
 * methods to persist changes or export as JSON. Serves as the
 * single configuration source for IED instances across the system.
 *
 * @see br.ufu.facom.ereno.api.Attacks
 * @see br.ufu.facom.ereno.api.GooseFlow
 */

public class SetupIED {
    private static final Logger logger = Logger.getLogger(SetupIED.class.getName());
    private static final Properties config = new Properties();

    public static String iedName = "defaultIED";
    public static String gocbRef = "LD/LLN0$GO$gcbA";
    public static String datSet = "LD/LLN0$IntLockA";
    public static String minTime = "100";
    public static String maxTime = "1000";
    public static String timestamp = "0";
    public static String stNum = "1";
    public static String sqNum = "1";

    static {
        loadConfigs();
    }

    public static void loadConfigs() {
        try (InputStream input = SetupIED.class.getClassLoader()
                .getResourceAsStream("params.properties")) {

            if (input == null) {
                logger.warning("ied.properties not found, using default configuration");
                return;
            }

            config.load(input);

            iedName = config.getProperty("ied.name", iedName);
            gocbRef = config.getProperty("ied.gocbRef", gocbRef);
            datSet = config.getProperty("ied.datSet", datSet);
            minTime = config.getProperty("ied.minTime", minTime);
            maxTime = config.getProperty("ied.maxTime", maxTime);
            timestamp = config.getProperty("ied.timestamp", timestamp);
            stNum = config.getProperty("ied.stNum", stNum);
            sqNum = config.getProperty("ied.sqNum", sqNum);

        } catch (IOException e) {
            logger.severe("Error loading IED configuration: " + e.getMessage());
        }
    }

    public static void saveConfiguration() {
        try {
            config.setProperty("ied.name", iedName);
            config.setProperty("ied.gocbRef", gocbRef);
            config.setProperty("ied.datSet", datSet);
            config.setProperty("ied.minTime", minTime);
            config.setProperty("ied.maxTime", maxTime);
            config.setProperty("ied.timestamp", timestamp);
            config.setProperty("ied.stNum", stNum);
            config.setProperty("ied.sqNum", sqNum);

            Path configPath = Paths.get(System.getProperty("user.dir"),
                    "src", "main", "resources", "ied.properties");

            Files.createDirectories(configPath.getParent());

            try (var writer = Files.newBufferedWriter(configPath)) {
                config.store(writer, "IED Configuration");
                logger.info("IED configuration saved successfully");
            }

        } catch (IOException e) {
            logger.severe("Failed to save IED configuration: " + e.getMessage());
            throw new RuntimeException("Failed to save IED configuration", e);
        }
    }

    public static String toJson() {
        return String.format(
                "{\"iedName\":\"%s\",\"gocbRef\":\"%s\",\"datSet\":\"%s\"," +
                        "\"minTime\":\"%s\",\"maxTime\":\"%s\",\"timestamp\":\"%s\"," +
                        "\"stNum\":\"%s\",\"sqNum\":\"%s\"}",
                iedName, gocbRef, datSet, minTime, maxTime, timestamp, stNum, sqNum
        );
    }
}