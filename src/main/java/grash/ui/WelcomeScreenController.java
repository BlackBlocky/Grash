package grash.ui;

import grash.core.GameController;
import grash.core.WindowState;
import grash.event.GrashEvent;
import grash.event.events.level.GrashEvent_LoadLevel;
import grash.event.events.scene.GrashEvent_SwitchScene;
import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.util.Duration;

public final class WelcomeScreenController extends ScreenController {
    private GameController game;
    private AnimationTimer currentBackgroundAnimationTimer;

    public WelcomeScreenController (GameController gameController) {
        this.game = gameController;
        this.currentBackgroundAnimationTimer = null;
    }

    /**
     * Setups all the Stuff for the Scene like Animations
     */
    @Override
    public void init() {
        setupStartButton();
        setupBackground();
    }

    @Override
    public void close() {
        stopBackgroundAnimationIfPossible();
    }

    private void stopBackgroundAnimationIfPossible() {
        if(currentBackgroundAnimationTimer != null) {
            currentBackgroundAnimationTimer.stop();
            currentBackgroundAnimationTimer = null;
        }
    }

    /**
     * Adds the Animation to the Start Button if you hover with the mouse over it
     */
    private void setupStartButton() {
        stopBackgroundAnimationIfPossible();

        Button startButton = (Button) game.getPrimaryStage().getScene().lookup("#startButton");

        double animSpeed = 0.05;
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(startButton);
        scaleTransition.setCycleCount(1);
        scaleTransition.setDuration(Duration.seconds(animSpeed));

        startButton.setOnMouseEntered(event -> {
            scaleTransition.stop();

            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        });
        startButton.setOnMouseExited(event -> {
            scaleTransition.stop();

            scaleTransition.setFromX(1.1);
            scaleTransition.setFromY(1.1);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });

        startButton.setOnMousePressed(event -> startGameButton_Handler());
    }

    /**
     * Setups this cool animation in background, that the color is "spinning"
     */
    private void setupBackground() {
        Pane welcomeScreenPane = (Pane) game.getPrimaryStage().getScene().getRoot();

        AnimationTimer timer = new AnimationTimer() {
            private double angle = 0;
            private long lastTick = 0;
            private final double nanosecondsInSecond = 1000000000;
            private final double animSpeed = 600;


            @Override
            public void handle(long l) {
                if(lastTick == 0) {
                    lastTick = l;
                    return;
                }

                double deltaTimeSeconds = (double)(l - lastTick) / nanosecondsInSecond;
                lastTick = l;

                // Increase Angle
                angle = (angle + 1.0 * deltaTimeSeconds * animSpeed) % 360.0;

                // Calculate start and end Points by the angle
                double startX = 0.5 + 0.5 * Math.cos(Math.toRadians(angle));
                double startY = 0.5 + 0.5 * Math.sin(Math.toRadians(angle));
                double endX = 0.5 - 0.5 * Math.cos(Math.toRadians(angle));
                double endY = 0.5 - 0.5 * Math.sin(Math.toRadians(angle));

                LinearGradient gradient = new LinearGradient(
                        startX, startY, endX, endY,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#751d8b")),
                        new Stop(1, Color.web("#011655"))
                );

                // Update Background
                welcomeScreenPane.setBackground(new Background(new BackgroundFill(
                        Paint.valueOf(gradient.toString()),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )));
            }
        };
        timer.start();
    }

    public void startGameButton_Handler() {
        System.out.println("Start Game!");
        game.getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.LevelSelectorMenu));
    }
}
