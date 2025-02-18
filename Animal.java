import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Abstract class representing an animal within the simulation. Extends the `Entity` class,
 * adding animal-specific attributes like age, breeding, food level, and movement.
 * Animal classes should inherit from this class.
 *
 * @author Mahdi Razzaque and Ozgur Dorunay
 * @version 10.02.2025
 */
public class Animal extends Entity {
    protected int BREEDING_AGE; // The minimum age for an animal to breed.
    protected int MAX_AGE; // The maximum age an animal can reach.
    protected double BREEDING_PROBABILITY; // The likelihood of breeding in a given step (0.0 - 1.0).
    protected int MAX_LITTER_SIZE; // The maximum number of offspring in a single birth.
    protected HashMap<String, Integer> FOOD_SOURCES; // Map of food source names to their nutritional values.
    protected static final Random rand = Randomizer.getRandom(); // Random number generator for the simulation.
    protected final Class<? extends Animal> SPECIES; // The specific species of this animal.
    protected Gender gender; // The gender of the animal (MALE or FEMALE).

    protected int age; // The current age of the animal (in simulation steps).
    protected int foodLevel; // The current food level of the animal.

    protected Disease disease;  // The disease the animal is infected with
    protected int timeInfected; // Number of steps the animal has been infected

    /**
     * Constructor for the Animal class. Creates a new animal with the specified parameters,
     * including its age, location, breeding characteristics, food sources, species, and gender.
     * The initial food level is set based on the provided food sources.
     * @param randomAge If true, the animal's age is randomly set; otherwise, it starts at 0.
     * @param location The animal's initial location on the field.
     * @param breedingAge The minimum age at which the animal can breed.
     * @param maxAge The maximum age the animal can live to.
     * @param breedingProbability The probability of the animal breeding in a given time step.
     * @param maxLitterSize The maximum number of offspring the animal can have at once.
     * @param foodSources A HashMap containing the animal's food sources and their nutritional values.
     * @param species The Class object representing the animal's species.
     * @param gender The animal's gender.
     * @param simulator The simulator instance managing this animal.
     */
    public Animal(boolean randomAge,
                  Location location,
                  int breedingAge,
                  int maxAge,
                  double breedingProbability,
                  int maxLitterSize,
                  HashMap<String, Integer> foodSources,
                  Class<? extends Animal> species,
                  Gender gender,
                  Simulator simulator

    ) {
        super(location, simulator);

        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProbability;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.FOOD_SOURCES = foodSources;
        this.SPECIES = species;
        this.gender = gender;

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }

