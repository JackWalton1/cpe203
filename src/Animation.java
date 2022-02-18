public class Animation implements Action {
    // only active entities executeAction
    private final AnimateEntity entity;
    private final int repeatCount;

    public Animation(
            AnimateEntity entity,
            int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        (this.entity).nextImage();

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity,
                    ActionsFactory.createAnimationAction(this.entity,
                            Math.max(this.repeatCount - 1,
                                    0)),
                    (this.entity).getAnimationPeriod());
        }
    }


}
