package edu.uci.cs237.tippersedge.darknet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class for invoking the Darknet command line tool
 * (see <a href="https://pjreddie.com/darknet/yolo/">https://pjreddie.com/darknet/yolo/</a>).
 * Darknet performs (real-time) object detection in images.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class DarknetProcess {

    /**
     * Regex pattern for detecting percentages in a string (e.g., of the form '92%').
     */
    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("\\d+%");

    /**
     * The directory where Darknet resides.
     */
    private final String mDarknetDir;

    /**
     * Process builder for spawning Darknet processes.
     */
    private final ProcessBuilder mProcessBuilder;

    public DarknetProcess(String darknetDir) {
        mDarknetDir = darknetDir;
        mProcessBuilder = new ProcessBuilder();
        mProcessBuilder.directory(new File(darknetDir));
    }

    /**
     * Spawn an instance of Darknet and execute it on the image specified by {@code imageFilepath}.
     * @param imageFilepath The full path to the image on which object detection is to be performed.
     * @return A list of objects detected by Darknet.
     * @throws IOException
     * @throws InterruptedException
     */
    public List<DetectedObject> exec(String imageFilepath) throws IOException, InterruptedException {
        mProcessBuilder.command("./darknet", "detect", "cfg/yolov3.cfg", "yolov3.weights", imageFilepath);
        Process darknet = mProcessBuilder.start();
        // Can do a Process.waitFor() here if we want the process to terminate before reading its output.
        // However, without it, we get outut as it arrives on the fly which provides better indication of progress.
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(darknet.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Note: Darknet seems to print its progress information to std.err.
                // However, "actual" errors will also show up in this output, e.g., if cfg/yolov3.cfg file is missing.
//                System.err.println(line);
            }
        }
        List<DetectedObject> detectedObjects = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(darknet.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                DetectedObject detectedObj = parseOutputLine(line);
                if (detectedObj != null) {
                    detectedObjects.add(detectedObj);
                }
            }
        }
        return detectedObjects;
    }

    /**
     * Parses a Darknet output line.
     * The output line is examined to detect if it contains information about an object detected by Darknet.
     * If yes, the information about the detected object is contained in the returned {@link DetectedObject}.
     * @param line A Darknet output line.
     * @return A {@link DetectedObject} that holds information about the object detected by Darknet or
     * {@code null} if {@code line} is not an output line containing information about a detected object.
     */
    private DetectedObject parseOutputLine(String line) {
        String[] items = line.split(" ");
        // We are only interested in those lines of the output that indicate that an object was recognized.
        // These are of the form 'objectname: confidence%', e.g., 'person: 95%'.
        if (items.length == 2 && PERCENTAGE_PATTERN.matcher(items[1]).matches()) {
            // Remove the colon from the object name.
            String objName = items[0].substring(0, items[0].length()-1);
            // Remove the percentage sign.
            int confidence = Integer.parseInt(items[1].substring(0, items[1].length()-1));
            return new DetectedObject(objName, confidence);
        }
        return null;
    }

    /**
     * Models an object detected by Darknet.
     */
    public static class DetectedObject {

        /**
         * Darknet's name/description of the detected object.
         */
        private final String mObjectName;
        /**
         * Darknet's confidence/certainty in its detection of this object (a percentage).
         */
        private final int mConfidence;

        public DetectedObject(String objectName, int confidence) {
            mObjectName = Objects.requireNonNull(objectName, "objectName cannot be null");
            mConfidence = confidence;
        }

        /**
         * Get the name/description of this detected object.
         * @return the name/description of this detected object.
         */
        public String getName() {
            return mObjectName;
        }

        /**
         * Get Darknet's confidence in its detection of this detected object (percentage).
         * @return confidence in its detection of this detected object (percentage).
         */
        public int getConfidence() {
            return mConfidence;
        }

        @Override
        public String toString() {
            return String.format("[ DetectedObject of type '%s' with confidence '%d' ]", mObjectName, mConfidence);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DetectedObject)) {
                return false;
            }
            DetectedObject other = (DetectedObject) obj;
            return other.mObjectName.equals(this.mObjectName) && other.mConfidence == other.mConfidence;
        }
    }

    // TODO should also overwrite hashCode according to overwrite of equals.

}