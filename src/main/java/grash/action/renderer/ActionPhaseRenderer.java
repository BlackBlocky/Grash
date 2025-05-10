package grash.action.renderer;

import grash.action.ActionPhaseController;
import grash.action.objects.*;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.core.Main;
import grash.editor.objects.EditorEffectIcon;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;

public final class ActionPhaseRenderer implements GrashEventListener {

    private static final int ropesPerSlide = 5;

    private static final double PIXEL_GRID_SIZE = 66;
    private GameController game;
    private Canvas gameCanvas;

    private Font mainFont;

    private RendererEffectData colorEffectData;
    private RendererEffectData rotationEffectData;
    private RendererEffectData fovScaleEffectData;

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
    public void setupRenderer(LevelMapEffect startColorEffect, LevelMapEffect startRotationEffect,
                              LevelMapEffect startFovScaleEffect, Canvas targetCanvas) {
        this.renderedBackgroundValue = 0.0;

        this.debug_renderGrid = false;
        this.debug_renderHitbox = false;

        this.mainFont =
                Font.loadFont(getClass().getResource("/fonts/VT323/VT323-Regular.ttf").toString(), 24);

        colorEffectData = new RendererEffectData(startColorEffect);
        rotationEffectData = new RendererEffectData(startRotationEffect);
        fovScaleEffectData = new RendererEffectData(startFovScaleEffect);

        gameCanvas = targetCanvas;
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
            case F12: {
                debug_renderGrid = !debug_renderGrid;
                break;
            }
            case F11: {
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
    public void updateRotations(LevelMapEffect currentRotationEffect, LevelMapEffect nextRotationEffect) {
        rotationEffectData.setCurrentEffect(currentRotationEffect);
        rotationEffectData.setNextEffect(nextRotationEffect);
        rotationEffectData.recalculate();
    }
    public void updateFovScales(LevelMapEffect currentFovScale, LevelMapEffect nextFovScale) {
        fovScaleEffectData.setCurrentEffect(currentFovScale);
        fovScaleEffectData.setNextEffect(nextFovScale);
        fovScaleEffectData.recalculate();
    }


    public void updateCanvas(double deltaTime, double secondsElapsedSinceStart,
                             List<ObstacleObject> renderedObstacleObjects,
                             List<NoteObject> renderedNoteObjects,
                             PlayerObject player) {

        GraphicsContext g = gameCanvas.getGraphicsContext2D();
        Color drawColor = colorEffectData.getCurrentEffect().getColor().interpolate(
                colorEffectData.getNextEffect().getColor(),
                colorEffectData.interpolateTime(secondsElapsedSinceStart));

        // Actual rendering
        drawBackgroundGradient(deltaTime, g, drawColor);

        // Apply Rotation and Stuff
        g.save(); // saving the state before the effects
        applyRendererEffects(g, secondsElapsedSinceStart);

        drawFloors(g, drawColor);
        drawAllObstacleObjects(g, renderedObstacleObjects);
        drawAllNoteObjects(g, renderedNoteObjects);

        if(player != null) drawSprite(g, player.getSprite(), player.getPosition(), Vec2.ONE());
        if(debug_renderGrid) drawGrid(g, drawColor);
        if(debug_renderHitbox) drawAllHitboxes(g, player, renderedObstacleObjects);

        g.restore(); // reverts all the effects
    }

    /**
     * This Method is only used by the Editor. It Visualises things such as bpm etc.
     */
    public void renderEditorOverdraw(double secondsElapsedSinceStart, double bpm, double mapSpeed, double playerX,
                                     ActionObject selectedObject, EditorEffectIcon selectedEffect,
                                     List<EditorEffectIcon> editorEffectIcons) {
        GraphicsContext g = gameCanvas.getGraphicsContext2D();
        Color drawColor = colorEffectData.getCurrentEffect().getColor().interpolate(
                colorEffectData.getNextEffect().getColor(),
                colorEffectData.interpolateTime(secondsElapsedSinceStart));

        // Apply Effects
        g.save();
        applyRendererEffects(g, secondsElapsedSinceStart);

        // Do the rendering
        drawBPM(g, secondsElapsedSinceStart, bpm, mapSpeed, drawColor, playerX);
        if(editorEffectIcons != null) drawEditorEffectIcons(g, editorEffectIcons);

        if(selectedObject != null) drawEditorSelectionOutline(g,
                selectedObject.getPosition().add(selectedObject.getDrawOffset()),
                selectedObject.getScale());
        if(selectedEffect != null) drawEditorSelectionOutline(g,
                selectedEffect.getPos(),
                new Vec2(1.0, 0.5));

        g.restore();
    }

    private void applyRendererEffects(GraphicsContext g, double secondsElapsedSinceStart) {
        g.translate(Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2);
        g.rotate(rotationEffectData.getValueBetweenBothEffectsByTime(secondsElapsedSinceStart));
        double scaleValue = fovScaleEffectData.getValueBetweenBothEffectsByTime(secondsElapsedSinceStart);
        g.scale(scaleValue, scaleValue);
        g.translate(-Main.SCREEN_WIDTH / 2, -Main.SCREEN_HEIGHT / 2);
    }

    private final double BPM_DRAW_BEFORE_OFFSET = 1.0, BPM_DRAW_AFTER_OFFSET = 4.0;
    private void drawBPM(GraphicsContext g, double secondsElapsedSinceStart, double bpm, double mapSpeed,
                         Color drawColor, double playerX) {
        double beatDurationSeconds = 1.0 / (bpm / 60.0);
        double timeLeftForCurrentBeat = secondsElapsedSinceStart % beatDurationSeconds;
        double beatsOffsetsSeconds = beatDurationSeconds - timeLeftForCurrentBeat;

        for(double beatTime = beatsOffsetsSeconds - BPM_DRAW_BEFORE_OFFSET; beatTime < BPM_DRAW_AFTER_OFFSET;
                beatTime += beatDurationSeconds) {
            double beatX = beatTime * mapSpeed + playerX;
            Vec2 beatScreenPos = calculateGridPixelsPos(new Vec2(beatX, ActionPhaseController.Y_MIDDLE));

            g.setStroke(drawColor.brighter());
            g.setLineWidth(2);
            g.setLineDashes();
            g.setLineDashOffset(0);
            g.strokeLine(beatScreenPos.x, 0, beatScreenPos.x, Main.SCREEN_HEIGHT);

            double betweenLineOffset = beatDurationSeconds / 2.0 * mapSpeed * PIXEL_GRID_SIZE;
            g.setLineDashes(5, 20);
            g.strokeLine(beatScreenPos.x + betweenLineOffset, 0,
                        beatScreenPos.x + betweenLineOffset, Main.SCREEN_HEIGHT);
        }
    }

    private void drawEditorSelectionOutline(GraphicsContext g, Vec2 pos, Vec2 scale) {
        Vec2 pixelDrawPos = calculateGridPixelsPos(pos);
        Vec2 objectSizePixels = calculateGridPixelsPos(scale);

        double colorBlinkTime = ((double)System.currentTimeMillis() % 1000.0) / 1000.0;
        g.setStroke(Color.hsb(colorBlinkTime * 360, 1, 1));
        g.setLineWidth(3);
        g.setLineDashes(10, 8);
        g.setLineDashOffset(((double)System.currentTimeMillis() * 0.05) % 18.0);
        g.strokeRect(pixelDrawPos.x, pixelDrawPos.y, objectSizePixels.x, objectSizePixels.y);
    }

    private void drawEditorEffectIcons(GraphicsContext g, List<EditorEffectIcon> editorEffectIcons) {
        for(EditorEffectIcon item : editorEffectIcons) {
            // Draw line
            Vec2 pixelPos = calculateGridPixelsPos(item.getPos());
            double animationValue = ((double)System.currentTimeMillis() * 0.5 % 1000) / 1000;

            g.setStroke(Color.BLACK);
            g.setLineWidth(2);
            g.setLineDashes(15, 5);
            g.setLineDashOffset(-20.0 * animationValue);
            g.strokeLine(pixelPos.x, pixelPos.y, pixelPos.x, ActionPhaseController.Y_UP * PIXEL_GRID_SIZE);

            // Draw Sprite
            drawSprite(g, item.getSprite(), item.getPos(), new Vec2(1.0, 0.5));
        }
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

    /**
     * This Method will draw a Text on the GameCanvas.
     * @param drawCentered if it is true, the text will be drawn centered at gridPos, otherwise it will we be
     *                     drawn starting at the top left.
     */
    private void drawText(GraphicsContext g, String textValue, Font font, double fontSize, Color color,
                          Vec2 gridPos, boolean drawCentered) {
        Font drawingFont = new Font(font.getName(), fontSize);
        Vec2 drawPosPixels = calculateGridPixelsPos(gridPos);

        // Calculate the centered offset
        Text text = new Text(textValue);
        text.setFont(drawingFont);
        // times 0.6 cause the text is too high normally, I guess
        double textHeightPixels = text.getBoundsInLocal().getHeight() * 0.6;
        drawPosPixels.y += (drawCentered) ? textHeightPixels / 2.0 : textHeightPixels;

        g.setFill(color);
        g.setGlobalAlpha(1.0);

        g.setFont(drawingFont);
        g.setTextAlign((drawCentered) ? TextAlignment.CENTER : TextAlignment.LEFT);
        g.fillText(textValue, drawPosPixels.x, drawPosPixels.y);
    }

    private void drawAllNoteObjects(GraphicsContext g, List<NoteObject> allNoteObjects) {
        for(NoteObject noteObject : allNoteObjects) {
            switch (noteObject.getLevelMapNote().getMapNoteType()) {
                case TapNote:
                    drawSprite(g, noteObject.getSprite(), // Adding a 0.5 so that you hit it in the middle
                            noteObject.getPosition().add(new Vec2(-0.5, 0)), noteObject.getScale());
                    // Draw the Score if the scoreText isn't empty. (Aka the Tap anim)
                    if(!noteObject.getScoreText().isEmpty()) {
                        drawText(g, noteObject.getScoreText(), mainFont, 50,
                                ActionPhaseController.NOTE_HIT_TO_COLOR_MAP.get(noteObject.getTappedNoteHitAccuracy()),
                                noteObject.getPosition().add(new Vec2(0.5, 0)), // Moving text in the middle
                                true);
                    }
//                    drawText(g, "HelloWorld", mainFont, 50, Color.RED, Vec2.ONE().multiply(6),  false);
                    break;
                case GrowNote:
                    break;
                case SlideNote:
                    break;
            }
        }
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
