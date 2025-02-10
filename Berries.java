public class Berries extends Plant {
    private static final String NAME = "Berries";
    private static final int GROWTH_RATE = 15;
    private static final int REPRODUCTION_RATE = 50;
    private static final int LIFE_SPAN = 250;
    private static final int SPREAD_RATE = 1;
    private static final int GROWTH_START_HOUR = 18;
    private static final int GROWTH_END_HOUR = 6;
    private static final int GROWTH_STAGE = 0;

    public Berries(Location location, Simulator simulator, Time time) {
        super(NAME, GROWTH_RATE, REPRODUCTION_RATE, LIFE_SPAN, SPREAD_RATE, GROWTH_START_HOUR, GROWTH_END_HOUR, GROWTH_STAGE, location, simulator, time, Berries.class);
    }
}
