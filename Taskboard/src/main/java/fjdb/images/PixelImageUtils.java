package fjdb.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PixelImageUtils {

    /**
     * Take an input image, and crop it to an integer multiple of the tile sizes in the X and Y direction. The returned image
     * will be centred relative to the input image.<p>
     * e.g. an input image file of size 250 by 380 and tile sizes 100 will produce an image cropped to 200 by 300, starting from {x,y} = {25, 40}
     */
    public static BufferedImage crop(int tileSizeX, int tileSizeY, File file) {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth();
            int height = read.getHeight();
            int xResidual = width % tileSizeX;
            int yResidual = height % tileSizeY;

            BufferedImage subimage = read.getSubimage(xResidual / 2, yResidual / 2, width - xResidual, height - yResidual);
            return subimage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Crop the image so that each dimension is the same integer multiple of the given tile sizes.
     * @return
     */
    public static BufferedImage cropFixRatio(int tileSizeX, int tileSizeY, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int widthFactor = width / tileSizeX;
        int heightFactor = height / tileSizeY;
        int factor = Math.min(widthFactor, heightFactor);
        int xResidual = width - factor * tileSizeX;
        int yResidual = height - factor * tileSizeY;

        return image.getSubimage(xResidual / 2, yResidual / 2, width - xResidual, height - yResidual);
    }


    /**
     * Crop image from file to a square, with dimension the maximum multiple of the tilesize possible.
     * e.g. a 250 by 200 pixel image with a tile size of 20 would resort in a 200 by 200 image.
     *
     * @return
     */
    public static BufferedImage cropSquare(int tileSize, File file) {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth();
            int height = read.getHeight();
            int xFactor = width / tileSize;
            int yFactor = height / tileSize;

            int xResidual;
            int yResidual;

            if (xFactor > yFactor) {
                yResidual = height % tileSize;
                xResidual = width - yFactor * tileSize;
            } else {
                xResidual = width % tileSize;
                yResidual = height - xFactor * tileSize;
            }


            BufferedImage subimage = read.getSubimage(xResidual / 2, yResidual / 2, width - xResidual, height - yResidual);
            return subimage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Changes the pixel dimension of the input images to the given dimension.
     */
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

}
