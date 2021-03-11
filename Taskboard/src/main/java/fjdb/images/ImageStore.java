package fjdb.images;

import java.util.Collection;

/**
 * A collection of PixelImages to be used for matching to a MainImage.
 * TODO input images should be categorised to allow specific selections of particular
 * image types. But first need to work out what those categories will be. e.g. those mostly of
 * one particular color for instance.
 * Created by francisbostock on 31/05/2016.
 */
public class ImageStore {

    private Collection<PixelImage> pixelImages;

    public ImageStore(Collection<PixelImage> pixelImages) {
        this.pixelImages = pixelImages;
    }


}
