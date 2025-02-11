import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing
 * various entities such as wolves, bobcats, squirrels, grouse, seeds, and berries.
 * The simulation models the interactions between these entities over time.
 *
 * @author David J. Barnes and Michael Kölling and Mahdi Razzaque
 * @version 10.02.2025
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    private static final int DEFAULT_WIDTH = 120; // The default width of the simulation grid.
    private static final int DEFAULT_DEPTH = 80;  // The default depth of the simulation grid.

    private static final double WOLF_CREATION_PROBABILITY = 0.005;
    private static final double BOBCAT_CREATION_PROBABILITY = 0.02;
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.045;
    private static final double GROUSE_CREATION_PROBABILITY = 0.05;
    private static final double SEEDS_CREATION_PROBABILITY = 0.08;
    private static final double BERRIES_CREATION_PROBABILITY = 0.05;


    private Field field; // Represents the current state of the simulation field, containing all entities.
    private int step; // Represents the current simulation step or iteration.
    private final SimulatorView view; // Provides a graphical representation of the simulation.
    private Time time; // Represents the current time in the simulation.
    private Weather weather;

    /**
     * Constructs a simulation with default dimensions.
     * This constructor initialises the simulation field with a default depth and width,
     * and sets the initial time to 17:00 with a time step of 10 minutes.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH); // Initialise the field with default depth and width.
        time = new Time(17,0,0, 10); // Set the initial time to 17:00 with a time step of 10 minutes.
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        time = new Time();
        weather = new Weather();
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width, this);

        reset();
    }

    /**
     * Returns the current time of the simulation.
     *
     * @return The current Time object representing the simulation time.
     */
    public Time getTime() {
        return time; // Return the current time.
    }

    public Weather getWeather() {
        return weather;
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep() {
        time.incrementTime();
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Entity> entities = field.getEntities();
        for (Entity anEntity : entities) {
            anEntity.act(field, nextFieldState);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        populate();
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Gender gender = Gender.getRandomGender();

                if (rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Wolf wolf = new Wolf(true, location, gender, this);
                    field.placeEntity(wolf, location);
                } else if (rand.nextDouble() <= BOBCAT_CREATION_PROBABILITY) {
                    Bobcat bobcat = new Bobcat(true, location, gender, this);
                    field.placeEntity(bobcat, location);
                } else if (rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY) {
                    Squirrel squirrel = new Squirrel(true, location, gender, this);
                    field.placeEntity(squirrel, location);
                } else if (rand.nextDouble() <= GROUSE_CREATION_PROBABILITY) {
                    Grouse grouse = new Grouse(true, location, gender, this);
                    field.placeEntity(grouse, location);
                } else if (rand.nextDouble() <= SEEDS_CREATION_PROBABILITY) {
                    Seeds seeds = new Seeds(location, this, time);
                    field.placeEntity(seeds, location);
                } else if (rand.nextDouble() <= BERRIES_CREATION_PROBABILITY) {
                    Berries berries = new Berries(location, this, time);
                    field.placeEntity(berries, location);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
}
