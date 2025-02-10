/**
 * Main class to start the predator-prey simulation.
 * This class initialises and runs the simulation.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Main {
    public static Simulator simulator;
    public static void main(String[] args) {
        simulator = new Simulator();
        simulator.runLongSimulation();
    }
}
