import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class OreBlob extends MovingEntity {
    public static final String BLOB_KEY = "blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;

    public OreBlob(
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
        Optional<Entity> blobTarget =
                world.findNearest(this.getPosition(), Vein.class);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (this.moveTo(world, blobTarget.get(), scheduler)) {
                Quake quake = EntityFactory.createQuake(tgtPos,
                        imageStore.getImageList(Quake.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
        // thought about calling super().scheduleActions, but last line different ('nextPeriod')
        scheduler.scheduleEvent(this,
                ActionsFactory.createActivityAction(this, world, imageStore),
                nextPeriod);
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
                        (this).getAnimationPeriod());

    }

    protected boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
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
            WorldModel world, Point destPos)
    {
//        PathingStrategy strat = new SingleStepPathingStrategy();
        PathingStrategy strat = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = p1 -> world.withinBounds(p1) && !(world.isOccupied(p1)) ||
                world.withinBounds(p1) && world.getOccupant(p1).get().getClass() == Ore.class;
        BiPredicate<Point, Point> withinReach = (p1, p2) -> Functions.distanceSquared(p1, p2) == 1;

        List<Point> path = strat.computePath( this.getPosition(),
                world.findNearest(this.getPosition(), Vein.class).get().getPosition(),
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


