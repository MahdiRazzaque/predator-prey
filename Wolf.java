import java.util.HashMap;

public class Wolf extends Animal {
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 140;
    private static final double BREEDING_PROBABILITY = 0.05;
    private static final int MAX_LITTER_SIZE = 6;

    public Wolf(boolean randomAge, Location location) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Wolf.class);
    }

    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();
        foodSources.put("Squirrel", 10);
        foodSources.put("Grouse", 6);
        return foodSources;
    }
}
