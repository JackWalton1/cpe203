import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MinerFull extends MovingEntity{
    public static final String MINER_KEY = "miner";
    public static final int MINER_NUM_PROPERTIES = 7;
    public static final int MINER_COL = 2;
    public static final int MINER_ROW = 3;
    public static final int MINER_LIMIT = 4;
    public static final int MINER_ACTION_PERIOD = 5;
    public static final int MINER_ANIMATION_PERIOD = 6;

    private final int resourceLimit;

    public MinerFull(
            Point position,
            List<PImage> images,
            int resourceLimit,
            int actionPeriod,
            int animationPeriod)
    {
        super(position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
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
        Optional<Entity> fullTarget =
                world.findNearest(this.getPosition(), BlackSmith.class);

        if (fullTarget.isPresent() && this.moveTo(world,
                fullTarget.get(), scheduler))
        {
            this.transformFull(world, scheduler, imageStore);
        }
        else {
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


    private void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        MinerNotFull baby_miner = EntityFactory.createMinerNotFull(this.resourceLimit,
                this.getPosition(), this.getActionPeriod(),
                this.getAnimationPeriod(),
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(baby_miner);
        baby_miner.scheduleActions(scheduler, world, imageStore);
    }

    protected boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                world.findNearest(this.getPosition(), BlackSmith.class);
        if (this.getPosition().adjacent(fullTarget.get().getPosition())) {
            return true;
        }
        else {
            return moveToHelper(world,
                    target,
                    scheduler);
        }
    }

    protected Point nextPosition(
            WorldModel world, Point destPos)
    {
//        PathingStrategy strat = new SingleStepPathingStrategy();
        PathingStrategy strat = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = p1 -> world.withinBounds(p1) && !(world.isOccupied(p1));
        BiPredicate<Point, Point> withinReach = (p1, p2) -> Functions.distanceSquared(p1, p2) == 1;

        List<Point> path = strat.computePath( this.getPosition(),
                world.findNearest(this.getPosition(), BlackSmith.class).get().getPosition(),
                canPassThrough,
                withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        Point newPos;
        if (path.size() > 0) {
            newPos = path.get(0);
        }

        else {
            newPos = this.getPosition();
        }
        return newPos;

    }

}
