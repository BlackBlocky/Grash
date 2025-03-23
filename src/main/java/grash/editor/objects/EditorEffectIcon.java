package grash.editor.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.MapEffectType;
import grash.math.Vec2;

public class EditorEffectIcon {
    public static final double Y_POS_1 = 2.0, Y_POS_2 = 1.8, Y_POS_3 = 1.6, Y_POS_4 = 1.4, Y_POS_5 = 1.2;

    private final GameController game;

    private Vec2 pos;
    private MapEffectType type;

    private Sprite mySprite;

    public EditorEffectIcon(Vec2 pos, MapEffectType type, GameController gameController) {
        this.pos = pos;
        this.type = type;
        this.game = gameController;

        validateSpriteByType();
    }

    public Sprite getSprite() {
        return this.mySprite;
    }
    public Vec2 getPos() {
        return pos;
    }

    public static double getYPosByIndex(int index) {
        int indexRange = index % 5;
        return switch (indexRange) {
            case 0 -> Y_POS_1;
            case 1 -> Y_POS_2;
            case 2 -> Y_POS_3;
            case 3 -> Y_POS_4;
            default -> Y_POS_5;
        };
    }

    private void validateSpriteByType() {
        this.mySprite = switch (type) {
            case Color -> game.getResourceLoader().getSprite("Editor_ColorIcon");
            case FOVScale -> game.getResourceLoader().getSprite("Editor_ScaleIcon");
            case Rotate -> game.getResourceLoader().getSprite("Editor_RotateIcon");
            default -> null;
        };
    }
}
