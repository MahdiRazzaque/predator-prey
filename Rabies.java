/**
 * Represents the rabies diesease in the simulation, a specific type of Disease.
 *
 *  * @author  Ozgur Dorunay
 *  * @version 18.02.2025
 */
public class Rabies extends Disease {
    public Rabies() {
        super("Rabies", 20, 0.7, true);  // Lasts 20 steps, 70% chance of spreading, lethal
    }
}
