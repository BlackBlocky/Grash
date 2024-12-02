package grash.ui;

import grash.core.GameController;
import grash.events.*;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;

public final class WelcomeScreenController extends ScreenController {
    private GameController game;

    public WelcomeScreenController (GameController gameController) {
        this.game = gameController;
    }

    @Override
    public void init() {
        setupStartButton();
    }

    private void setupStartButton() {
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

    public void startGameButton_Handler() {
        System.out.println("Start Game!");
    }
}
