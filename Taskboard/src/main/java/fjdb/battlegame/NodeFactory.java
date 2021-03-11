package fjdb.battlegame;

import fjdb.battlegame.units.Player;
import fjdb.battlegame.units.Unit;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by Frankie Bostock on 23/09/2017.
 */
public class NodeFactory {

    public static abstract class Glyph extends Group {
        public abstract void setSelected(boolean selected);
    }

    public static Node addText() {
        Group g = new Group();
        DropShadow ds = new DropShadow();
        Text t = new Text();
        t.setEffect(ds);
        t.setCache(true);
        t.setX(20.0f);
        t.setY(70.0f);
        t.setFill(Color.RED);
        t.setText("JavaFX drop shadow effect");
        t.setFont(Font.font("null", FontWeight.BOLD, 32));
        g.getChildren().add(t);
        return g;
    }

    public static NodeFactory.Glyph playerGlyph(Player player, float radius) {
        return new PlayerGlyph(player, radius);
    }

    public static NodeFactory.Glyph enemyGlyph(float radius) {
        return new EnemyGlyph(radius);
    }

    public static void addGrid(GraphicsContext gc, int xInset, int yInset, int gridSize, int rows, int columns) {
        gc.setLineWidth(0.5);
        //for 10 rows we require 11 lines
        rows +=1;
        columns +=1;
        int squareX = gridSize;
        int squareY = gridSize;
        //draw horizontal lines
        int width = gridSize * (columns -1) + xInset;
        int height = gridSize * (rows -1) + yInset;
        for (int i = 0; i < rows; i++) {
            gc.strokeLine(xInset, yInset + i*squareY, width, yInset + i*squareY);
        }
        //draw vertical lines
        for (int i = 0; i < columns; i++) {
            gc.strokeLine(xInset + i*squareX, yInset, xInset + i*squareX, height);
        }

    }

    public static class EnemyGlyph extends Glyph {

        private final DropShadow ds1;

        public EnemyGlyph(float radius) {
            DropShadow ds = new DropShadow();
            ds.setOffsetY(3.0);
            ds.setOffsetX(3.0);
            ds.setColor(Color.GRAY);


            ds1 = new DropShadow();
            ds1.setOffsetY(4.0f);
            ds1.setOffsetX(4.0f);
            ds1.setColor(Color.CORAL);

            Rectangle box = new Rectangle();
            box.setEffect(ds1);
            box.setHeight(radius);
            box.setWidth(radius);
            box.setArcHeight(radius/2);
            box.setArcWidth(radius/2);

            box.setFill(Color.RED);
            box.setCache(true);

            getChildren().add(box);
        }

        @Override
        public void setSelected(boolean selected) {
            ds1.setColor(selected ? Color.MAGENTA : Color.CORAL);
        }
    }


    public static class PlayerGlyph extends Glyph {

        private final Player player;
        private final DropShadow ds1;

        public PlayerGlyph(Player player, float radius) {
            this.player = player;

            DropShadow ds = new DropShadow();
            ds.setOffsetY(3.0);
            ds.setOffsetX(3.0);
            ds.setColor(Color.GRAY);

            ds1 = new DropShadow();
            ds1.setOffsetY(4.0f);
            ds1.setOffsetX(4.0f);
            ds1.setColor(Color.DARKSLATEBLUE);

            Circle c = new Circle();
            c.setEffect(ds1);
            c.setCenterX(radius + 0.0f);
            c.setCenterY(radius + 0.0f);
            c.setRadius(radius);
            c.setFill(Color.BLUE);
            c.setCache(true);

            Circle top = new Circle();
            top.setEffect(ds1);
            top.setCenterX(radius + 0.0f);
            top.setCenterY(radius + 0.0f);
            top.setRadius(radius/2);
            top.setFill(Color.PURPLE);
            top.setCache(true);

            getChildren().add(c);
            getChildren().add(top);
        }

        public void setSelected(boolean selected) {
            ds1.setColor(selected ? Color.MAGENTA: Color.DARKSLATEBLUE);
        }

        public Unit getUnit() {
            return player;
        }
    }
}
