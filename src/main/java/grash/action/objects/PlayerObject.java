package grash.action.objects;

import grash.action.ActionPhaseController;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.math.Vec2;

public final class PlayerObject {

    private final Vec2 position;
    private final Sprite sprite;

    private final GameController game;

    private boolean jumpOnNextTick;
    private boolean switchSideOnNextTick;

    public PlayerObject(GameController gameController, Vec2 startPos) {
        this.position = startPos;
        this.game = gameController;
        this.sprite = game.getResourceLoader().getSprite("MagnetSnake");

        this.jumpOnNextTick = false;
        this.switchSideOnNextTick = false;
    }

    public Vec2 getPosition() { return this.position; }
    public Sprite getSprite() { return this.sprite; }

    public void doJumpOnNextTick() { this.jumpOnNextTick = true; }
    public void doSwitchSideOnNextTick() { this.switchSideOnNextTick = true; }

    public void playerTick() {
        if(switchSideOnNextTick) {
            switchSides();
            this.switchSideOnNextTick = false;
        }
    }

    private void switchSides() {
        if(position.y == ActionPhaseController.Y_UP) {
            position.y = ActionPhaseController.Y_DOWN;
        }
        else {
            position.y = ActionPhaseController.Y_UP;
        }
    }

}
