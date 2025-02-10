import java.util.HashMap;
import java.util.List;

/**
 * Represents a Grouse in the simulation.
 * Grouse are animals that eat Seeds. They have specific breeding,
 * age, and food requirements defined in this class.  Grouse behaviour
 * differs between day and night.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Grouse extends Animal{
    private static final int BREEDING_AGE = 5;          // The age at which a grouse can start breeding (in simulation steps).
    private static final int MAX_AGE = 130;         // The maximum age a grouse can live to (in simulation steps).
    private static final double BREEDING_PROBABILITY = 0.09; // The probability of a grouse breeding during a simulation step (0.0 to 1.0).
    private static final int MAX_LITTER_SIZE = 6;       // The maximum number of offspring a grouse can produce in a single breeding.

    /**
     * Creates a new Grouse.
     *
     * @param randomAge If true, the grouse will have a random age; otherwise, it starts at age 0.
     * @param location The location of the grouse in the field.
     * @param gender The gender of the grouse.
     * @param simulator The simulator instance.
     */
    public Grouse(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Grouse.class, gender, simulator);
    }

    /**
     * Creates a map of food sources and their corresponding food values for the grouse.
     * This map defines what the grouse eats and how much nutrition it gets from each food source.
     *
     * @return A HashMap containing the food sources (String, e.g., "Seeds") and their food values (Integer, e.g., 5).
     */
    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>(); // Initialise a new HashMap to store food sources
        foodSources.put("Seeds", 5); // Add "Seeds" as a food source with a food value of 5.
        return foodSources; // Return the HashMap containing the grouse's food sources.
    }

    /**
     * Represents the behaviour of the grouse in each step of the simulation.
     * The grouse ages, potentially decreases its food level, and then,
     * depending on the time of day, either attempts to breed (at a reduced rate during the night)
     * or moves to find food and/or breed (during the day).
     *
     * @param currentField The field the grouse is currently in.
     * @param nextFieldState The field representing the next state of the simulation.
     */
    @Override
    public void act(Field currentField, Field nextFieldState) {
        incrementAge(); // Increase the grouse's age.
        if (!FOOD_SOURCES.isEmpty()) {
            decreaseFoodLevel(); // Decrease the grouse's food level, if it has food sources.
        }

        if (!isAlive())
            return; // If the grouse is not alive, exit the method.

        // Movement and finding food disabled at night. Birth rate heavily reduced
        if (simulator.getTime().isNight()) { // Check if it is night.
            if (rand.nextDouble() < 0.001) { // Reduced breeding probability at night.
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                if (!freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations, currentField); // Attempt to give birth if there are free locations.
                }
            } else {
                Location currentLocation = getLocation();
                nextFieldState.placeEntity(this, currentLocation); // Stay in the same location.
            }
            return; // Exit the method after nighttime actions.
        }

        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
        if (!freeLocations.isEmpty()) {
            giveBirth(nextFieldState, freeLocations, currentField); // Attempt to give birth if there are free locations.
        }
        // Move towards a source of food if found
        Location nextLocation = findFood(currentField);
        if (nextLocation == null && !freeLocations.isEmpty()) {
            // No food found - try to move to a free location.
            nextLocation = freeLocations.remove(0); // Move to a free location if no food is found.
        }
        // See if it was possible to move.
        if (nextLocation != null) {
            setLocation(nextLocation); // Set the new location.
            nextFieldState.placeEntity(this, nextLocation); // Place the grouse in the new location
        } else {
            setDead(); // If it was not possible to move, the grouse dies.
        }
    }
}
