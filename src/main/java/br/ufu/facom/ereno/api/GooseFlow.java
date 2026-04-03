package br.ufu.facom.ereno.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Manages GOOSE message flow parameters for IEC 61850 simulations.
 *
 * <p>Loads and saves configuration from {@code params.properties}, exposing
 * Ethernet frame parameters, GOOSE identifiers, and protocol flags as static fields.</p>
 *
 * <p>Configuration is automatically loaded at class initialization.</p>
 *
 * @see br.ufu.facom.ereno.api.Attacks
 */

public class GooseFlow {
    public static String goID;
    public static int numberOfMessages;
    public static String ethSrc;
    public static String ethDst;
    public static String ethType;
    public static String gooseAppid;
    public static String TPID;
    public static boolean ndsCom;
    public static boolean test;
    public static boolean cbstatus;

    private static final String CONFIG_FILE = "params.properties";
    private static final Properties props = new Properties();

    static {
        loadConfigs();
    }

    public static void loadConfigs() {
        try (InputStream input = GooseFlow.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new RuntimeException(CONFIG_FILE + " not found in classpath");
            }

            props.load(input);

            goID = props.getProperty("goose.flow.goID");
            numberOfMessages = Integer.parseInt(props.getProperty("goose.flow.numberOfMessages", "0"));
            ethSrc = props.getProperty("goose.flow.ethSrc");
            ethDst = props.getProperty("goose.flow.ethDst");
            ethType = props.getProperty("goose.flow.ethType");
            gooseAppid = props.getProperty("goose.flow.gooseAppid");
            TPID = props.getProperty("goose.flow.TPID");
            ndsCom = Boolean.parseBoolean(props.getProperty("goose.flow.ndsCom", "false"));
            test = Boolean.parseBoolean(props.getProperty("goose.flow.test", "false"));
            cbstatus = Boolean.parseBoolean(props.getProperty("goose.flow.cbstatus", "false"));

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from " + CONFIG_FILE, e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format in configuration", e);
        }
    }

    public static void saveConfigs() {
        try {
            props.setProperty("goose.flow.goID", goID != null ? goID : "");
            props.setProperty("goose.flow.numberOfMessages", String.valueOf(numberOfMessages));
            props.setProperty("goose.flow.ethSrc", ethSrc != null ? ethSrc : "");
            props.setProperty("goose.flow.ethDst", ethDst != null ? ethDst : "");
            props.setProperty("goose.flow.ethType", ethType != null ? ethType : "");
            props.setProperty("goose.flow.gooseAppid", gooseAppid != null ? gooseAppid : "");
            props.setProperty("goose.flow.TPID", TPID != null ? TPID : "");
            props.setProperty("goose.flow.ndsCom", String.valueOf(ndsCom));
            props.setProperty("goose.flow.test", String.valueOf(test));
            props.setProperty("goose.flow.cbstatus", String.valueOf(cbstatus));

            Path configPath = Paths.get(System.getProperty("user.dir"),
                    "src", "main", "resources", CONFIG_FILE);

            try (var writer = Files.newBufferedWriter(configPath)) {
                props.store(writer, "Goose Flow Configuration");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration to " + CONFIG_FILE, e);
        }
    }
}