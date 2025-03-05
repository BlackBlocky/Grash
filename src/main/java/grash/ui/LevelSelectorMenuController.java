package grash.ui;

import grash.assets.MapMetadata;
import grash.core.GameController;
import grash.event.events.level.GrashEvent_LoadLevel;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Random;

public class LevelSelectorMenuController extends ScreenController {

    private final GameController game;

    public LevelSelectorMenuController(GameController gameController) {
        this.game = gameController;
    }

    @Override
    public void init() {
        setupBackground();
        generateMapButtons();
    }

    private void setupBackground() {
        Pane levelSelectorMenuPane = (Pane) game.getPrimaryStage().getScene().getRoot();
        final int STARS_COUNT = 225;

        Random random = new Random();
        Circle[] stars = new Circle[STARS_COUNT];
        for (int i = 0; i < stars.length; i++) {
            double posX = random.nextDouble() * (levelSelectorMenuPane.getWidth() + 100);
            double posY = random.nextDouble() * levelSelectorMenuPane.getHeight();
            double radius = random.nextDouble() * 10 + 3; // Size between 3 and 10
            Circle newStar = new Circle(posX, posY, radius, Color.WHITE);
            newStar.setOpacity(random.nextDouble() * 0.5 + 0.5); // Alpha between 0.3 and 1
            stars[i] = newStar;
        }

        AnimationTimer timer = new AnimationTimer() {
            private long lastTick = 0;
            private final double nanosecondsInSecond = 1000000000;
            private final double xSpeed = 100;
            private final double ySpeed = 250;
            private final double yWiggleStrength = 0.2;

            @Override
            public void handle(long l) {
                if(lastTick == 0) {
                    lastTick = l;
                    return;
                }

                double deltaTimeSeconds = (double)(l - lastTick) / nanosecondsInSecond;
                lastTick = l;

                double passedTimeSeconds = l / nanosecondsInSecond;

                // Update the stars
                for (Circle star : stars) {
                    // Flicker stars
                    double flickerStrength = random.nextDouble() * 0.05 - 0.025;
                    double newOpacity = Math.min(1, Math.max(0.5, star.getOpacity() + flickerStrength));
                    star.setOpacity(newOpacity);

                    // Move stars
                    star.setCenterX(star.getCenterX() - xSpeed * deltaTimeSeconds);
                    star.setCenterY(star.getCenterY() +
                            Math.sin(Math.toRadians(star.getCenterY() + passedTimeSeconds * ySpeed)) * yWiggleStrength);

                    // Move the Stars back
                    if(star.getCenterX() < -50) {
                        star.setCenterX(levelSelectorMenuPane.getWidth() + 50);
                        star.setCenterY(random.nextDouble() * levelSelectorMenuPane.getHeight());
                    }

                }

                // Draw Stars on
                WritableImage renderedStars = createStarImage(stars, levelSelectorMenuPane);

                // Update Background
                BackgroundImage backgroundImage = new BackgroundImage(
                        renderedStars,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(100, 100, true, true, true ,true)
                );
                levelSelectorMenuPane.setBackground(new Background(backgroundImage));
            }
        };
        timer.start();
    }

    private WritableImage createStarImage(Circle[] stars, Pane levelSelectorMenuPane) {
        Canvas renderingCanvas = new Canvas(levelSelectorMenuPane.getWidth(), levelSelectorMenuPane.getHeight());
        GraphicsContext g = renderingCanvas.getGraphicsContext2D();

        // Set Background
        g.setFill(Color.web("#0b0b19"));
        g.fillRect(0, 0, renderingCanvas.getWidth(), renderingCanvas.getHeight());

        // Render Stars
        g.setFill(Color.web("#751d8b"));
        for(Circle star : stars) {
            double posX = star.getCenterX();
            double posY = star.getCenterY();
            double alpha = star.getOpacity();
            double radius = star.getRadius();

            g.setGlobalAlpha(alpha);
            g.fillOval(posX - radius, posY - radius, 2 * radius, 2 * radius);
        }

        g.setGlobalAlpha(1.0); // Just making sure :P

        WritableImage result = new WritableImage((int)renderingCanvas.getWidth(), (int)renderingCanvas.getHeight());
        renderingCanvas.snapshot(null, result);
        return result;
    }

    private void generateMapButtons() {
        Pane levelSelectorMenuPane = (Pane) game.getPrimaryStage().getScene().getRoot();
        HBox levelsList = (HBox) levelSelectorMenuPane.lookup("#levelsList");

        // Clear all old Buttons
        levelsList.getChildren().clear();

        // Generate the new ones
        String[] allMapKeys = game.getResourceLoader().getAllMapKeys();
        for(String mapKey : allMapKeys) {
            MapMetadata currentMetadata = game.getResourceLoader().getMapMetadata(mapKey);
            VBox generatedElement = generateMapSelectionElement(currentMetadata);

            levelsList.getChildren().add(generatedElement);
        }
    }

    private VBox generateMapSelectionElement (MapMetadata mapMetadata) {
        VBox mapElementContainer = new VBox();
        mapElementContainer.setAlignment(Pos.CENTER);
        mapElementContainer.getStyleClass().add("map-element");

        Text headerText = new Text();
        headerText.setText(mapMetadata.getMapName().replace('_', ' '));
        headerText.getStyleClass().add("map-header");

        Text difficultyText = new Text();
        difficultyText.setText("Difficulty: " + mapMetadata.getMapDifficulty().replace('_', ' '));
        difficultyText.getStyleClass().add("map-info");

        Button playButton = new Button();
        playButton.setText("Start Level");
        playButton.setMinWidth(250);
        playButton.setMaxWidth(250);
        playButton.setPrefHeight(100);
        playButton.getStyleClass().add("map-start-button");
        playButton.setOnMousePressed(mouseEvent -> {
            game.getEventBus().triggerEvent(new GrashEvent_LoadLevel(mapMetadata.getMapKey()));
        });

        Text songInfoText = new Text();
        songInfoText.setText(mapMetadata.getSongName().replace('_', ' ') + " - " +
                mapMetadata.getSongAuthor().replace('_', ' '));
        songInfoText.getStyleClass().add("map-info");

        Text creatorInfo = new Text();
        creatorInfo.setText(mapMetadata.getMapAuthor().replace('_', ' '));
        creatorInfo.getStyleClass().add("map-info");

        mapElementContainer.getChildren().add(headerText);
        mapElementContainer.getChildren().add(difficultyText);
        mapElementContainer.getChildren().add(playButton);
        mapElementContainer.getChildren().add(songInfoText);
        mapElementContainer.getChildren().add(creatorInfo);

        return mapElementContainer;
    }

}
