package edu.uci.cs237.tippersedge.darknet;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;

/**
 * Loads the Darknet configuration from resources and exposes its contents to the rest of the application as static methods.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}.
 */
public class DarknetConfig {

    private static final String DARKNET_CONFIG_FILENAME = "/cfg/darknetconfig.properties";

    private static final Properties PROPERTIES;

    // ==== Begin keys used in properties file ====
    private static final String DARKNET_DIR_KEY = "darknetDir";
    // ===== End keys used in properties file =====

    // ==== Begin cached values of PROPERTIES contents ====
    private static final String DARKNET_DIR;
    // ===== End cached values of PROPERTIES contents =====

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(DarknetConfig.class.getResourceAsStream(DARKNET_CONFIG_FILENAME));
            DARKNET_DIR = Objects.requireNonNull(PROPERTIES.getProperty(DARKNET_DIR_KEY, null),
                    String.format("No value for key '%s' in properties file '%s'", DARKNET_DIR_KEY, DARKNET_CONFIG_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
            throw new MissingResourceException(
                    String.format("Darknet configuration file not found in resources. Missing file: '%s'", DARKNET_CONFIG_FILENAME),
                    DarknetConfig.class.getName(),
                    DARKNET_CONFIG_FILENAME
            );

        }
    }

    private DarknetConfig() {
        // Make constructor private in order to prevent instantiation of class.
    }

    /**
     * Get the local directory where Darknet is installed.
     * @return the local directory where Darknet is installed
     */
    public static String getDarknetDirectory() {
        return DARKNET_DIR;
    }

}
