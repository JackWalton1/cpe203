import processing.core.PImage;

import java.util.List;

public abstract class Entity {
    private Point position;
    private final List<PImage> images;

    protected Entity(Point position, List<PImage> images) {
        this.position = position;
        this.images = images;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public List<PImage> getImages(){ return images; }

    protected abstract PImage getCurrentImage();

    public void tryAddEntity(WorldModel world) {
        if (world.isOccupied(this.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        world.addEntity(this);
    }
}
