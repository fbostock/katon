package fjdb.images;

import com.google.common.collect.Lists;
import fjdb.threading.LazyConcurrentField;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Created by francisbostock on 31/05/2016.
 */
public class MainImage {

    private final File file;
    private final int tileSizeX;
    private final int tileSizeY;
    private final BufferedImage bufferedImage;
    private Grid<Tile> tileGrid;
    private List<Tile> tiles;


    public MainImage(File file, int tileSizeX, int tileSizeY) {
        this.file = file;
        this.tileSizeX = tileSizeX;
        this.tileSizeY = tileSizeY;
        bufferedImage = PixelImageUtils.crop(tileSizeX, tileSizeY, file);
        getTiles();
    }

    private void createImageGrid() {
        //TODO
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public synchronized Grid<Tile> getTileGrid() {
        getTiles();//ensure tiles have been populated.
        return tileGrid;
    }

    public synchronized List<Tile> getTiles() {
        if (tiles == null || tileGrid == null) {
            tiles = Lists.newArrayList();

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            int widthIter = width / tileSizeX;
            int heightIter = height / tileSizeY;

            tileGrid = new Grid<>(widthIter, heightIter);

//        Map<Integer, Map<Integer, BufferedImage>>
            BufferedImage[][] tilePieces = new BufferedImage[widthIter][heightIter];
            for (int i = 0; i < widthIter; i++) {
                for (int j = 0; j < heightIter; j++) {
                    BufferedImage subimage = bufferedImage.getSubimage(i * tileSizeX, j * tileSizeY, tileSizeX, tileSizeY);
                    tilePieces[i][j] = subimage;
                    Tile tile = new Tile(subimage, i, j);
                    tiles.add(tile);
                    tileGrid.put(i, j, tile);
                }
            }
        }
        return tiles;
    }

    public int getTilesAcross() {
        return tileGrid.getWidth();
    }

    public int getTilesHigh() {
        return tileGrid.getHeight();
    }


    public static class Tile {
        private final BufferedImage bufferedImage;
        private final BufferedImage resized;
        private int xImageIndex;
        private int yImageIndex;

        public Tile(BufferedImage bufferedImage, int xImageIndex, int yImageIndex) {
            this.bufferedImage = bufferedImage;
            //TODO where to specify width/height of 10?
            resized = PixelImageUtils.resize(bufferedImage, 10, 10);
            this.xImageIndex = xImageIndex;
            this.yImageIndex = yImageIndex;
        }

        public int getxImageIndex() {
            return xImageIndex;
        }

        public int getyImageIndex() {
            return yImageIndex;
        }

        public int tileSize() {
//            return bufferedImage.getWidth();
            return resized.getWidth();
        }

        LazyConcurrentField<List<Integer>> tileColors = new LazyConcurrentField<List<Integer>>() {
            @Override
            public List<Integer> fetch() {
                return getColors(resized);
            }
        };

        public List<Integer> getColors() {
//TODO replace with tileColors.get();
            return tileColors.get();
//            List<Integer> colorVector = Lists.newArrayList();
//            for (int i = 0; i < 10; i++) {
//                for (int j = 0; j < 10; j++) {
//                    Color color = new Color(resized.getRGB(i, j));
//                    int red = color.getRed();
//                    int green = color.getGreen();
//                    int blue = color.getBlue();
//                    //TODO put the colors into a vector
//                    //TODO alternatively, we could simply try the int value stored in Color, and see how that performs
//                    //in the pca? Though I don't think it would work well.
//                    colorVector.add(red);
//                    colorVector.add(green);
//                    colorVector.add(blue);
//                }
//            }
//            //            resized.getRaster().getda
//            return colorVector;
        }

        public static List<Integer> getColors(BufferedImage image) {
            List<Integer> colorVector = Lists.newArrayList();
            for (int i = 0; i < image.getTileWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    Color color = new Color(image.getRGB(i, j));
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    //TODO put the colors into a vector
                    //TODO alternatively, we could simply try the int value stored in Color, and see how that performs
                    //in the pca? Though I don't think it would work well.
                    colorVector.add(red);
                    colorVector.add(green);
                    colorVector.add(blue);
                }
            }
            return colorVector;
        }
    }

}
