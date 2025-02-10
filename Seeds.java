public class Seeds extends Plant {
    private static final String NAME = "Seeds";
    private static final int GROWTH_RATE = 5;
    private static final int REPRODUCTION_RATE = 15;
    private static final int LIFE_SPAN = -1;
    private static final int SPREAD_RATE = 2;
    private static final int GROWTH_START_HOUR = 6;
    private static final int GROWTH_END_HOUR = 18;
    private static final int GROWTH_STAGE = 0;

    public Seeds(Location location, Simulator simulator, Time time) {
        super(NAME, GROWTH_RATE, REPRODUCTION_RATE, LIFE_SPAN, SPREAD_RATE, GROWTH_START_HOUR, GROWTH_END_HOUR, GROWTH_STAGE, location, simulator, time, Seeds.class);
        }
}
