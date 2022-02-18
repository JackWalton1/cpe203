import processing.core.PImage;

import java.util.List;

public abstract class AnimateEntity extends ActiveEntity{
    private final int animationPeriod;
    private int imageIndex;

    public AnimateEntity(Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;

    }

    protected abstract void nextImage();

    public int getAnimationPeriod() {return animationPeriod; }

    public int getImageIndex() {return imageIndex; }

    public void setImageIndex(int newImageIndex) {this.imageIndex =  newImageIndex; }


}
