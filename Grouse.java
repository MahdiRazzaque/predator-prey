import java.util.HashMap;
import java.util.List;

public class Grouse extends Animal{
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 130;
    private static final double BREEDING_PROBABILITY = 0.09;
    private static final int MAX_LITTER_SIZE = 6;

    public Grouse(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Grouse.class, gender, simulator);
    }

    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();
        return foodSources;
    }

    @Override
    public void act(Field currentField, Field nextFieldState) {
        incrementAge();
        if (!FOOD_SOURCES.isEmpty()) {
            decreaseFoodLevel();
        }

        if (!isAlive())
            return;

        //Movement and finding food disabled at night. Birth rate heavily reduced
        if (simulator.getTime().isNight()) {
            if (rand.nextDouble() < 0.001) {
                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
                if (!freeLocations.isEmpty()) {
                    giveBirth(nextFieldState, freeLocations, currentField);
                }
            } else {
                Location currentLocation = getLocation();
                nextFieldState.placeEntity(this, currentLocation);
            }
            return;
        }
        
        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
        if (!freeLocations.isEmpty()) {
            giveBirth(nextFieldState, freeLocations, currentField);
        }
        // Move towards a source of food if found (Squirrels are herbivores, so findFood might be null)
        Location nextLocation = findFood(currentField); // For herbivores, findFood will likely be null
        if (nextLocation == null && !freeLocations.isEmpty()) {
            // No food found - try to move to a free location.
            nextLocation = freeLocations.remove(0);
        }
        // See if it was possible to move.
        if (nextLocation != null) {
            setLocation(nextLocation);
            nextFieldState.placeEntity(this, nextLocation);
        } else {
            setDead();
        }
    }
}
