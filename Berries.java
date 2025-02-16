public class Berries extends Plant {
    private static final String NAME = "Berries";
    private static final int GROWTH_RATE = 15;
    private static final int REPRODUCTION_RATE = 5;
    private static final int LIFE_SPAN = 250;
    private static final int SPREAD_RATE = 1;
    private static final int GROWTH_START_HOUR = 18;
    private static final int GROWTH_END_HOUR = 6;
    private static final int GROWTH_STAGE = 0;

    public Berries(Location location, Simulator simulator, Time time) {
        super(NAME, GROWTH_RATE, REPRODUCTION_RATE, LIFE_SPAN, SPREAD_RATE, GROWTH_START_HOUR, GROWTH_END_HOUR, GROWTH_STAGE, location, simulator, time, Berries.class);
    }

    /**
     * Determines if the plant can grow at the current time.
     * Berries only grow if the weather is Sunny or Cloudy, and if the current time is within their growth hours.
     * @return {@code true} if the plant can grow, {@code false} otherwise.
     */
    @Override
    protected boolean canGrow() {
        String currentWeather = simulator.getWeather().getWeatherText(); // Fetches the current weather conditions from the simulator.
        if(!currentWeather.equals("Sunny") && !currentWeather.equals("Cloudy"))
            return false; // Berries cannot grow unless the weather is Sunny or Cloudy.

        int currentHour = time.getHour(); // Gets the current hour of the simulation.
        if (growthStartHour < currentHour) {
            return currentHour >= growthStartHour && currentHour < growthEndHour; // Checks if the current hour is within the growth window (when growthStartHour < currentHour).
        }
        else {
            return currentHour >= growthStartHour || currentHour < growthEndHour; // Checks if the current hour is within the growth window (when growthStartHour > currentHour, handles cases that growth hours cross midnight).
        }
    }
}
