/**
 * This class represents the Flu disease that extends the Disease class.
 *
 *  * @author  Ozgur Dorunay
 *  * @version 18.02.2025
 */

// Constructor that initializes the Flu disease with its characteristics.
public class Flu extends Disease {
    public Flu() {
        super("Flu", 10, 0.3, false);  // Lasts 10 steps, 30% chance of spreading, not lethal
    }
}
