package edu.uci.cs237.tippersedge.cameras;

import edu.uci.cs237.tippersedge.AbstractPeriodicSampleHandler;
import edu.uci.cs237.tippersedge.DarknetProcess;
import edu.uci.cs237.tippersedge.sensoria.ImageUploader;

import java.io.IOException;
import java.util.List;

/**
 * A concrete implementation of {@link AbstractPeriodicSampleHandler} for images shot by the surveillance cameras.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class CameraSampleHandler extends AbstractPeriodicSampleHandler<String> {

    /**
     * Flag for toggling console debug output on/off.
     */
    private static final boolean ENABLE_DEBUG_OUTPUT = true;

    /**
     * Provides object detection capabilities.
     */
    private final DarknetProcess mDarknetProcess;

    /**
     * REST client for uploading images that are to slip through the filter.
     */
    private final ImageUploader mImageUploader;

    public CameraSampleHandler(CameraRestClient cameraRestClient, int sampleCacheSize, long sampleRateMillis, String darknetDir, ImageUploader restClient) {
        super(cameraRestClient, sampleCacheSize, sampleRateMillis);
        mDarknetProcess = new DarknetProcess(darknetDir);
        mImageUploader = restClient;
    }

    @Override
    protected boolean shouldIncludeSample(String sample) {
        /*
         * TODO implement comparison of last image and new image here.
         * For now, we simply use a basic comparison that checks if the scene is exactly the same.
         *
         * TODO currently, AbstractSampleHandler does not add samples to its cache until after they are succesfully
         * uploaded. This is problematic as Darknet takes approximately 13-15 seconds to execute on Janus' MBP.
         * As we sample every ~2 seconds or so, we are essentially comparing the new image to the least-recently cached
         * image which will hence be 13-15 seconds 'old'.
         * Hence we may want to add images to the cache immediately, rather than wait until after they are analyzed.
         * On the other hand, this might cause issues as we are thereby possibly comparing the new image to an image
         * that will never be uploaded.... hmm... ideas?
         */
        String previousImg = null;
        synchronized (mSampleCache) {
            if (mSampleCache.size() > 0) {
                // Get the location of the most-recently cached image.
                previousImg = mSampleCache.get(mSampleCache.size()-1);
            }
        }
        /*
         * Note that we should NOT perform object detection nor upload of the image inside the synchronized block as
         * these are very slow operations that would cause other threads, which access the list, to block for a long
         * time.
         */
        if(previousImg == null) {
            // No previously cached image, so definitely upload this one.
            if (ENABLE_DEBUG_OUTPUT) {
                System.out.println(String.format("[ No previous img in cache; approving upload of img '%s' ]", sample));
            }
            return true;
        }
        try {
            List<DarknetProcess.DetectedObject> oldScene = mDarknetProcess.exec(previousImg);
            List<DarknetProcess.DetectedObject> newScene = mDarknetProcess.exec(sample);
            if (oldScene.size() != newScene.size()) {
                // Scenes definitely differ as there is a different number of objects in the two.
                if (ENABLE_DEBUG_OUTPUT) {
                    System.out.println(String.format("[ Number of detected objects in previous img and new img differ; approving upload of img '%s' ]", sample));
                }
                return true;
            }
            int matchedObjects = 0;

	    for (int i  = 0; i < oldScene.size(); i++) {
   	     		if (oldScene.get(i).equals(newScene.get(i)))
				matchedObjects++;
	    }
	    boolean identical = matchedObjects == oldScene.size(); 
  
            if (ENABLE_DEBUG_OUTPUT) {
                if (identical) {
                    System.out.println(String.format("[ Previous img and new img identical; disapproving upload of img '%s' ]", sample));
                } else {
                    System.out.println(String.format("[ Previous img and new img represent different scenes; approving upload of img '%s' ]", sample));
                }
            }
            // Only upload this image if there is a discrepancy between the objects of the new and the old scene.
            return !identical;
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
            // Always upload on error -- TODO: better strategy?
            System.out.println(String.format("[ EXCEPTION during shouldIncludeSample; approving upload of img '%s' ]", sample));
            return true;
        }
    }

    @Override
    protected boolean uploadSample(String imgFilepath) {
        return mImageUploader.uploadImage(imgFilepath);
    }
}
