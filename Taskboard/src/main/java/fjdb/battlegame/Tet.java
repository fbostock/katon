package fjdb.battlegame;

import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Tet extends Application {

    @Override
    public void start(Stage primaryStage)
    {
        LinearGradient lg = new LinearGradient(0.0, 0.0, 0.0, 1.0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.BLUE),
                new Stop(1.0, Color.LIGHTSKYBLUE));
        Group root = new Group();
        Scene scene = new Scene(root, lg);

        Rectangle r = new Rectangle();
        r.setWidth(200);
        r.setHeight(80);
        r.setArcWidth(10);
        r.setArcHeight(10);
        r.translateXProperty().bind(scene.widthProperty().
                subtract(r.getLayoutBounds().getWidth()).
                divide(2));
        r.translateYProperty().bind(scene.heightProperty().
                subtract(r.getLayoutBounds().getHeight()).
                divide(2));
        r.setFill(Color.LIGHTYELLOW);

        root.getChildren().add(r);

        Text text = new Text("TranslateTransition Demo");
        text.setFont(new Font("Times New Roman BOLD", 22));
        text.setTextOrigin(VPos.TOP);
        text.setFill(Color.MEDIUMBLUE);
        text.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.GRAY, 1, 0.25, 2,
                2));
        root.getChildren().add(text);

        TranslateTransition tt = new TranslateTransition();
        tt.setNode(text);

        tt.fromXProperty().bind(r.translateXProperty().
                add(r.getLayoutBounds().getWidth() + 20));
        tt.fromYProperty().bind(r.translateYProperty().
                add((r.getLayoutBounds().getHeight() -
                        text.getLayoutBounds().getHeight()) / 2));

        tt.toXProperty().bind(r.translateXProperty().
                subtract(text.getLayoutBounds().getWidth() + 20));
        tt.toYProperty().bind(tt.fromYProperty());
        tt.setDuration(new Duration(5000));
        tt.setInterpolator(Interpolator.LINEAR);
        tt.setAutoReverse(true);
        tt.setCycleCount(Timeline.INDEFINITE);

        scene.widthProperty().addListener((observable, oldValue, newValue) ->
        {
            tt.playFromStart();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) ->
        {
            tt.playFromStart();
        });

        Rectangle rClip = new Rectangle();
        rClip.setWidth(200);
        rClip.setHeight(80);
        rClip.setArcWidth(10);
        rClip.setArcHeight(10);
        rClip.translateXProperty().bind(r.translateXProperty());
        rClip.translateYProperty().bind(r.translateYProperty());
        root.setClip(rClip);

        primaryStage.setTitle("TranslateTransition Demo");
        primaryStage.setScene(scene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        primaryStage.show();
    }
}
