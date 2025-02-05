import java.util.List;

public class Plant extends Entity{
    private final String name;
    private final int growthRate;
    private final int reproductionRate;
    private final int lifespan;
    private final int spreadRate;
    private int age;
    private Time time;
    private final int growthStartHour;
    private final int growthEndHour;
    private int growthStage;

    public Plant(String name,
                 int growthRate,
                 int reproductionRate,
                 int lifespan,
                 int spreadRate,
                 int growthStartHour,
                 int growthEndHour,
                 int growthStage,
                 Location location,
                 Simulator simulator) {
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

        //Spread to new locations at the spread rate interval
        if (age % spreadRate == 0) {
            spread (currentField, nextFieldState);
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
    private boolean canGrow() {
        int currentHour = time.getHour();
        if (growthStartHour < currentHour) {
            return currentHour >= growthStartHour && currentHour < growthEndHour;
        }
        else {
            return currentHour >= growthStartHour || currentHour < growthEndHour;
        }
    }

    /**
     * Attempts to spread the plant to nearby empty locations
     */
    private void spread(Field currentField, Field nextFieldState) {
        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());

        if (!freeLocations.isEmpty()) {
            Location newLocation = freeLocations.remove(0);
            Plant newPlant = new Plant(name, growthRate, reproductionRate, lifespan, spreadRate,
                    growthStartHour, growthEndHour, growthStage,
                    newLocation, simulator);
            nextFieldState.placeEntity(newPlant, newLocation);
        }
    }

    /**
     * Attempts to reproduce by creating new plants in available locations
     */
    private void reproduce(Field currentField, Field nextFieldState) {
        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());

        for (Location location : freeLocations) {
            Plant newPlant = new Plant(name, growthRate, reproductionRate, lifespan, spreadRate,
                    growthStartHour, growthEndHour, growthStage,
                    location, simulator);
            nextFieldState.placeEntity(newPlant, location);
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
