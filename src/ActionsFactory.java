public class ActionsFactory {

    public static Animation createAnimationAction(AnimateEntity entity, int repeatCount) {
        return new Animation(entity, repeatCount);
    }

    public static Activity createActivityAction(
            ActiveEntity entity, WorldModel world, ImageStore imageStore)
    {
        return new Activity(entity, world, imageStore);
    }

}
