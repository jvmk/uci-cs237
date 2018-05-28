package edu.uci.cs237.tippersedge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class for invoking the Darknet command line tool.
 * Darknet performs (real-time) object detection in images.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class DarknetProcess {

    private final String mDarknetDir;
    private final String mImageFilepath;

    public DarknetProcess(String darknetDir, String imageFilepath) {
        mDarknetDir = darknetDir;
        mImageFilepath = imageFilepath;
    }

    public void exec() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("./darknet", "detect", "cfg/yolov3.cfg", "yolov3.weights", mImageFilepath);
        processBuilder.directory(new File(mDarknetDir));
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        Process process = processBuilder.start();
        // Can do a process.waitFor() here if we want the process to terminate before reading its output.
        // However, without it, we get outut as it arrives on the fly which provides better indication of progress.
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Note: Darknet seems to print its progress information to std.err.
                // However, "actual" errors will also show up in this output, e.g., if cfg/yolov3.cfg file is missing.
                System.err.println(line);
            }
        }
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
;    }

}
