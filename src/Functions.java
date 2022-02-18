import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import processing.core.PImage;
import processing.core.PApplet;

public final class Functions
{
    public static final Random rand = new Random();

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    public static final int PROPERTY_KEY = 0;

    public static void loadImages(
            Scanner in, ImageStore imageStore, PApplet screen)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(imageStore.getImages(), in.nextLine(), screen);
            }
            catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d%n",
                              lineNumber);
            }
            lineNumber++;
        }
    }

    private static void processImageLine(
            Map<String, List<PImage>> images, String line, PApplet screen)
    {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    private static List<PImage> getImages(
            Map<String, List<PImage>> images, String key)
    {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    private static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public static void load(
            Scanner in, WorldModel world, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                if (!processLine(in.nextLine(), world, imageStore)) {
                    System.err.printf("invalid entry on line %d%n",
                            lineNumber);
                }
            }
            catch (NumberFormatException e) {
                System.err.printf("invalid entry on line %d%n", lineNumber);
            }
            catch (IllegalArgumentException e) {
                System.err.printf("issue on line %d: %s%n", lineNumber,
                              e.getMessage());
            }
            lineNumber++;
        }
    }

    private static boolean processLine(
            String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case Background.BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case MinerFull.MINER_KEY:
                    return parseMiner(properties, world, imageStore);
                case Obstacle.OBSTACLE_KEY:
                    return parseObstacle(properties, world, imageStore);
                case Ore.ORE_KEY:
                    return parseOre(properties, world, imageStore);
                case BlackSmith.SMITH_KEY:
                    return parseSmith(properties, world, imageStore);
                case Vein.VEIN_KEY:
                    return parseVein(properties, world, imageStore);
            }
        }
        return false;
    }

    private static boolean parseVein(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Vein.VEIN_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Vein.VEIN_COL]),
                    Integer.parseInt(properties[Vein.VEIN_ROW]));
            Vein vein = EntityFactory.createVein(pt,
                    Integer.parseInt(
                            properties[Vein.VEIN_ACTION_PERIOD]),
                    imageStore.getImageList(Vein.VEIN_KEY));
            vein.tryAddEntity(world);
        }

        return properties.length == Vein.VEIN_NUM_PROPERTIES;
    }


    private static boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Background.BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Background.BGND_COL]),
                                 Integer.parseInt(properties[Background.BGND_ROW]));
            String id = properties[Background.BGND_ID];
            world.setBackground(pt,
                          new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == Background.BGND_NUM_PROPERTIES;
    }

    private static boolean parseMiner(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        // does this parse MinerFull or MinerNotFull??
        if (properties.length == MinerFull.MINER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[MinerFull.MINER_COL]),
                                 Integer.parseInt(properties[MinerFull.MINER_ROW]));
            MinerNotFull miner = EntityFactory.createMinerNotFull(Integer.parseInt(
                                                       properties[MinerFull.MINER_LIMIT]),
                                               pt, Integer.parseInt(
                            properties[MinerFull.MINER_ACTION_PERIOD]), Integer.parseInt(
                            properties[MinerFull.MINER_ANIMATION_PERIOD]),
                            imageStore.getImageList(MinerFull.MINER_KEY));
            miner.tryAddEntity(world);
        }

        return properties.length == MinerFull.MINER_NUM_PROPERTIES;
    }

    private static boolean parseObstacle(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Obstacle.OBSTACLE_COL]),
                                 Integer.parseInt(properties[Obstacle.OBSTACLE_ROW]));
            Obstacle obs = EntityFactory.createObstacle(pt,
                    imageStore.getImageList(Obstacle.OBSTACLE_KEY));
            obs.tryAddEntity(world);
        }

        return properties.length == Obstacle.OBSTACLE_NUM_PROPERTIES;
    }

    private static boolean parseOre(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == Ore.ORE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Ore.ORE_COL]),
                                 Integer.parseInt(properties[Ore.ORE_ROW]));
            Ore ore = EntityFactory.createOre(pt, Integer.parseInt(
                    properties[Ore.ORE_ACTION_PERIOD]),
                    imageStore.getImageList(Ore.ORE_KEY));
            ore.tryAddEntity(world);
        }

        return properties.length == Ore.ORE_NUM_PROPERTIES;
    }

    private static boolean parseSmith(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BlackSmith.SMITH_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BlackSmith.SMITH_COL]),
                                 Integer.parseInt(properties[BlackSmith.SMITH_ROW]));
            BlackSmith smith = EntityFactory.createBlacksmith(pt,
                    imageStore.getImageList(BlackSmith.SMITH_KEY));
            smith.tryAddEntity(world);
        }

        return properties.length == BlackSmith.SMITH_NUM_PROPERTIES;
    }



    public static int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }


    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }


}