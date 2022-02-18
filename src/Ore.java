import processing.core.PImage;

import java.util.List;

public class Ore extends ActiveEntity{

    public static final int ORE_CORRUPT_MIN = 9000;
    public static final int ORE_CORRUPT_MAX = 20000;
    public static final int ORE_REACH = 1;

    public static final String ORE_KEY = "ore";
    public static final int ORE_NUM_PROPERTIES = 5;
    public static final int ORE_COL = 2;
    public static final int ORE_ROW = 3;
    public static final int ORE_ACTION_PERIOD = 4;


    public Ore(
            Point position,
            List<PImage> images,
            int actionPeriod)
    {
        super(position, images, actionPeriod);
    }

    protected PImage getCurrentImage() {

        return this.getImages().get(0);
    }


     protected void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        OreBlob blob = EntityFactory.createOreBlob(pos,
                this.getActionPeriod() / OreBlob.BLOB_PERIOD_SCALE,
                OreBlob.BLOB_ANIMATION_MIN + Functions.rand.nextInt(
                        OreBlob.BLOB_ANIMATION_MAX
                                - OreBlob.BLOB_ANIMATION_MIN),
                imageStore.getImageList(OreBlob.BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }


}
