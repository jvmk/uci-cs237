package edu.uci.cs237.tippersedge.cameras;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;

/**
 * Loads the camera configuration from resources and exposes its contents to the rest of the application as static methods.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}.
 */
public final class CameraConfig {

    private static final String RESOURCE_FILENAME = "/cfg/cameraconfig.properties";

    private static final Properties PROPERTIES;

    // ==== Begin keys used in properties file ====
    private static final String CAMERA_URL_KEY = "cameraUrl";
    private static final String CAMERA_USERNAME_KEY = "cameraUsername";
    private static final String CAMERA_PASSWORD_KEY = "cameraPassword";
    // ===== End keys used in properties file =====

    // ==== Begin cached values of PROPERTIES contents ====
    private static final String CAMERA_URL;
    private static final String CAMERA_USERNAME;
    private static final String CAMERA_PASSWORD;
    // ===== End cached values of PROPERTIES contents =====

    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(CameraConfig.class.getResourceAsStream(RESOURCE_FILENAME));
            // Throw NPE if required keys not present in config file.
            CAMERA_URL = Objects.requireNonNull(PROPERTIES.getProperty(CAMERA_URL_KEY),
                    String.format("No value for key '%s' in properties file '%s'", CAMERA_URL_KEY, RESOURCE_FILENAME));
            CAMERA_USERNAME = Objects.requireNonNull(PROPERTIES.getProperty(CAMERA_USERNAME_KEY),
                    String.format("No value for key '%s' in properties file '%s'", CAMERA_USERNAME_KEY, RESOURCE_FILENAME));
            CAMERA_PASSWORD = Objects.requireNonNull(PROPERTIES.getProperty(CAMERA_PASSWORD_KEY),
                    String.format("No value for key '%s' in properties file '%s'", CAMERA_PASSWORD_KEY, RESOURCE_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
            throw new MissingResourceException(
                    String.format("Camera configuration file not found in resources. Missing file: '%s'", RESOURCE_FILENAME),
                    CameraConfig.class.getName(), "Could not find file: /cfg/cameraconfig.properties"
            );
        }
    }

    private CameraConfig() {
        // Make constructor private in order to prevent instantiation of class.
    }

    public static String getCameraUrl() {
        return CAMERA_URL;
    }

    public static String getCameraUsername() {
        return CAMERA_USERNAME;
    }

    public static String getCameraPassword() {
        return CAMERA_PASSWORD;
    }

}
