import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Animal extends Entity {
    private static int BREEDING_AGE;
    private static int MAX_AGE;
    private static double BREEDING_PROBABILITY;
    private static int MAX_LITTER_SIZE;
    private static HashMap<String, Integer> FOOD_SOURCES;
    private static final Random rand = Randomizer.getRandom();

    private int age;
    private int foodLevel;

    public Animal(boolean randomAge,
                  Location location,
                  int breedingAge,
                  int maxAge,
                  double breedingProbability,
                  int maxLitterSize,
                  HashMap<String, Integer> foodSources
    ) {
        super(location);

        BREEDING_AGE = breedingAge;
        MAX_AGE = maxAge;
        BREEDING_PROBABILITY = breedingProbability;
        MAX_LITTER_SIZE = maxLitterSize;
        FOOD_SOURCES = foodSources;

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        int foodLevel = 0;
        for(int value : FOOD_SOURCES.values()) {
            foodLevel = Math.max(foodLevel, rand.nextInt(value));
        }
    }

    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState) {
        incrementAge();
        decreaseFoodLevel();

        if(!isAlive())
            return;

        List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());
        if(! freeLocations.isEmpty()) {
            giveBirth(nextFieldState, freeLocations);
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
        }
        else {
            //setDead();
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
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void decreaseFoodLevel() {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field) {
        if(FOOD_SOURCES.isEmpty()) return null;
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Entity entity = field.getEntityAt(loc);
            for(String foodSource : FOOD_SOURCES.keySet()) {
                if(entity.getClass().getName().equals(foodSource)) {
                    entity.setDead();
                    foodLevel = FOOD_SOURCES.get(foodSource);
                    foodLocation = loc;
                }
            }
//            if(entity instanceof Rabbit rabbit) {
//                if(rabbit.isAlive()) {
//                    rabbit.setDead();
//                    foodLevel = RABBIT_FOOD_VALUE;
//                    foodLocation = loc;
//                }
//            }
        }
        return foodLocation;
    }

    /**
     * Check whether this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Fox young = new Fox(false, loc);
                nextFieldState.placeEntity(young, loc);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
