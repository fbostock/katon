package fjdb.typinggame;

import javafx.scene.image.ImageView;

import java.net.URL;

public class Images {

    public static final String IMAGE_PATH = "/fjdb/typinggame/";

    public static ImageView getGreenArrow() {
        URL resource = Images.class.getResource(IMAGE_PATH + "GreenArrow.png");
        return new ImageView(resource.toExternalForm());
    }
}
