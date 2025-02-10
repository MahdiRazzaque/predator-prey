import java.util.Random;

/**
 * Enum that represents the gender of an animal, either MALE or FEMALE.
 * Provides a method for randomly assigning a gender.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public enum Gender {
    MALE,
    FEMALE;

    /**
     * Returns a randomly selected gender (MALE or FEMALE).
     * The selection is determined by a random number generator, with each gender
     * having a 50% chance of being selected.
     *
     * @return MALE or FEMALE, chosen randomly.
     */
    public static Gender getRandomGender() {
        Random rand = Randomizer.getRandom();
        if (rand.nextDouble() < 0.5) {
            return MALE; // Return MALE if the random number is less than 0.5
        } else {
            return FEMALE; // Otherwise, return FEMALE
        }
    }
}
