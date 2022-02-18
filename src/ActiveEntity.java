import processing.core.PImage;

import java.util.List;

public abstract class ActiveEntity extends Entity{

    private final int actionPeriod;

    protected ActiveEntity(Point position, List<PImage> images, int actionPeriod) {
        super(position, images);
        this.actionPeriod = actionPeriod;

    }

    public int getActionPeriod(){return actionPeriod;}

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                ActionsFactory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }

    protected abstract void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler);
}
