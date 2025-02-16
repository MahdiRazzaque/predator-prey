import java.util.List;

public class Plant extends Entity{
    protected final String name;
    protected final int growthRate;
    protected final int reproductionRate;
    protected final int lifespan;
    protected final int spreadRate;
    protected int age;
    protected final int growthStartHour;
    protected final int growthEndHour;
    protected int growthStage;
    protected final Class<? extends Plant> PLANT_TYPE;
    protected Time time;

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
     * Determines if the plant can grow at the current time
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
     * Attempts to reproduce by creating new plants in available locations
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
     * Returns name of plant
     */
    public String getName() {
        return name;
    }

    /**
     * Returns plant's growth stage
     */
    public int getGrowthStage() {
        return growthStage;
    }
}
