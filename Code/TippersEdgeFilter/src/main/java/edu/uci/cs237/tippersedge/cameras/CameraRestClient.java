package edu.uci.cs237.tippersedge.cameras;

import edu.uci.cs237.tippersedge.SampleProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.time.Instant;

/**
 * Concrete implementation of {@link ImageSupplier} targeting a real REST endpoint.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}.
 */
public class CameraRestClient implements ImageSupplier, SampleProvider<String> {

    /**
     * Cameras require HTTP Basic authentication, so an authenticator must be provided.
     */
    private final Authenticator mAuthenticator = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(CameraConfig.getCameraUsername(), CameraConfig.getCameraPassword().toCharArray());
        }
    };

    public CameraRestClient() {
        // Set system default authenticator.
        Authenticator.setDefault(mAuthenticator);
    }

    @Override
    public BufferedImage downloadImage(String webTargetUrl) throws IOException {
        throw new UnsupportedOperationException("Work in progress, not yet implemented");
    }

    @Override
    public boolean downloadAndStoreImage(String webTargetUrl, String fileName) throws IOException {
        Client restClient = ClientBuilder.newClient();
        Response response = restClient.target(webTargetUrl).request("image/jpeg").get();
        // Create directories on filepath if they do not already exist.
        new File(fileName).getParentFile().mkdirs();
        // Use try-with-resource to automatically close streams upon leaving try block.
        try (BufferedInputStream input = new BufferedInputStream(response.readEntity(InputStream.class));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(fileName))) {
            byte[] buffer = new byte[8192];
            int readBytes;
            while ((readBytes = input.read(buffer)) != -1) {
                output.write(buffer, 0, readBytes);
            }
            output.flush();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    @Override
    public String sample() {
        /*
         * Put images in specified folder.
         * An image is named using the pattern 'imgXXX.jpg' where XXX is the current system epoch (millisecond
         * granularity). As such, if this method is called multiple times within a single millisecond, only the image
         * fetched as part of the last call will be persisted as earlier images will be overwritten by that image.
         * However, this will likely never be an issue as the network delay incurred by contacting the camera will
         * almost certainly be greater than a single millisecond.
         */
        String imgFilename = String.format("%s/img%d.jpg",
                CameraConfig.getCameraOutputDirectory(), Instant.now().toEpochMilli());
        boolean imgDownloaded;
        try {
            imgDownloaded = downloadAndStoreImage(CameraConfig.getCameraUrl(), imgFilename);
            return imgDownloaded ? imgFilename : null;
        } catch (IOException exc) {
            exc.printStackTrace();
            return null;
        }
    }
}