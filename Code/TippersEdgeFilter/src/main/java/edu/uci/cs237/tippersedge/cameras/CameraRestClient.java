package edu.uci.cs237.tippersedge.cameras;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Concrete implementation of {@link ImageSupplier} targeting a real REST endpoint.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}.
 */
public class CameraRestClient implements ImageSupplier {

    @Override
    public BufferedImage downloadImage(String webTargetUrl) throws IOException {
        throw new UnsupportedOperationException("Work in progress, not yet implemented");
    }

    @Override
    public boolean downloadAndStoreImage(String webTargetUrl, String fileName) throws IOException {
        Client restClient = ClientBuilder.newClient();
        Response response = restClient.target(webTargetUrl).request("image/jpeg").get();
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

}