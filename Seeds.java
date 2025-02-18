/**
 * Concrete class representing Seeds within the simulation. Extends the `Plant` class,
 * adding specific attributes like growth rate, reproduction rate, lifespan, and spread rate.
 * Seeds are a type of plant that grows, reproduces, and spreads within their specified hours.
 *
 * @author Ozgur Dorunay
 * @version 10.02.2025
 */
public class Seeds extends Plant {
    private static final String NAME = "Seeds"; // The name of the plant.
    private static final int GROWTH_RATE = 5; // The rate at which the plant grows.
    private static final int REPRODUCTION_RATE = 10; // The rate at which the plant reproduces.
    private static final int LIFE_SPAN = -1; // The lifespan of the plant in days (-1 indicates indefinite lifespan).
    private static final int SPREAD_RATE = 3; // The rate at which the plant spreads.
    private static final int GROWTH_START_HOUR = 6; // The hour when the plant starts growing (6 AM).
    private static final int GROWTH_END_HOUR = 18; // The hour when the plant stops growing (6 PM).
    private static final int GROWTH_STAGE = 0; // The initial growth stage of the plant.

    // Constructor that initializes the Seeds plant with its characteristics and environment.
    public Seeds(Location location, Simulator simulator, Time time) {
        super(NAME, GROWTH_RATE, REPRODUCTION_RATE, LIFE_SPAN, SPREAD_RATE, GROWTH_START_HOUR, GROWTH_END_HOUR, GROWTH_STAGE, location, simulator, time, Seeds.class);
    }
}
