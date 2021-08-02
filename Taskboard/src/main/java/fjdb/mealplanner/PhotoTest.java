package fjdb.mealplanner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoTest {

    public static void main(String[] args) throws IOException {

        String currentUsersHomeDir = System.getProperty("user.home");
        File mealPlansFolder = new File(currentUsersHomeDir, "MealPlans");
        File photo = new File(mealPlansFolder, "Original.jpg");
//4256, 2832
        BufferedImage read = ImageIO.read(photo);
        int difference = 1424;
        int factor = difference / 2+400;

        BufferedImage subimage = read.getSubimage(factor, 0, 2832, 2832);
        boolean jpg = ImageIO.write(subimage, "jpg", new File(mealPlansFolder, "image4.jpg"));

    }
}
