import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Vein extends ActiveEntity{

    public static final String VEIN_KEY = "vein";
    public static final int VEIN_NUM_PROPERTIES = 5;
    public static final int VEIN_COL = 2;
    public static final int VEIN_ROW = 3;
    public static final int VEIN_ACTION_PERIOD = 4;

//    private Point position;
//    private final List<PImage> images;
//    private final int actionPeriod;

    public Vein(
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
        Optional<Point> openPt = world.findOpenAround(this.getPosition());

        if (openPt.isPresent()) {
            Ore ore = EntityFactory.createOre(openPt.get(),
                    Ore.ORE_CORRUPT_MIN + Functions.rand.nextInt(
                            Ore.ORE_CORRUPT_MAX - Ore.ORE_CORRUPT_MIN),
                    imageStore.getImageList(Ore.ORE_KEY));
            world.addEntity(ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                ActionsFactory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
    }


}

