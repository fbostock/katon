package fjdb.images;

import java.awt.*;

/**
 * A PixelImage would contain the raw information of a particular piece of a main image.
 * For example if a MainImage is a 60X80 pixel image, and we break it down in to 10x10
 * pieces, then each PixelImage would represent the 10x10 pixels from the MainImage
 *
 * Created by francisbostock on 31/05/2016.
 */
public class PixelImage {

    private int width;
    private int height;
    //TODO is this how it would be represented, as Colors (RGBs indices)?
    private Grid<Color> colorGrid;

}
