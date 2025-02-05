import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Animal extends Entity {
    protected int BREEDING_AGE;
    protected int MAX_AGE;
    protected double BREEDING_PROBABILITY;
    protected int MAX_LITTER_SIZE;
    protected HashMap<String, Integer> FOOD_SOURCES;
    protected static final Random rand = Randomizer.getRandom();
    protected final Class<? extends Animal> SPECIES;
    protected Gender gender;

    protected int age;
    protected int foodLevel;

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
        
        // Initialize food level based on food sources
        this.foodLevel = 0;
        if (FOOD_SOURCES != null && !FOOD_SOURCES.isEmpty()) {
            this.foodLevel = FOOD_SOURCES.values().iterator().next();
        }
    }

    protected Gender getGender() {
        return gender;
    }

    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    protected void act(Field currentField, Field nextFieldState) {
        incrementAge();
        if (!FOOD_SOURCES.isEmpty()) {
            decreaseFoodLevel();
        }

        if(!isAlive())
            return;

        List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());
        if(! freeLocations.isEmpty()) {
            giveBirth(nextFieldState, freeLocations, currentField);
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
            setDead();
        }
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    protected void incrementAge() {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    protected void decreaseFoodLevel() {
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
    protected Location findFood(Field field) {
        if(FOOD_SOURCES.isEmpty()) return null;
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Entity entity = field.getEntityAt(loc);
            for(String foodSource : FOOD_SOURCES.keySet()) {
                if(entity != null && foodSource.equals(entity.getClass().getSimpleName())) {
                    // System.out.println(this.getClass().getSimpleName() + " ate " + entity.getClass().getSimpleName());
                    entity.setDead();
                    foodLevel = FOOD_SOURCES.get(foodSource);
                    foodLocation = loc;
                    break;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField) { // Modify giveBirth to accept currentField
        int births = breed(currentField);
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
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(Field field) {
        int births = 0;
        if(canBreed(field) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
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
                    return true; // Found a suitable mate
                }
            }
        }
        return false; // No mate found or not old enough
    }
}
