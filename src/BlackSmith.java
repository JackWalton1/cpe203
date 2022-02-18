import processing.core.PImage;

import java.util.List;

public class BlackSmith extends Entity{

    public static final String SMITH_KEY = "blacksmith";
    public static final int SMITH_NUM_PROPERTIES = 4;
    public static final int SMITH_COL = 2;
    public static final int SMITH_ROW = 3;

    public BlackSmith(
            Point position,
            List<PImage> images)
    {
        super(position, images);
    }

    protected PImage getCurrentImage() {

        return this.getImages().get(0);
    }


}

