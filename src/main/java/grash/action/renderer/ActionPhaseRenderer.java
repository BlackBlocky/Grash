package grash.action.renderer;

import grash.action.ActionPhaseController;
import grash.action.objects.Hitbox;
import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.level.map.LevelMapEffect;
import grash.math.Vec2;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.List;

public final class ActionPhaseRenderer implements GrashEventListener {

    private static final int ropesPerSlide = 5;

    private static final double PIXEL_GRID_SIZE = 66;
    private GameController game;
    private Canvas gameCanvas;

    private RendererEffectData colorEffectData;
    private RendererEffectData rotationEffectData;

    private double renderedBackgroundValue; // used in drawBackgroundGradient() ONLY

    private boolean debug_renderGrid;
    private boolean debug_renderHitbox;

    public ActionPhaseRenderer(GameController gameController) {
        this.game = gameController;
        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
    }

    /**
     * This will be called every Time the Renderer starts, aka another level is starting
     */
    public void setupRenderer(LevelMapEffect startColorEffect, LevelMapEffect startRotationEffect) {
        this.renderedBackgroundValue = 0.0;

        this.debug_renderGrid = false;
        this.debug_renderHitbox = false;

        colorEffectData = new RendererEffectData(startColorEffect);
        rotationEffectData = new RendererEffectData(startRotationEffect);

        gameCanvas = (Canvas) game.getPrimaryStage().getScene().lookup("#gameCanvas");
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
        }
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        switch (event.getKeyCode()) {
            case DIGIT0: {
                debug_renderGrid = !debug_renderGrid;
                break;
            }
            case DIGIT9: {
                debug_renderHitbox = !debug_renderHitbox;
                break;
            }
        }
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
        if(debug_renderGrid) drawGrid(g, drawColor);
        if(debug_renderHitbox) drawAllHitboxes(g, player, renderedObstacleObjects);
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
           switch (obstacleObject.getLevelMapElement().getMapElementType()) {
               case Spike:
               case Wall:
               case DoubleJump:
                   drawSprite(g, obstacleObject.getSprite(), obstacleObject.getPosition().add(obstacleObject.getDrawOffset()), obstacleObject.getScale());
                   break;
               case Rope:
                   drawRope(g, obstacleObject);
                   break;
               case Slide:
                   drawSlide(g, obstacleObject);
                   break;
           }
        }
    }

    private void drawRope(GraphicsContext g, ObstacleObject ropeObject) {
        double ropeSizePixels = PIXEL_GRID_SIZE / 6;
        double ropeDrawOffset = (PIXEL_GRID_SIZE / 2) - (ropeSizePixels / 2);

        // Reminder: It's the Position on the TOP LEFT of the grid
        Vec2 ropeStartPosPixels = calculateGridPixelsPos(ropeObject.getPosition());
        Vec2 ropeEndPosPixels = calculateGridPixelsPos(ropeObject.getAdditionalPositions()[0]); // 0 = EndPos

        Vec2 lineOffset = new Vec2(ropeSizePixels / 2, ropeSizePixels / 2);
        ropeStartPosPixels = ropeStartPosPixels.add(ropeDrawOffset).add(lineOffset);
        ropeEndPosPixels = ropeEndPosPixels.add(ropeDrawOffset).add(lineOffset);
        ropeEndPosPixels.x -= ropeSizePixels;

        g.setStroke(Color.YELLOW);
        g.setLineWidth(ropeSizePixels);

        /* Adding the ropeSizePixels / 2 offset, because the line is not just 6 pixels tall,
        it also goes into the length: See in the lineOffset Vec2 */
        g.strokeLine(ropeStartPosPixels.x + ropeSizePixels / 2, ropeStartPosPixels.y,
                  ropeEndPosPixels.x + ropeSizePixels / 2, ropeEndPosPixels.y);

        // Add a glow, I guess
        g.setGlobalAlpha(0.2);
        g.setLineWidth(ropeSizePixels + 5);
        g.strokeLine(ropeStartPosPixels.x + ropeSizePixels / 2, ropeStartPosPixels.y,
                ropeEndPosPixels.x + ropeSizePixels / 2, ropeEndPosPixels.y);
        g.setGlobalAlpha(1.0);

        // Draw the Sprites over the line/rope
        drawSprite(g, ropeObject.getSprite(), ropeObject.getPosition(), Vec2.ONE());
        drawSprite(g, ropeObject.getAdditionalSprites()[0], ropeObject.getAdditionalPositions()[0], Vec2.ONE());
    }

    private void drawSlide(GraphicsContext g, ObstacleObject slideObject) {
        // Draw base-line
        Vec2 slideStartPixels = calculateGridPixelsPos(slideObject.getPosition());
        Vec2 slideEndPixels = calculateGridPixelsPos(slideObject.getAdditionalPositions()[0].add(new Vec2(0, 1)));
        final double baselineStartOffsetPixels = PIXEL_GRID_SIZE / 4.4; // Making it a bit larger so it overlaps a bit
        g.setFill(Color.WHITE);
        g.setStroke(Color.WHITE);
        g.setLineWidth(3.0);
        g.strokeRect(slideStartPixels.x + baselineStartOffsetPixels, slideStartPixels.y + 1.5, //xy
                (slideEndPixels.x - slideStartPixels.x) - (baselineStartOffsetPixels * 2), // w
                (slideEndPixels.y - slideStartPixels.y) - 3); // h

        // Draw "holder" Lines
        Vec2 slideStartPos = slideObject.getPosition();
        Vec2 slideEndPos = slideObject.getAdditionalPositions()[0];
        double holderLinesY = (slideObject.getLevelMapElement().getIsUp()) ? slideStartPos.y + 1.0 : slideStartPos.y;
        double slideStartOffset = 0.5;
        double holderLineStepSize = (slideEndPos.x - slideStartPos.x) / (ropesPerSlide - 1) -
                (slideStartOffset / (ropesPerSlide - 1)); // length / 4 (making it parts)
                // (somehow it makes one more rope as intended, but it works so don't touch it lol :P)
        for(double lineX = slideStartPos.x + slideStartOffset / 2; lineX < slideEndPos.x; lineX += holderLineStepSize) {
            Vec2 holderLineStartPixels = calculateGridPixelsPos(new Vec2(lineX, holderLinesY));
            double lineEndY = (slideObject.getLevelMapElement().getIsUp()) ?
                    ActionPhaseController.Y_DOWN + 1 : ActionPhaseController.Y_UP;
            Vec2 holderLineEndPixels = calculateGridPixelsPos(new Vec2(lineX, lineEndY));

            g.strokeLine(holderLineStartPixels.x, holderLineStartPixels.y,
                    holderLineEndPixels.x, holderLineEndPixels.y);
        }

        // Draw Sprites
        drawSprite(g, slideObject.getSprite(), slideObject.getPosition(), Vec2.ONE()); // StartPos, SlideLeftSprite
        drawSprite(g, slideObject.getAdditionalSprites()[0],
                slideObject.getAdditionalPositions()[0].add(new Vec2(-1, 0)), Vec2.ONE()); // EndPos, SlideRightSprite
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
        if(hitbox == null) return;

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
