package grash.ui;

import grash.core.GameController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class LevelActionScreenController extends ScreenController {
    private GameController game;

    public LevelActionScreenController(GameController gameController) {
        this.game = gameController;
    }

    @Override
    public void init() {
        System.out.println("Hello from the LevelActionScreenController");
        Canvas test = (Canvas) game.getPrimaryStage().getScene().lookup("#gameCanvas");

        GraphicsContext g = test.getGraphicsContext2D();

        LinearGradient gradient = new LinearGradient(
                0, 0.5, 1, 0.5,
                true,
                null,
                new Stop(0, javafx.scene.paint.Color.rgb(16, 16, 73)),
                new Stop(0.45, javafx.scene.paint.Color.rgb(15, 15, 30)),
                new Stop(0.55, javafx.scene.paint.Color.rgb(15, 15, 30)),
                new Stop(1, javafx.scene.paint.Color.rgb(16, 16, 73))
        );

        g.setFill(gradient);
        g.fillRect(0, 0, test.getWidth(), test.getHeight());
    }
}