        // Initialise food level based on food sources.
        this.foodLevel = 0;
        if (FOOD_SOURCES != null && !FOOD_SOURCES.isEmpty()) {
            this.foodLevel = FOOD_SOURCES.values().iterator().next(); // Get the first food source's value.
        }
    }

    /**
     * Returns the gender of the animal.
     * @return The animal's gender (MALE or FEMALE).
     */
    protected Gender getGender() {
        return gender;
    }

    /**
     * Simulates one step of the animal's actions.  This includes incrementing age, decreasing food level,
     * potentially giving birth, finding food, moving, and possibly dying (either from hunger, old age, or
     * if it cannot move).
     * @param currentField The current state of the simulation field.
     * @param nextFieldState The field representing the next state of the simulation, being built up.
     */
    protected void act(Field currentField, Field nextFieldState) {
        incrementAge();

        handleDisease(); // Handle disease progression or death
        if (!isAlive()) return; //Don't act if the animal is already dead

        spreadDisease(currentField); // Attempt to spread disease

        if (!FOOD_SOURCES.isEmpty()) {
            decreaseFoodLevel();
        }

        if(!isAlive()) {
            return; // Don't act if the animal is already dead.
        }

        List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());
        if(! freeLocations.isEmpty()) {
            giveBirth(nextFieldState, freeLocations, currentField); // Pass currentField for breeding checks
        }
        // Move towards a source of food if found.
        Location nextLocation = findFood(currentField);
        if(nextLocation == null && ! freeLocations.isEmpty()) {
            // No food found - try to move to a free location.
            nextLocation = freeLocations.remove(0);
        }
        // See if it was possible to move.
        if(nextLocation != null) {
            setLocation(nextLocation);
            nextFieldState.placeEntity(this, nextLocation);
        } else {
            // Could not move (no free locations and no food found).
            setDead();
        }
    }

    /**
     * Infect the animal with a disease.
     * An animal can only become infected if it's currently healthy (no existing disease).
     *
     * @param newDisease The disease to infect the animal with
     */
    public void infect(Disease newDisease) {
        if (disease == null) {  // Only get infected if currently healthy
            this.disease = newDisease;
            this.timeInfected = 0;
        }
    }


    /**
     * Get the current disease affecting this animal, if any.
     *
     * @return The disease object, or null if the animal is healthy
     */
    public Disease getDisease() {
        return this.disease;
    }

    /**
     * Handles disease progression, recovery, or death on each time step.
     * This method should be called during each step of the simulation
     * to update the animal's disease state.
     */
    protected void handleDisease() {
        if (disease != null) {
            timeInfected++;

            // Check if the animal recovers
            if (disease.isCured(timeInfected)) {
                disease = null; // The animal recovers
                return;
            }

            // If the disease is lethal and the duration is reached, the animal dies
            if (disease.isLethal() && timeInfected >= disease.infectionDuration) {
                setDead();
            }
        }
    }

    /**
     * Attempts to spread disease to adjacent animals in the field.
     * This simulates disease transmission through proximity.
     *
     * @param field The field containing this animal and potential infection targets
     */
    protected void spreadDisease(Field field) {
        if (disease != null) {
            for (Location loc : field.getAdjacentLocations(getLocation())) {
                Entity entity = field.getEntityAt(loc);
                if (entity instanceof Animal otherAnimal && otherAnimal.disease == null) {
                    if (disease.spreads()) {
                        otherAnimal.infect(disease);
                    }
                }
            }
        }
    }


    /**
     * Increments the animal's age by one simulation step. If the animal exceeds its maximum age, it dies.
     */
    protected void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead(); // The animal dies of old age.
        }
    }

    /**
     * Decreases the animal's food level, simulating hunger. If the food level reaches zero or below,
     * the animal dies of starvation.
     */
    protected void decreaseFoodLevel() {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead(); // The animal dies of starvation.
        }
    }

    /**
     * Searches for food in adjacent locations. The animal will consume the first valid food source found,
     * increasing its food level accordingly. Includes logic to potentially avoid eating
     * members of species with low populations.
     * @param field The current state of the simulation field.
     * @return The location of the food source if found, {@code null} otherwise.
     */
    protected Location findFood(Field field) {
        if(FOOD_SOURCES.isEmpty()) {
            return null; // Return null if the animal has no food sources.
        }
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null; // Initialise the location of the found food to null.
        while(foodLocation == null && it.hasNext()) { // Iterate through adjacent locations until food is found or all locations are checked.
            Location loc = it.next();
            Entity entity = field.getEntityAt(loc); // Gets the entity at the current location.
            for(String foodSource : FOOD_SOURCES.keySet()) { // Iterate through the animal's food sources.
                if(entity != null && foodSource.equals(entity.getClass().getSimpleName())) { // Check that the entity exists and can be eaten.
                    boolean shouldEat = true; // Flag to indicate if the animal should eat.

                    if (entity instanceof Animal targetAnimal) { // Check if target is an animal
                        // Check animal population
                        int males = field.getMaleCount(targetAnimal.getClass());
                        int females = field.getFemaleCount(targetAnimal.getClass());
                        if (males < 5 || females < 5) { // If population is small
                            shouldEat = rand.nextDouble() < 0.05; // Only eat 5% of the time
                        }
                    }
                    else if (entity instanceof Plant targetPlant) { // Check if target is a plant
                        // Check plant population
                        int plantCount = field.getPlantCount(targetPlant.getClass());
                        if (plantCount < 5) { // If population is small
                            shouldEat = rand.nextDouble() < 0.05; // Only eat 5% of the time
                        }
                    }

                    if (shouldEat) { // If the animal should eat
                        entity.setDead(); // Kill the target entity
                        foodLevel = FOOD_SOURCES.get(foodSource); // Increase food level
                        foodLocation = loc; // Remember the location of the food
                        break; // Stop searching after finding the first food source.
                    }
                }
            }
        }
        return foodLocation; // Returns location of food.
    }

    /**
     * Attempts to give birth to new animals of the same species. Births occur in free adjacent
     * locations, up to the maximum litter size, and are dependent on the result of the `breed` method.
     * @param nextFieldState The field representing the next state of the simulation.
     * @param freeLocations A list of free adjacent locations where offspring can be placed.
     * @param currentField The current state of the simulation field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField) { // Modify giveBirth to accept currentField
        int births = breed(currentField); // Determine the number of births.
        if(births > 0) {
            for(int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                try {
                    Animal young = SPECIES.getDeclaredConstructor(boolean.class, Location.class, Gender.class, Simulator.class) // Add Gender to constructor call
                            .newInstance(false, loc, Gender.getRandomGender(), simulator); // Pass random gender
                    nextFieldState.placeEntity(young, loc);
                } catch (Exception e) {
                    System.err.println("Failed to create new " + SPECIES.getSimpleName());
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Determines the number of offspring an animal will produce in the current time step, based on
     * its ability to breed and a random probability.
     * @param field The current state of the simulation field.
     * @return The number of births (0 if the animal cannot breed or the probability check fails).
     */
    protected int breed(Field field) {
        int births = 0;
        if(canBreed(field) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Determines if the animal can breed in the current step.  An animal can breed if it has reached
     * breeding age and there is a suitable mate of the opposite gender in an adjacent location.
     * @param field The current state of the simulation field.
     * @return `true` if the animal can breed, `false` otherwise.
     */
    protected boolean canBreed(Field field) {
        if (age >= BREEDING_AGE) {
            List<Location> adjacentLocations = field.getAdjacentLocations(getLocation());
            for (Location loc : adjacentLocations) {
                Entity neighbor = field.getEntityAt(loc);
                if (neighbor != null &&
                        neighbor.getClass() == this.getClass() &&
                        ((Animal) neighbor).getGender() != this.gender &&
                        neighbor.isAlive()) {
                    return true; // Found a suitable mate.
                }
            }
        }
        return false; // No mate found or not old enough.
    }
}
