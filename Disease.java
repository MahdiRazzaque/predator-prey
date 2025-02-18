import java.util.Random;

/**
 * An abstract representation of a disease that can affect animals in the simulation.
 * Each specific disease will inherit from this class and may override or extend its functionality.
 *
 *  @author Ozgur Dorunay
 *  @version 18.02.2025
 */


public abstract class Disease {
    protected String name; // The name of the disease
    protected int infectionDuration;  // How long the disease lasts
    protected double transmissionRate; // Probability of infecting another animal
    protected boolean isLethal; // Does the disease kill the host?

    protected static final Random rand = new Random();

    /**
     * Constructor for creating a new disease
     *
     * @param name The name of the disease
     * @param infectionDuration How many time steps the infection lasts
     * @param transmissionRate Probability from 0.0 to 1.0 of spreading to another animal
     * @param isLethal Whether the disease can be fatal
     */
    public Disease(String name, int infectionDuration, double transmissionRate, boolean isLethal) {
        this.name = name;
        this.infectionDuration = infectionDuration;
        this.transmissionRate = transmissionRate;
        this.isLethal = isLethal;
    }

    /**
     * @return The name of the disease
     */
    public String getName() {
        return name;
    }

    /**
     * @return Whether this disease can cause death
     */
    public boolean isLethal() {
        return isLethal;
    }

    /**
     * Determines whether the disease spreads during a particular interaction
     * based on the transmission rate
     *
     * @return true if the disease spreads, false otherwise
     */
    public boolean spreads() {
        return rand.nextDouble() < transmissionRate;
    }

    /**
     * Determines if the animal has fought off the infection based on time infected
     *
     * @param timeInfected The number of time steps the animal has been infected
     * @return true if the animal is now cured, false if still infected
     */
    public boolean isCured(int timeInfected) {
        return timeInfected >= infectionDuration;
    }
}
