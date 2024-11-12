package grash.core;

public abstract class GrashComponent {
    public final GameController game;
    public final int id;

    public GrashComponent(GameController gameController) {
        this.game = gameController;
        this.id = game.generateUniqueID();
        game.registerGrashComponent(this);
    }

    public abstract void start();
    public abstract void update();
}
