import java.util.Random;

public enum Gender {
        MALE,
        FEMALE;

    public static Gender getRandomGender() {
        Random rand = Randomizer.getRandom(); // Assuming you have Randomizer accessible
        if (rand.nextDouble() < 0.5) {
            return MALE;
        } else {
            return FEMALE;
        }
    }
}
