package edu.uci.cs237.tippersedge;

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

        // Mock REST service.
        MockImageSupplier imgSupplier = new MockImageSupplier();
        BufferedImage img = imgSupplier.downloadImage("ignored by mock implementation");

        // Perform object detection on imageFile
        List<DarknetProcess.DetectedObject> detectedObjects = new DarknetProcess(darknetDir).exec(imageFile);
        for (DarknetProcess.DetectedObject detectedObj : detectedObjects) {
            System.out.println(detectedObj.toString());
        }

        // =============================================================================================================
    }

}