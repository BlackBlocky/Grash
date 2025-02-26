package grash.action.objects;

import grash.action.ActionPhaseController;
import grash.action.ActionPhaseLogicHandler;
import grash.action.CollisionInfo;
import grash.action.CollisionType;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.math.Vec2;

import java.util.HashMap;
import java.util.List;

public final class PlayerObject {

    public static final double JUMP_HEIGHT = 1.6;
    public static final double JUMP_DISTANCE = 3;
    public static final double JUMP_FORCE = 18;
    public static final double JUMP_GRAVITY = 90;
    public static final double JUMP_MAP_SPEED_STANDARD = 12.0;

    private final Vec2 position;
    private Sprite sprite;

    private final HashMap<String, Sprite> spritesMap;

    private Hitbox hitbox;
    private final Hitbox defaultHitbox;
    private final Hitbox sneakHitboxDown;
    private final Hitbox sneakHitboxUp;

    private PlayerState playerState;

    private final GameController game;

    private boolean isDown;

    private boolean jumpOnNextTick;
    private boolean switchSideOnNextTick;
    private boolean switchSneakStateNextTick;

    private double elapsedSecondsAtJump;
    private double currentJumpForce;
    private ObstacleObject lastUsedDoubleJump;

    private double heightChangeMultiplier; // This is used a debug Setting, when the useCustomPlayerHeight is true

    public PlayerObject(GameController gameController, Vec2 startPos,
                        Hitbox hitbox, Hitbox sneakHitboxDown, Hitbox sneakHitboxUp) {
        this.position = startPos;
        this.isDown = (startPos.y == ActionPhaseController.Y_DOWN);

        this.defaultHitbox = hitbox;
        this.sneakHitboxDown = sneakHitboxDown;
        this.sneakHitboxUp = sneakHitboxUp;
        this.hitbox = this.defaultHitbox;

        this.game = gameController;

        this.spritesMap = new HashMap<>();
        spritesMap.put("default", game.getResourceLoader().getSprite("MagnetSnake"));
        spritesMap.put("sneakDown", game.getResourceLoader().getSprite("MagnetSnakeSneakDown"));
        spritesMap.put("sneakUp", game.getResourceLoader().getSprite("MagnetSnakeSneakUp"));

        this.sprite = game.getResourceLoader().getSprite("MagnetSnake");

        this.playerState = PlayerState.Idle;

        this.jumpOnNextTick = false;
        this.switchSideOnNextTick = false;
        this.elapsedSecondsAtJump = 0.0;
        this.currentJumpForce = 0.0;
        this.lastUsedDoubleJump = null;
        this.switchSneakStateNextTick = false;
    }

    public Vec2 getPosition() { return this.position; }
    public Sprite getSprite() { return this.sprite; }
    public Hitbox getHitbox() { return this.hitbox; }
    public PlayerState getPlayerState() { return this.playerState; }

    public void setHeightChangeMultiplier(double heightChangeMultiplier) {
        this.heightChangeMultiplier = heightChangeMultiplier; }

    public void doJumpOnNextTick() { this.jumpOnNextTick = true; }
    public void doSwitchSideOnNextTick() { this.switchSideOnNextTick = true; }
    public void doSwitchSneakStateNextTick() { this.switchSneakStateNextTick = true; }

    public void playerTick(double secondsElapsedSinceStart, double mapSpeed, double realDeltaTime, double deltaTime) {
        // Doing these things first because otherwise it would affect the initial states.
        jumpTick(mapSpeed, deltaTime);

        /* Solving this with an "else if",
        because otherwise it my happen that a sideSwitch and a Jump happening at the same time.

        Note: Doing all these things in the next tick, because the Input Thread is the JavaFX thread, and not the
              tick "thread" */
        if(switchSideOnNextTick) {
            switchSides();
            this.switchSideOnNextTick = false;
        }
        else if(jumpOnNextTick) {
            doJump();
            this.jumpOnNextTick = false;
        }

        if(switchSneakStateNextTick) {
            switchSneakState();
            this.switchSneakStateNextTick = false;
        }

        changeHeight(realDeltaTime); // DEBUG
    }

