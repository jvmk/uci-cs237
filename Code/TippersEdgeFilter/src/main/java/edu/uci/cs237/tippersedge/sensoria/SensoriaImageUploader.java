package edu.uci.cs237.tippersedge.sensoria;

/**
 * Concrete implementation of {@link ImageUploader} that uploads images to Sensoria using the TIPPERS REST API.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class SensoriaImageUploader implements ImageUploader {

    @Override
    public boolean uploadImage(String imgFilepath) {
        throw new UnsupportedOperationException("Not yet implemented as we haven't been granted access to the TIPPERS REST API.");
    }

}
