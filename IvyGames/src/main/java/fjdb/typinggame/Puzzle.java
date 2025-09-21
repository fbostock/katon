package fjdb.typinggame;

import java.io.File;
import java.net.MalformedURLException;

public class Puzzle {

    private String name;

    public String getName() {
        return name;
    }

    public File getImage() {
        return image;
    }

    private File image;

    public Puzzle(String name, File image) {
        this.name = name;
        this.image = image;
    }

    public String loadFile() {
//        Image image = new Image(this.image.toURI().toURL().toExternalForm());
        try {
            return this.image.toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