    private void switchSides() {
        if(playerState == PlayerState.Jumping || playerState == PlayerState.Sneaking) return;

        // Hop to bottom or top if the Player is gliding on a Rope
        if(playerState == PlayerState.RopingToTop) {
            position.y = ActionPhaseController.Y_UP;
            isDown = false;
            playerState = PlayerState.Idle;
            return;
        }
        else if(playerState == PlayerState.RopingToBottom) {
            position.y = ActionPhaseController.Y_DOWN;
            isDown = true;
            playerState = PlayerState.Idle;
            return;
        }

        // Check if there is a Rope at the Player
        ObstacleObject detectedRope = game.getActionPhaseController().getActionPhaseLogicHandler()
                .checkIfRopeIsAtPlayer(
                        game.getActionPhaseController().getActionPhaseValues().getCurrentObstacleObjects(), this
        );

        if(detectedRope != null) { switchToARope(detectedRope); return; };

        // Flip sides
        if (position.y == ActionPhaseController.Y_UP) {
            position.y = ActionPhaseController.Y_DOWN;
        } else {
            position.y = ActionPhaseController.Y_UP;
        }
        isDown = !isDown;
    }

    private void switchToARope(ObstacleObject rope) {
        position.y = rope.getPosition().y;
        if(isDown) playerState = PlayerState.RopingToTop; // Go from bottom to top
        else playerState = PlayerState.RopingToBottom; // Go from top to bottom
    }

    private void doJump() {
        if(playerState != PlayerState.Idle && playerState != PlayerState.Jumping) return;

        ActionPhaseLogicHandler logicHandler = game.getActionPhaseController().getActionPhaseLogicHandler();
        List<ObstacleObject> allObstacleObjects =
                game.getActionPhaseController().getActionPhaseValues().getCurrentObstacleObjects();
        CollisionInfo checkedCollision = logicHandler.checkIfPlayerIsColliding(this, allObstacleObjects);
        if (checkedCollision.getCollisionType() != CollisionType.DoubleJump && playerState == PlayerState.Jumping) return;

        if (checkedCollision.getCollisionType() == CollisionType.DoubleJump) {
            if(checkedCollision.getObstacleObject() == lastUsedDoubleJump) return;
            lastUsedDoubleJump = checkedCollision.getObstacleObject();
        }

        this.playerState = PlayerState.Jumping;
        if(isDown) this.currentJumpForce = -JUMP_FORCE; // Bottom Jump
        else this.currentJumpForce = JUMP_FORCE; // Top Jump
    }

    private void jumpTick(double mapSpeed, double deltaTime) {
        if(playerState != PlayerState.Jumping) return;

        double mapSpeedMultiplier = mapSpeed / JUMP_MAP_SPEED_STANDARD; // Makes e.g.,
                                                                        // the Jump slower if the Map is slower
        double deltaTimeWithMultiplier = deltaTime * mapSpeedMultiplier;

        // Calculate the new y Pos for the Jump
        position.y += currentJumpForce * deltaTimeWithMultiplier;
        if(isDown) currentJumpForce += JUMP_GRAVITY * deltaTimeWithMultiplier;
        else currentJumpForce -= JUMP_GRAVITY * deltaTimeWithMultiplier;

        // Check if the Jump is over or not
        if(isDown && position.y > ActionPhaseController.Y_DOWN) endJump();
        else if (!isDown && position.y < ActionPhaseController.Y_UP) endJump();
    }

    private void switchSneakState() {
        if(playerState == PlayerState.Idle) {
            playerState = PlayerState.Sneaking;
            if(isDown) {
                this.hitbox = sneakHitboxDown;
                this.sprite = spritesMap.get("sneakDown");
            }
            else {
                this.hitbox = sneakHitboxUp;
                this.sprite = spritesMap.get("sneakUp");
            }
        }
        else if (playerState == PlayerState.Sneaking) {
            playerState = PlayerState.Idle;
            this.hitbox = defaultHitbox;
            this.sprite = spritesMap.get("default");
        }
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

    /**
     * DEBUG Method for changing the PlayerHeight.
     * @param realDeltaTime Using realDeltaTime because this should also work when the time is frozen.
     */
    private void changeHeight(double realDeltaTime) {
        final double heightChangeSpeed = 5.0;
        this.position.y += heightChangeSpeed * heightChangeMultiplier * realDeltaTime;
    }

}
