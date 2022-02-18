import processing.core.PImage;

import java.util.List;

public class EntityFactory {

    public static BlackSmith createBlacksmith(
            Point position, List<PImage> images)
    {
        return new BlackSmith(position, images);
    }

    public static MinerFull createMinerFull(
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerFull(position, images,
                          resourceLimit, actionPeriod,
                          animationPeriod);
    }

    public static MinerNotFull createMinerNotFull(
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerNotFull(position, images,
                          resourceLimit, 0, actionPeriod, animationPeriod);
    }

    public static Obstacle createObstacle(Point position, List<PImage> images)
    {
        return new Obstacle(position, images);
    }

    public static Ore createOre(Point position, int actionPeriod, List<PImage> images)
    {
        return new Ore(position, images, actionPeriod);
    }

    public static OreBlob createOreBlob(
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new OreBlob(position, images, actionPeriod, animationPeriod);
    }

    public static Quake createQuake(
            Point position, List<PImage> images)
    {
        return new Quake(position, images,
                Quake.QUAKE_ACTION_PERIOD, Quake.QUAKE_ANIMATION_PERIOD);
    }

    public static Vein createVein(
            Point position, int actionPeriod, List<PImage> images)
    {
        return new Vein(position, images, actionPeriod);
    }


}
