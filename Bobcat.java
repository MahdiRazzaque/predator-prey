import java.util.HashMap;

/**
 * Represents a Bobcat in the simulation.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Bobcat extends Animal {
    private static final int BREEDING_AGE = 8; // The minimum age for a bobcat to breed.
    private static final int MAX_AGE = 130; // The maximum age a bobcat can reach.
    private static final double BREEDING_PROBABILITY = 0.06; // The likelihood of a bobcat breeding (0.0 - 1.0).
    private static final int MAX_LITTER_SIZE = 3; // The maximum number of offspring in a single birth.

    /**
     * Constructor for the Bobcat class. Creates a new Bobcat instance with the specified parameters.
     * @param randomAge If true, the bobcat's age is set randomly; otherwise, it starts at 0.
     * @param location The bobcat's initial location on the field.
     * @param gender The bobcat's gender.
     * @param simulator The simulator instance managing this bobcat.
     */
    public Bobcat(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Bobcat.class, gender, simulator);
    }

    /**
     * Creates a HashMap representing the food sources for a Bobcat and their corresponding nutritional values.
     * @return A HashMap where keys are food source names (Strings) and values are nutritional values (Integers).
     */
    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();
        foodSources.put("Squirrel", 20);
        return foodSources;
    }
}
