import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Wolf extends Entity{
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 140;
    private static final double BREEDING_PROBABILITY = 0.05;
    private static final int MAX_LITTER_SIZE = 6;
    private static final int SQUIRREL_FOOD_VALUE = 5;
    private static final int GROUSE_FOOD_VALUE = 6;
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating squirrels or grouse.
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param location  The location within the field.
     */
    public Wolf(boolean randomAge, Location location) {
        super(location);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        } else {
            age = 0;
        }

        int randInt1 = rand.nextInt(SQUIRREL_FOOD_VALUE);
        int randInt2 = rand.nextInt(GROUSE_FOOD_VALUE);
        foodLevel = Math.max(randInt1, randInt2);
    }

    /**
     * This is what the wolf does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     *
     * @param currentField   The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if (!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if (nextLocation == null && !freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if (nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeEntity(this, nextLocation);
            } else {
                // Overcrowding.
                setDead();
            }
        }
    }


    @Override
    public String toString() {
        return "Fox{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the wolf's death.
     */
    private void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this wolf more hungry. This could result in the wolf's death.
     */
    private void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     *
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Entity entity = field.getEntityAt(loc);
            if (entity instanceof Rabbit rabbit) {
                if (rabbit.isAlive()) {
                    rabbit.setDead();
                    foodLevel = SQUIRREL_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations) {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if (births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Fox young = new Fox(false, loc);
                nextFieldState.placeEntity(young, loc);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        } else {
            births = 0;
        }
        return births;
    }

    /**
     * A wolf can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }
}
