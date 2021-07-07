package fjdb.images;

import java.awt.image.BufferedImage;
import java.io.File;

public class CandidateImage {
    private File inputFile;
    private BufferedImage tileImage;
    private PixelMatcher.ImageMaker imageMaker;

    public CandidateImage(File inputFile, BufferedImage tileImage, PixelMatcher.ImageMaker imageMaker) {
        this.inputFile = inputFile;
        this.tileImage = tileImage;
        this.imageMaker = imageMaker;
    }

    public BufferedImage getOriginal() {
        return imageMaker.makeImage(inputFile);
//            try {
//                return ImageIO.read(inputFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
    }


    public File getInputFile() {
        return inputFile;
    }

    public BufferedImage getTileImage() {
        return tileImage;
    }


    @Override
    public String toString() {
        return inputFile.toString();
    }
}
