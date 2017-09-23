package fjdb.graphics;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by Frankie Bostock on 23/09/2017.
 */
public class NodeFactory {

    public static Node dropShadow(float radius) {
        Group g = new Group();

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.GRAY);

//        Text t = new Text();
//        t.setEffect(ds);
//        t.setCache(true);
//        t.setX(20.0f);
//        t.setY(70.0f);
//        t.setFill(Color.RED);
//        t.setText("JavaFX drop shadow effect");
//        t.setFont(Font.font("null", FontWeight.BOLD, 32));
//        g.getChildren().add(t);

        DropShadow ds1 = new DropShadow();
        ds1.setOffsetY(4.0f);
        ds1.setOffsetX(4.0f);
        ds1.setColor(Color.CORAL);

        Circle c = new Circle();
        c.setEffect(ds1);
        c.setCenterX(radius + 0.0f);
        c.setCenterY(radius + 0.0f);
        c.setRadius(radius);
        c.setFill(Color.RED);
        c.setCache(true);

        g.getChildren().add(c);
        return g;
    }
}
