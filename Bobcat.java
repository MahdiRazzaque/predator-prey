import java.util.HashMap;

public class Bobcat extends Animal {
    private static final int BREEDING_AGE = 8;
    private static final int MAX_AGE = 130;
    private static final double BREEDING_PROBABILITY = 0.06;
    private static final int MAX_LITTER_SIZE = 3;

    public Bobcat(boolean randomAge, Location location, Gender gender, Simulator simulator) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Bobcat.class, gender, simulator);
    }

    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();
        foodSources.put("Squirrel", 20);
        return foodSources;
    }
}
