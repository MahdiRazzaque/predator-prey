import java.util.HashMap;

/**
 * Represents a Wolf in the simulation.
 * Wolves are predators that eat Squirrels and Grouse.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Wolf extends Animal {
    private static final int BREEDING_AGE = 10;  // The age at which a wolf can start breeding (in simulation steps).
    private static final int MAX_AGE = 140; // The maximum age a wolf can live to (in simulation steps).
    private static final double BREEDING_PROBABILITY = 0.05;  // The probability of a wolf breeding during a simulation step (0.0 to 1.0).
    private static final int MAX_LITTER_SIZE = 6;  // The maximum number of offspring a wolf can produce in a single breeding.

    /**
     * Constructs a new Wolf.
     *
     * @param randomAge If true, the wolf will have a random age; otherwise, it starts at age 0.
     * @param location The location of the wolf in the field.
     * @param gender The gender of the wolf.
     * @param simulator The simulator instance.
     */
    public Wolf(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Wolf.class, gender, simulator);
    }

    /**
     * Creates a map of food sources and their corresponding food values for the wolf.
     * This map defines what the wolf eats and how much nutrition it gets from each food source.
     *
     * @return A HashMap containing the food sources (String, e.g., "Squirrel") and their food values (Integer, e.g., 12).
     */
    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();  // Initialise a new HashMap to store food sources.
        foodSources.put("Squirrel", 12);  // Add "Squirrel" as a food source with a food value of 12.
        foodSources.put("Grouse", 7);   // Add "Grouse" as a food source with a food value of 7.
        return foodSources;  // Return the HashMap containing the wolf's food sources.
    }
}
