package grash.action;

import grash.core.GameController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public final class ActionPhaseRenderer {

    private GameController game;
    private Canvas gameCanvas;

    private Color currentColor;
    private Color renderedColor;
    private Color nextColor;

    private double renderedBackgroundValue;

    /**
     * This will be called every Time the Renderer starts, aka another level is starting
     */
    public void setupRenderer(GameController gameController, Color startColor) {
        this.game = gameController;

        this.renderedBackgroundValue = 0.0;

        this.currentColor = startColor;
        this.renderedColor = startColor;
        this.nextColor = startColor;

        gameCanvas = (Canvas) game.getPrimaryStage().getScene().lookup("#gameCanvas");
    }

    public void updateCanvas(double deltaTime) {
        GraphicsContext g = gameCanvas.getGraphicsContext2D();

        drawBackgroundGradient(deltaTime, g);
    }

    public void drawBackgroundGradient(double deltaTime, GraphicsContext g) {
        double BACKGROUND_ANIMATION_SPEED = 2.5;
        renderedBackgroundValue += deltaTime * BACKGROUND_ANIMATION_SPEED;

        // Some reference: https://de.wikipedia.org/wiki/Sinus_und_Kosinus
        double rotationX = Math.cos(renderedBackgroundValue);
        double rotationY = Math.sin(renderedBackgroundValue) * 3; // times 3 for making it look like a little cooler :P

        // The 0.5 offset is for moving it to the center
        LinearGradient gradient = new LinearGradient(
                rotationX + 0.5, rotationY + 0.5, -rotationX + 0.5, -rotationY + 0.5,
                true,
                null,
                new Stop(0, renderedColor),
                new Stop(1, renderedColor.deriveColor(0, 1, 0.4, 1))
        );

        g.setFill(gradient);
        g.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

}
