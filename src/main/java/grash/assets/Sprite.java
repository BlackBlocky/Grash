package grash.assets;

import javafx.scene.image.Image;

public final class Sprite {
    private final Image image;
    private final String spriteName;

    public Sprite(Image image, String spriteName) {
        this.image = image;
        this.spriteName = spriteName;
    }

    public Image getImage() {
        return image;
    }

    public String getSpriteName() {
        return this.spriteName;
    }

    public double getHeight() {
        return image.getHeight();
    }

    public double getWidth() {
        return image.getWidth();
    }
}
