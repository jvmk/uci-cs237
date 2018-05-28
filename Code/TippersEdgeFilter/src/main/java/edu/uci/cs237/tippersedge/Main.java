package edu.uci.cs237.tippersedge;

import edu.uci.cs237.tippersedge.cameras.CameraConfig;
import edu.uci.cs237.tippersedge.cameras.CameraRestClient;
import edu.uci.cs237.tippersedge.cameras.MockImageSupplier;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Application entry point.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 1) {
            System.out.println(String.format("usage: java %s darknetDir [imageFile]", Main.class.getName()));
            return;
        }
        String darknetDir = args[0];
        String imageFile = args.length > 1 ? args[1] : Main.class.getResource("/img/eagle.jpg").getFile();

        // =============================================================================================================
        // Insert code for testing/debugging functionality here...

        // Perform object detection on imageFile
        List<DarknetProcess.DetectedObject> detectedObjects = new DarknetProcess(darknetDir).exec(imageFile);
        for (DarknetProcess.DetectedObject detectedObj : detectedObjects) {
            System.out.println(detectedObj.toString());
        }

        // Mock REST client.
        MockImageSupplier imgSupplier = new MockImageSupplier();
        BufferedImage img = imgSupplier.downloadImage("ignored by mock implementation");

        // Concrete REST client.
        CameraRestClient restClient = new CameraRestClient();
        // Download image from camera and store it current user's documents dir.
        // Note: HTTPS currently not supported (need to add authentication code for that in CamreRestClient).
        String userHome = System.getProperty("user.home");
        String outputFile = userHome + "/Documents/camerarestclienttest.jpg";
        // You can download below image for instead of camera image for testing purposes if you cannot access the camera
        // (e.g., when not on a UCI network).
//        boolean success = restClient.downloadAndStoreImage("http://itu.dk/people/janv/mufc_abc.jpg", outputFile);
        boolean success = restClient.downloadAndStoreImage(CameraConfig.getCameraUrl(), outputFile);
        if (success) {
            System.out.println("Successfully downloaded and stored image in " + outputFile);
        } else {
            System.out.println("ERROR: image download failed, or failed to store downloaded image on disk");
        }

        // =============================================================================================================
    }

}