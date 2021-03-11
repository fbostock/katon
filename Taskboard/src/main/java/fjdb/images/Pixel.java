package fjdb.images;

/**
 * A pixel wraps a PixelImage, and contains analytical information about that PixelImage,
 * such as color variation across the pixel, brightness variation etc.
 * Created by francisbostock on 31/05/2016.
 */
public class Pixel {

    private PixelImage image;

    public Pixel(PixelImage image) {
        this.image = image;
    }


}
