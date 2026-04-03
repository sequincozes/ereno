package br.ufu.facom.ereno.scenarios;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * Utility class for retrieving Sampled Values (SV) input files from a configured directory.
 *
 * Loads configuration from 'config.properties' in the classpath, including:
 * - root.directory: the base directory to search for files
 * - file.extension: filter files by this extension (default ".out")
 * - max.depth: maximum directory depth to traverse (default unlimited)
 *
 * Uses parallel streams and a thread-safe collection for efficient file discovery.
 *
 * Provides methods to:
 * - Get all electrical source files matching the configured criteria
 * - Access configuration properties with or without default values
 *
 * Usage example:
 * <pre>{@code
 * String[] svFiles = InputFilesForSV.getElectricalSourceFiles();
 * for (String file : svFiles) {
 *     System.out.println(file);
 * }
 * }</pre>
 *
 * @see java.nio.file.Files
 * @see java.util.stream.Stream
 */

public class InputFilesForSV {
    private static final Properties config = new Properties();

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        try (InputStream input = InputFilesForSV.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            config.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String[] getElectricalSourceFiles() {
        String rootDir = config.getProperty("root.directory");
        String fileExtension = config.getProperty("file.extension", ".out");
        int maxDepth = Integer.parseInt(config.getProperty("max.depth", "2147483647"));

        if (rootDir == null || rootDir.trim().isEmpty()) {
            throw new RuntimeException("root.directory not specified in config.properties");
        }

        ConcurrentLinkedQueue<String> fileQueue = new ConcurrentLinkedQueue<>();

        try (Stream<Path> pathStream = Files.walk(Paths.get(rootDir), maxDepth)) {
            pathStream.parallel()
                    .filter(Files::isRegularFile).filter(p -> p.toString().toLowerCase().endsWith(fileExtension.toLowerCase())).forEach(p -> fileQueue.add(p.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading files from directory: " + rootDir, e);
        }

        return fileQueue.stream().sorted().toArray(String[]::new);
    }

    public static String getConfig(String key) {
        return config.getProperty(key);
    }

    public static String getConfig(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }
}