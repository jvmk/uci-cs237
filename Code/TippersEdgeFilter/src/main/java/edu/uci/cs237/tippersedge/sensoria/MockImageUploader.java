package edu.uci.cs237.tippersedge.sensoria;

/**
 * Mock implementation of {@link ImageUploader} for use during development/until we are granted access to the TIPPERS
 * REST API.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class MockImageUploader implements ImageUploader {

    /**
     * <p><b>Side-effect free mock implementation that does nothing.</b></p>
     *
     * {@inheritDoc}
     */
    @Override
    public boolean uploadImage(String imgFilepath) {
        return true;
    }

}
