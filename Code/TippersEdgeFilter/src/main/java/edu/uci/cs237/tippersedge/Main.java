package edu.uci.cs237.tippersedge;

import edu.uci.cs237.tippersedge.cameras.CameraRestClient;
import edu.uci.cs237.tippersedge.cameras.CameraSampleHandler;
import edu.uci.cs237.tippersedge.darknet.DarknetConfig;
import edu.uci.cs237.tippersedge.darknet.DarknetProcess;
import edu.uci.cs237.tippersedge.sensoria.MockImageUploader;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Application entry point.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
//        if (args.length < 1) {
//            System.out.println(String.format("usage: java %s darknetDir [imageFile]", Main.class.getName()));
//            return;
//        }
//        String darknetDir = args[0];
//        String imageFile = args.length > 1 ? args[1] : Main.class.getResource("/img/eagle.jpg").getFile();





        // =============================================================================================================
        // Insert code for testing/debugging functionality here...

        /*
        testInclusionLogic("/Users/varmarken/temp/tippers/sample_data/test_set");
        */

        /*
        benchmarkDarknet("/Users/varmarken/temp/tippers/sample_data", 100);
        */


        CameraSampleHandler cameraSampleHandler = new CameraSampleHandler(new CameraRestClient(),
                300,
                30_000,
                DarknetConfig.getDarknetDirectory(),
                new MockImageUploader()
        );
        cameraSampleHandler.startPeriodicSampling();
        Thread.sleep(15 * 20_000);
        // Terminate sampling and wait (block) for 30 seconds for tasks to terminate.
        // TODO this seems to ignore the fact that Darknet is still running
        cameraSampleHandler.stopPeriodicSampling(true, 30_000);


        /*
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
        */

        // =============================================================================================================

    }

    /**
     * Benchmark darknet by computing the average time it takes to execute darknet on each .jpg image in the given directory.
     * @param imgDir The directory where the files to be used in the benchmark reside.
     * @param cap A maximum number of images to be included in the benchmark.
     */
    static void benchmarkDarknet(String imgDir, int cap) throws IOException, InterruptedException {
        DarknetProcess dp = new DarknetProcess(DarknetConfig.getDarknetDirectory());
        List<String> images = getListOfJpgImagesInDir(imgDir, cap);
        List<Long> executionTimes = new ArrayList<>();
        int count = 0;
        for (String img : images) {
            count++;
            long start = System.currentTimeMillis();
            List<DarknetProcess.DetectedObject> objects = dp.exec(img);
            long end = System.currentTimeMillis();
            long execTimeMillis = end - start;
            objects.stream().forEach(o -> o.toString());
            System.out.println(String.format("Execution time for img %d out of %d was %d milliseconds", count, images.size(), execTimeMillis));
            System.out.println("=================================================================");
            executionTimes.add(execTimeMillis);
        }
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Long execTime : executionTimes) {
            // Ugh, may loose precision when converting from long to double
            stats.addValue(execTime);
        }
        System.out.println("Average execution time: " + stats.getMean());
        System.out.println("Standard deviation: " + stats.getStandardDeviation());
        System.out.println("Variance: " + stats.getVariance());

        // double avgExecTimeMillis = executionTimes.stream().reduce((l1, l2) -> l1 + l2).get() / new Double(count);
    }

    static void testInclusionLogic(String imgDir) {
        final List<String> images = getListOfJpgImagesInDir(imgDir, Integer.MAX_VALUE);
        Collections.sort(images);
        // Overwrite the standard rest client to just return images from a local directory.
        CameraRestClient crc = new CameraRestClient() {

            private int count = 0;

            @Override
            public String sample() {
                return images.get(count++);
            }

        };
        CameraSampleHandler csh = new CameraSampleHandler(crc, images.size() / 2, 1_000_000_000, DarknetConfig.getDarknetDirectory(), new MockImageUploader());
        int i = 0;
        while (i < images.size()) {
            csh.sampleAndUpload();
            i++;
        }
    }

    private static List<String> getListOfJpgImagesInDir(String imgDir, int cap) {
        File dir = new File(imgDir);
        File[] fileList = dir.listFiles();
        List<String> images = new ArrayList<>();
        for(File f : fileList) {
            if (f.isFile() && f.getName().endsWith(".jpg")) {
                images.add(f.getAbsolutePath());
            }
            if (images.size() >= cap) {
                break;
            }
        }
        return images;
    }

}
