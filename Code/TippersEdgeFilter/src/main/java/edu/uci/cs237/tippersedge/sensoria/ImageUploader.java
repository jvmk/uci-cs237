package edu.uci.cs237.tippersedge.sensoria;

/**
 * Interface for uploading an image.
 * Allows for dependency injection (see
 * <a href="https://stackoverflow.com/a/32007266/1214974">https://stackoverflow.com/a/32007266/1214974</a> for an
 * example of how to use the design pattern) which is useful (in fact a necessity) at this stage of development as we
 * do not yet have access to the TIPPERS REST API.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public interface ImageUploader {

    /**
     * Uploads an image.
     * @param imgFilepath The filepath to the image that is to be uploaded.
     * @return {@code true} if upload was successful, {@code false} otherwise.
     */
    boolean uploadImage(String imgFilepath);

}
