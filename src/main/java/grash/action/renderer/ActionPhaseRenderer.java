package grash.action.renderer;

import grash.core.GameController;
import grash.level.map.LevelMapEffect;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public final class ActionPhaseRenderer {

    private static final double PIXEL_GRID_SIZE = 60;
    private GameController game;
    private Canvas gameCanvas;

    private RendererEffectData colorEffectData;

    private double renderedBackgroundValue; // used in drawBackgroundGradient() ONLY

    /**
     * This will be called every Time the Renderer starts, aka another level is starting
     */
    public void setupRenderer(GameController gameController, LevelMapEffect startColorEffect) {
        this.game = gameController;

        this.renderedBackgroundValue = 0.0;

        colorEffectData = new RendererEffectData(startColorEffect);

        gameCanvas = (Canvas) game.getPrimaryStage().getScene().lookup("#gameCanvas");
    }

    public void updateColors(LevelMapEffect currentColorEffect, LevelMapEffect nextColorEffect) {
        colorEffectData.setCurrentEffect(currentColorEffect);
        colorEffectData.setNextEffect(nextColorEffect);
        colorEffectData.recalculate();
    }

    public void updateCanvas(double deltaTime, double secondsElapsedSinceStart) {
        GraphicsContext g = gameCanvas.getGraphicsContext2D();
        Color drawColor = colorEffectData.getCurrentEffect().getColor().interpolate(
                colorEffectData.getNextEffect().getColor(),
                colorEffectData.interpolateTime(secondsElapsedSinceStart));

        drawBackgroundGradient(deltaTime, g, drawColor);

        drawGrid(g, drawColor);
    }

    /**
     * Converts simple positions like [2.2,3.5] into screen-space pixel positions, based
     * on the Grid, e.g. [220/350]
     */
    private void getGridPixelsPos(double x, double y) {

    }

    private void drawBackgroundGradient(double deltaTime, GraphicsContext g, Color drawColor) {
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
                new Stop(0, drawColor),
                new Stop(1, drawColor.deriveColor(0, 1, 0.4, 1))
        );

        g.setFill(gradient);
        g.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    private void drawFloors(GraphicsContext g, Color drawColor) {

    }

    private void drawGrid(GraphicsContext g, Color drawColor) {
        for(int x = 0; x < gameCanvas.getWidth(); x += (int)PIXEL_GRID_SIZE) {
            for(int y = 0; y < gameCanvas.getHeight(); y += (int)PIXEL_GRID_SIZE) {
                boolean isEvenX = (x / PIXEL_GRID_SIZE) % 2 == 0;
                boolean isEvenY = (y / PIXEL_GRID_SIZE) % 2 == 0;

                Color c = (isEvenX == isEvenY)
                        ? Color.grayRgb(255, 0.2)
                        : Color.grayRgb(0, 0.2);

                g.setFill(c);
                g.fillRect(x, y, PIXEL_GRID_SIZE, PIXEL_GRID_SIZE);
            }
        }
    }



}
