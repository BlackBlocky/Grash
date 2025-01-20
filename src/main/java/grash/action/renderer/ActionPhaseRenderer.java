package grash.action.renderer;

import grash.action.objects.Hitbox;
import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.MapElementType;
import grash.math.Vec2;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.List;

public final class ActionPhaseRenderer {

    private static final double PIXEL_GRID_SIZE = 66;
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

    public void updateCanvas(double deltaTime, double secondsElapsedSinceStart,
                             List<ObstacleObject> renderedObstacleObjects,
                             PlayerObject player) {

        GraphicsContext g = gameCanvas.getGraphicsContext2D();
        Color drawColor = colorEffectData.getCurrentEffect().getColor().interpolate(
                colorEffectData.getNextEffect().getColor(),
                colorEffectData.interpolateTime(secondsElapsedSinceStart));

        drawBackgroundGradient(deltaTime, g, drawColor);
        drawFloors(g, drawColor);
        drawAllObstacleObjects(g, renderedObstacleObjects);

        if(player != null) drawSprite(g, player.getSprite(), player.getPosition(), Vec2.ONE());
        //drawGrid(g, drawColor);
        drawAllHitboxes(g, player, renderedObstacleObjects);
    }

    /**
     * Converts simple positions like (2.2,3.5) into screen-space pixel positions, based
     * on the Grid, e.g. (220,350)
     * In other words, returning the top left of the target Grid.
     */
    private Vec2 calculateGridPixelsPos(Vec2 pos) {
        return pos.multiply(PIXEL_GRID_SIZE);
    }

    private void drawAllObstacleObjects(GraphicsContext g, List<ObstacleObject> allObstacleObjects) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            drawSprite(g, obstacleObject.getSprite(), obstacleObject.getPosition().add(obstacleObject.getDrawOffset()), obstacleObject.getScale());
        }
    }

    private void drawAllHitboxes(GraphicsContext g, PlayerObject player, List<ObstacleObject> allObstacleObjects) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            drawHitbox(g, obstacleObject.getPosition(), obstacleObject.getHitbox());
        }

        if(player != null) drawHitbox(g, player.getPosition(), player.getHitbox());
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

    private void drawSprite(GraphicsContext g, Sprite sprite, Vec2 gridPos, Vec2 scale) {
        Vec2 pixelPos = calculateGridPixelsPos(gridPos);


        if(sprite == null) {
            g.setFill(Color.HOTPINK);
            g.fillRect(pixelPos.x, pixelPos.y, PIXEL_GRID_SIZE * scale.x, PIXEL_GRID_SIZE * scale.y);
            return;
        }

        g.drawImage(sprite.getImage(), pixelPos.x, pixelPos.y,
                PIXEL_GRID_SIZE * scale.x, PIXEL_GRID_SIZE * scale.y);
    }

    private void drawHitbox(GraphicsContext g, Vec2 gridPos, Hitbox hitbox) {
        Vec2 pixelPos = calculateGridPixelsPos(gridPos.add(hitbox.getOffset()));

        g.setStroke(Color.LIME);
        g.setLineWidth(2);
        g.strokeRect(pixelPos.x, pixelPos.y,
                PIXEL_GRID_SIZE * hitbox.getSize().x, PIXEL_GRID_SIZE * hitbox.getSize().y);
    }

    private void drawFloors(GraphicsContext g, Color drawColor) {
        // Draw the first two lines
        g.setFill(drawColor.invert());

        Sprite floorUpSprite = game.getResourceLoader().getSprite("FloorUp");
        for(int x = 0; x < gameCanvas.getWidth() / PIXEL_GRID_SIZE; x++) {
            drawSprite(g, floorUpSprite, new Vec2(x, 3), Vec2.ONE());
        }

        Sprite floorDownSprite = game.getResourceLoader().getSprite("FloorDown");
        for(int x = 0; x < gameCanvas.getWidth() / PIXEL_GRID_SIZE; x++) {
            drawSprite(g, floorDownSprite, new Vec2(x, 9), Vec2.ONE());
        }
        /*
        drawSprite(g, game.getResourceLoader().getSprite("SpikeDown"), new Vec2(8, 8));
        drawSprite(g, game.getResourceLoader().getSprite("SpikeDown"), new Vec2(10, 8));
        drawSprite(g, game.getResourceLoader().getSprite("SpikeDown"), new Vec2(11, 8));

        drawSprite(g, game.getResourceLoader().getSprite("SpikeUp"), new Vec2(9, 4));
        drawSprite(g, game.getResourceLoader().getSprite("SpikeUp"), new Vec2(11, 4));
        drawSprite(g, game.getResourceLoader().getSprite("SpikeUp"), new Vec2(12, 4));*/
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
