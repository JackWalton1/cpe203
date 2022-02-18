import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MinerNotFull extends MovingEntity{

    private final int resourceLimit;
    private int resourceCount;

    public MinerNotFull(
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod)
    {
        super(position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
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
        Optional<Entity> notFullTarget =
                world.findNearest(this.getPosition(), Ore.class);

        if (notFullTarget.isEmpty() || !this.moveTo(world,
                notFullTarget.get(),
                scheduler)
                || !this.transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    ActionsFactory.createActivityAction(this, world, imageStore),
                    this.getActionPeriod());
        }
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {

        super.scheduleActions(scheduler,
            world,
            imageStore);
        scheduler.scheduleEvent(this,
                        ActionsFactory.createAnimationAction(this, 0),
                        this.getAnimationPeriod());

    }

    private boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            MinerFull miner = EntityFactory.createMinerFull(this.resourceLimit,
                    this.getPosition(), this.getActionPeriod(),
                    this.getAnimationPeriod(),
                    this.getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }


    protected boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            this.resourceCount += 1;
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);

            return true;
        }
        else {
            return moveToHelper(world,
                    target,
                    scheduler);
        }
    }


    protected Point nextPosition(
            WorldModel world, Point destPos) {
//        PathingStrategy strat = new SingleStepPathingStrategy();
        PathingStrategy strat = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = p1 -> world.withinBounds(p1) && !(world.isOccupied(p1));
        BiPredicate<Point, Point> withinReach = (p1, p2) -> Functions.distanceSquared(p1, p2) == 1;

        List<Point> path = strat.computePath(this.getPosition(),
                world.findNearest(this.getPosition(), Ore.class).get().getPosition(),
                canPassThrough,
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        Point newPos;
        if (path.size() > 0)
        {
            newPos = path.get(0);
        }
        else
        {
            newPos = this.getPosition();
        }

        return newPos;
    }
}

