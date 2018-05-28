package edu.uci.cs237.tippersedge;

import java.io.IOException;
import java.util.List;

/**
 * Application entry point.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println(String.format("usage: java %s darknetDir imageFile", Main.class.getName()));
            return;
        }
        String darknetDir = args[0];
        String imageFile = args[1];
        List<DarknetProcess.DetectedObject> detectedObjects = new DarknetProcess(darknetDir).exec(imageFile);
        for (DarknetProcess.DetectedObject detectedObj : detectedObjects) {
            System.out.println(detectedObj.toString());
        }
    }

}
