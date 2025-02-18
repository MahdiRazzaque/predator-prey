import java.util.List;

/**
 * Abstract class representing a plant within the simulation. Extends the `Entity` class,
 * adding plant-specific attributes like growth rate, reproduction rate, lifespan, and growth stages.
 * Plant classes should inherit from this class.
 *
 * @author Ozgur Dorunay and Mahdi Razzaque
 * @version 10.02.2025
 */

public class Plant extends Entity{
    protected final String name; // The name of the plant.
    protected final int growthRate; // The rate at which the plant grows.
    protected final int reproductionRate; // The rate at which the plant reproduces.
    protected final int lifespan; // The lifespan of the plant in days.
    protected final int spreadRate; // The rate at which the plant spreads.
    protected int age; // The current age of the plant.
    protected final int growthStartHour; // The hour when the plant starts growing.
    protected final int growthEndHour; // The hour when the plant stops growing.
    protected int growthStage; // The current growth stage of the plant.
    protected final Class<? extends Plant> PLANT_TYPE; // The type of the plant.
    protected Time time; // The time of the simulation.

    // Constructor that initializes the Plant with its characteristics and environment.
    public Plant(String name,
                 int growthRate,
                 int reproductionRate,
                 int lifespan,
                 int spreadRate,
                 int growthStartHour,
                 int growthEndHour,
                 int growthStage ,
                 Location location,
                 Simulator simulator,
                 Time time,
                 Class<? extends Plant> plantType) {
        super (location, simulator);
        this.name = name;
        this.growthRate = growthRate;
        this.reproductionRate = reproductionRate;
        this.lifespan = lifespan;
        this.spreadRate = spreadRate;
        this.age = 0;
        this.time = time;
        this.growthStartHour = growthStartHour;
        this.growthEndHour = growthEndHour;
        this.growthStage = 0;
        this.PLANT_TYPE = plantType;
    }

    /**
     * Plants grow, reproduce and die based on different properties
     * @param currentField The field currently occupied
     * @param nextFieldState The updated field
     */
    @Override
    protected void act(Field currentField, Field nextFieldState) {
        age++;

        //Check if plant should die
        if (lifespan != -1 && age >= lifespan) {
            setDead();
            return;
        }

        //Check if it's the correct time for growth
        if (canGrow() && age % growthRate == 0){
            grow();
        }

        //Reproduce at reproduction interval
        if (age % reproductionRate == 0) {
            reproduce(currentField, nextFieldState);
        }


        //Plants remain in same location
        nextFieldState.placeEntity(this, getLocation());
    }

    /**
     * Handles plant growth at the specified interval
     */
    private void grow() {
        growthStage++;
    }

    /**
     * Determines if the plant can grow at the current time.
     * @return {@code true} if the plant can grow, {@code false} otherwise.
     */
    protected boolean canGrow() {
        int currentHour = time.getHour();
        if (growthStartHour < currentHour) {
            return currentHour >= growthStartHour && currentHour < growthEndHour;
        }
        else {
            return currentHour >= growthStartHour || currentHour < growthEndHour;
        }
    }



    /**
     * Attempts to reproduce by creating new plants in available locations.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    private void reproduce(Field currentField, Field nextFieldState) {
        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
        int spreadCount = Math.min(freeLocations.size(), spreadRate); // Ensure we don't exceed available free locations

        for (int i = 0; i < spreadCount; i++) {
            Location newLocation = freeLocations.remove(0);
            try {
                Plant newPlant = PLANT_TYPE.getDeclaredConstructor(Location.class, Simulator.class, Time.class)
                        .newInstance(newLocation, simulator, time);
                nextFieldState.placeEntity(newPlant, newLocation);
            } catch (Exception e) {
                System.err.println("Failed to create new " + PLANT_TYPE.getSimpleName());
            }
        }
    }


    /**
     * Returns the name of the plant.
     * @return The name of the plant.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the plant's growth stage.
     * @return The growth stage of the plant.
     */
    public int getGrowthStage() {
        return growthStage;
    }
}
