import processing.core.PImage;
import java.util.List;

public class Quake extends AnimateEntity{

    public static final String QUAKE_KEY = "quake";
    public static final int QUAKE_ACTION_PERIOD = 1100;
    public static final int QUAKE_ANIMATION_PERIOD = 100;
    public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public Quake(
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(position, images, actionPeriod, animationPeriod);
    }

    protected PImage getCurrentImage() {

        return this.getImages().get(this.getImageIndex());
    }

    protected void nextImage() {
        this.setImageIndex((this.getImageIndex() + 1) % this.getImages().size()) ;
    }


    protected void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        super.scheduleActions(scheduler,
                world,
                imageStore);
        scheduler.scheduleEvent(this, ActionsFactory.createAnimationAction(this,
                QUAKE_ANIMATION_REPEAT_COUNT), (this).getAnimationPeriod());
    }

}


