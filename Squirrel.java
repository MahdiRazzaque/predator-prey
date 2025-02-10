import java.util.HashMap;

/**
 * Represents a Squirrel in the simulation.
 * Squirrels are animals that eat Berries. They have specific breeding,
 * age, and food requirements defined in this class.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Squirrel extends Animal {
    private static final int BREEDING_AGE = 5;  // The age at which a squirrel can start breeding (in simulation steps).
    private static final int MAX_AGE = 120; // The maximum age a squirrel can live to (in simulation steps).
    private static final double BREEDING_PROBABILITY = 0.08;  // The probability of a squirrel breeding during a simulation step (0.0 to 1.0).
    private static final int MAX_LITTER_SIZE = 4;  // The maximum number of offspring a squirrel can produce in a single breeding.

    /**
     * Constructs a new Squirrel.
     * A squirrel can be created with a random age or a specific age.
     *
     * @param randomAge If true, the squirrel will have a random age; otherwise, it starts at age 0.
     * @param location The location of the squirrel in the field.
     * @param gender The gender of the squirrel.
     * @param simulator The simulator instance.
     */
    public Squirrel(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Squirrel.class, gender, simulator);
    }

    /**
     * Creates a map of food sources and their corresponding food values for the squirrel.
     * This map defines what the squirrel eats and how much nutrition it gets from each food source.
     *
     * @return A HashMap containing the food sources (String, e.g., "Berries") and their food values (Integer, e.g., 8).
     */
    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>(); // Initialise a new HashMap to store food sources.
        foodSources.put("Berries", 8); // Add "Berries" as a food source with a food value of 8.
        return foodSources; // Return the HashMap containing the squirrel's food sources.
    }
}
