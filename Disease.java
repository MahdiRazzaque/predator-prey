import java.util.Random;

public abstract class Disease {
    protected String name;
    protected int infectionDuration;  // How long the disease lasts
    protected double transmissionRate; // Probability of infecting another animal
    protected boolean isLethal; // Does the disease kill the host?

    protected static final Random rand = new Random();

    public Disease(String name, int infectionDuration, double transmissionRate, boolean isLethal) {
        this.name = name;
        this.infectionDuration = infectionDuration;
        this.transmissionRate = transmissionRate;
        this.isLethal = isLethal;
    }

    public String getName() {
        return name;
    }

    public boolean isLethal() {
        return isLethal;
    }

    public boolean spreads() {
        return rand.nextDouble() < transmissionRate;
    }

    public boolean isCured(int timeInfected) {
        return timeInfected >= infectionDuration;
    }
}
