package grash.action.objects;

import grash.action.ActionPhaseController;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.math.Vec2;

public final class PlayerObject {

    public static final double JUMP_HEIGHT = 1.5;
    public static final double JUMP_DISTANCE = 2;

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

        position.y += (isDown) ? -JUMP_HEIGHT : JUMP_HEIGHT;
    }

    private void jumpTick(double mapSpeed, double secondsElapsedSinceStart) {
        if(playerState != PlayerState.Jumping) return;

        // Calculate the new y Pos for the Jump

        // Check if the Jump is over or not
        double secondsSinceJump = secondsElapsedSinceStart - this.elapsedSecondsAtJump;
        double traveledDistanceSinceJump = secondsSinceJump * mapSpeed;
        if(traveledDistanceSinceJump >= JUMP_DISTANCE) endJump();
    }

    private void endJump() {
        position.y -= (isDown) ? -JUMP_HEIGHT : JUMP_HEIGHT;
        this.playerState = PlayerState.Idle;
    }

}
