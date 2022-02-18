public class Activity implements Action {
    // only active entities executeAction
    private final ActiveEntity entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(
            ActiveEntity entity,
            WorldModel world,
            ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }


    public void executeAction(
            EventScheduler scheduler) {
        // chased switch cases to if, else cases, made all those cases fall into one case:
        if (entity != null) {
            entity.executeActivity(this.world,
                    this.imageStore, scheduler);
        }
    }
}
