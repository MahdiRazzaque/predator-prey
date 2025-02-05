import java.util.HashMap;

public class Squirrel extends Animal {
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 120;
    private static final double BREEDING_PROBABILITY = 0.08;
    private static final int MAX_LITTER_SIZE = 4;

    public Squirrel(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Squirrel.class, gender, simulator);
    }

    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();
        return foodSources;
    }
}
