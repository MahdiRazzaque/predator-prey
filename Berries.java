/**
 * Represents berry plants in the simulation, a specific type of Plant.
 * Berries have unique growth characteristics and prefer specific weather conditions.
 *
 *  * @author  Ozgur Dorunay and Mahdi Razzaque
 *  * @version 18.02.2025
 */

public class Berries extends Plant {
    // Constants that define the plant's characteristics.
    private static final String NAME = "Berries"; // The name of the plant.
    private static final int GROWTH_RATE = 15; // The rate at which the plant grows.
    private static final int REPRODUCTION_RATE = 5; // The rate at which the plant reproduces.
    private static final int LIFE_SPAN = 250; // The lifespan of the plant in days.
    private static final int SPREAD_RATE = 1; // The rate at which the plant spreads.
    private static final int GROWTH_START_HOUR = 18; // The hour when the plant starts growing (6 PM).
    private static final int GROWTH_END_HOUR = 6; // The hour when the plant stops growing (6 AM).
    private static final int GROWTH_STAGE = 0; // The initial growth stage of the plant.


    // Constructor that initializes the Berries plant with its characteristics and environment.
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
