import java.util.HashMap;

public class Grouse extends Animal{
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 130;
    private static final double BREEDING_PROBABILITY = 0.09;
    private static final int MAX_LITTER_SIZE = 6;

    public Grouse(boolean randomAge, Location location, Gender gender) {
        super(randomAge, location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE,
                createFoodSources(), Grouse.class, gender);
    }

    private static HashMap<String, Integer> createFoodSources() {
        HashMap<String, Integer> foodSources = new HashMap<>();
        return foodSources;
    }
}
