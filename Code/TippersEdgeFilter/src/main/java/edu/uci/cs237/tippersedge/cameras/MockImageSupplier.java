package edu.uci.cs237.tippersedge.cameras;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Mock implementation of {@link ImageSupplier} for use during development/testing.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 */
public class MockImageSupplier implements ImageSupplier {

    @Override
    public BufferedImage downloadImage(String webTargetUrl) {
        InputStream resourceInputStream = MockImageSupplier.class.getResourceAsStream("/img/eagle.jpg");
        try {
            BufferedImage img = ImageIO.read(resourceInputStream);
            resourceInputStream.close();
            return img;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean downloadAndStoreImage(String webTargetUrl, String fileName) throws IOException {
        return true;
    }

}
