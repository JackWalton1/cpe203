import java.util.List;

import processing.core.PImage;

public final class Background

{
    public static final String BGND_KEY = "background";
    public static final int BGND_NUM_PROPERTIES = 4;
    public static final int BGND_ID = 1;
    public static final int BGND_COL = 2;
    public static final int BGND_ROW = 3;
    private final List<PImage> images;
    private int imageIndex;

    private String id;

    public Background(String id, List<PImage> images) {
        this.images = images;
    }

    public PImage getCurrentImage() {
        return this.images.get(this.imageIndex);
    }

}
