import processing.core.PImage;

import java.util.List;
import java.util.Optional;


public abstract class MovingEntity extends AnimateEntity {

    public MovingEntity(Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(position, images, actionPeriod, animationPeriod);
    }

    protected abstract Point nextPosition(
            WorldModel world, Point destPos);

    public boolean moveToHelper(WorldModel world,
                                Entity target,
                                EventScheduler scheduler){
        Point nextPos = this.nextPosition(world, target.getPosition());

        if (!this.getPosition().equals(nextPos)) {
            Optional<Entity> occupant = world.getOccupant(nextPos);
            occupant.ifPresent(scheduler::unscheduleAllEvents);

            world.moveEntity(this, nextPos);
        }
        return false;
    }

    protected abstract boolean moveTo(WorldModel world,
                                      Entity target,
                                      EventScheduler scheduler);

}
