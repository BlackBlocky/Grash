package grash.action.objects;

import grash.action.ActionPhaseController;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.math.Vec2;

public final class PlayerObject {

    public static final double JUMP_HEIGHT = 1.6;
    public static final double JUMP_DISTANCE = 3;

    private final Vec2 position;
    private final Sprite sprite;

    private PlayerState playerState;

    private final GameController game;

    private boolean isDown;

    private boolean jumpOnNextTick;
    private boolean switchSideOnNextTick;

    private double elapsedSecondsAtJump;

    public PlayerObject(GameController gameController, Vec2 startPos) {
        this.position = startPos;
        this.isDown = (startPos.y == ActionPhaseController.Y_DOWN);

        this.game = gameController;
        this.sprite = game.getResourceLoader().getSprite("MagnetSnake");

        this.playerState = PlayerState.Idle;

        this.jumpOnNextTick = false;
        this.switchSideOnNextTick = false;
        this.elapsedSecondsAtJump = 0.0;
    }

    public Vec2 getPosition() { return this.position; }
    public Sprite getSprite() { return this.sprite; }

    public void doJumpOnNextTick() { this.jumpOnNextTick = true; }
    public void doSwitchSideOnNextTick() { this.switchSideOnNextTick = true; }

    public void playerTick(double secondsElapsedSinceStart, double mapSpeed) {
        // Doing these things first because otherwise it would affect the initial states.
        jumpTick(mapSpeed, secondsElapsedSinceStart);

        /* Solving this with an "else if",
        because otherwise it my happen that a sideSwitch and a Jump happening at the same time. */
        if(switchSideOnNextTick && playerState == PlayerState.Idle) {
            switchSides();
            this.switchSideOnNextTick = false;
        }
        else if(jumpOnNextTick) {
            doJump(secondsElapsedSinceStart);
            this.jumpOnNextTick = false;
        }
    }

    private void switchSides() {
        if(position.y == ActionPhaseController.Y_UP) {
            position.y = ActionPhaseController.Y_DOWN;
        }
        else {
            position.y = ActionPhaseController.Y_UP;
        }

        isDown = !isDown;
    }

    private void doJump(double secondsElapsedSinceStart) {
        if(playerState == PlayerState.Jumping) return;

        this.playerState = PlayerState.Jumping;
        this.elapsedSecondsAtJump = secondsElapsedSinceStart;
    }

    private void jumpTick(double mapSpeed, double secondsElapsedSinceStart) {
        if(playerState != PlayerState.Jumping) return;

        // Calculate the new y Pos for the Jump
        double jumpHeightMultiplier = calculateJumpHeightMultiplier(secondsElapsedSinceStart, mapSpeed);
        if(isDown) position.y = ActionPhaseController.Y_DOWN - (JUMP_HEIGHT * jumpHeightMultiplier);
        else position.y = ActionPhaseController.Y_UP + (JUMP_HEIGHT * jumpHeightMultiplier);

        // Check if the Jump is over or not
        double secondsSinceJump = secondsElapsedSinceStart - this.elapsedSecondsAtJump;
        double traveledDistanceSinceJump = secondsSinceJump * mapSpeed;
        if(traveledDistanceSinceJump >= JUMP_DISTANCE) endJump();
    }

    private double calculateJumpHeightMultiplier(double secondsElapsedSinceStart, double mapSpeed) {
        double maxJumpDurationSeconds = JUMP_DISTANCE / mapSpeed;
        double currentJumpDurationSeconds = secondsElapsedSinceStart - this.elapsedSecondsAtJump;
        double currentJumpDurationNormalized = currentJumpDurationSeconds / maxJumpDurationSeconds;
        /* \/ We currently have a value from 0 to 1.
        But now it's going to be mapped like this "0.0 - 1.0 - 0.0", while the middle is 0.5.
        This is going to be solved with a parabola in the linear factor form*/
        return -4.0 * (currentJumpDurationNormalized - 0.0) * (currentJumpDurationNormalized - 1.0);
    }

    private void endJump() {
        position.y = (isDown) ? ActionPhaseController.Y_DOWN : ActionPhaseController.Y_UP;
        this.playerState = PlayerState.Idle;
    }

}
