package edu.uci.cs237.tippersedge.cameras;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Interface for downloading an image.
 * Allows for dependency injection (see
 * <a href="https://stackoverflow.com/a/32007266/1214974">https://stackoverflow.com/a/32007266/1214974</a> for an
 * example of how to use the design pattern).
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public interface ImageSupplier {

    /**
     * Download an image from a REST endpoint and return an in-memory representation of it.
     * @param webTargetUrl REST Endpoint that serves the image to be downloaded.
     * @return An in-memory representation of the downloaded image.
     */
    BufferedImage downloadImage(String webTargetUrl) throws IOException;


    /**
     * Download an image from a REST endpoint and store it on disk.
     * @param webTargetUrl REST Endpoint that serves the image to be downloaded.
     * @param fileName The absolute name of the file in which the image is to be stored.
     * @return {@code true} if the image was successfully downloaded and stored on disk, {@code false} otherwise.
     * @throws IOException
     */
    boolean downloadAndStoreImage(String webTargetUrl, String fileName) throws IOException;

}
