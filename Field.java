import java.util.*;

/**
 * Represents a rectangular grid of field positions. Each position is able to store a single
 * entity (e.g., an animal or a plant).  Provides methods for managing entities within the field,
 * including placing, retrieving, and moving them.
 *
 * @author David J. Barnes and Michael Kölling and Mahdi Razzaque
 * @version 10.02.2025
 */
public class Field {

    private static final Random rand = Randomizer.getRandom(); // A random number generator for providing random locations.
    private final int depth, width; // The dimensions of the field.
    private final Map<Location, Entity> field = new HashMap<>(); // Animals mapped by location.
    private final List<Entity> entity = new ArrayList<>(); // List of all entities in the field

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width) {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Places an entity at the specified location in the field. If another entity already
     * occupies that location, it is removed (replaced) by the new entity.
     * @param anEntity The entity to be placed.
     * @param location The location where the entity should be placed.
     */
    public void placeEntity(Entity anEntity, Location location) {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            entity.remove(other); // Remove any existing entity at the location.
        }
        field.put(location, anEntity); // Place the new entity in the field.
        entity.add(anEntity); // Add entity to list.
    }

    /**
     * Returns the entity at the specified location in the field.
     * @param location The location to check.
     * @return The entity at the given location, or `null` if the location is empty.
     */
    public Entity getEntityAt(Location location) {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location) {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Entity anEntity = field.get(next);
            if(anEntity == null) {
                free.add(next);
            }
            else if(!anEntity.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location) {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Prints out the current statistics of the field, including the number of each type of animal and plant.
     * The output is formatted into tables for easy readability, displaying total counts as well as
     * the number of males and females for each animal type.
     */
    public void fieldStats() {
        int numWolfMales = 0, numWolfFemales = 0;  // Counters for male and female wolves
        int numBobcatMales = 0, numBobcatFemales = 0;  // Counters for male and female bobcats
        int numSquirrelMales = 0, numSquirrelFemales = 0;  // Counters for male and female squirrels
        int numGrouseMales = 0, numGrouseFemales = 0;  // Counters for male and female grouse
        int numSeeds = 0;  // Counter for seeds
        int numBerries = 0;  // Counter for berries

        // Iterate through all entities in the field
        for (Entity anEntity : field.values()) {
            if (!anEntity.isAlive()) continue;

            switch (anEntity) {
                case Wolf wolf -> {
                    if (wolf.getGender() == Gender.MALE) {
                        numWolfMales++;
                    } else {
                        numWolfFemales++;
                    }
                }
                case Bobcat bobcat -> {
                    if (bobcat.getGender() == Gender.MALE) {
                        numBobcatMales++;
                    } else {
                        numBobcatFemales++;
                    }
                }
                case Squirrel squirrel -> {
                    if (squirrel.getGender() == Gender.MALE) {
                        numSquirrelMales++;
                    } else {
                        numSquirrelFemales++;
                    }
                }
                case Grouse grouse -> {
                    if (grouse.getGender() == Gender.MALE) {
                        numGrouseMales++;
                    } else {
                        numGrouseFemales++;
                    }
                }
                case Seeds seeds -> numSeeds++;
                case Berries berries -> numBerries++;
                default ->
                    // Warn if an unexpected entity type is encountered
                        System.err.println("Warning: Unexpected entity type in field: " + anEntity.getClass().getSimpleName());
            }
        }

        String headerSeparator = "+-----------------+----------+--------+--------+\n";  // Separator for the animal table
        String rowFormat = "| %-15s | %-8s | %-6s | %-6s |\n"; // Format string for the animal table rows

        System.out.print(headerSeparator);
        System.out.printf("| %-15s | %-8s | %-6s | %-6s |\n", "Animal", "Total", "Male", "Female");
        System.out.print(headerSeparator);

        // Animals table
        System.out.printf(rowFormat, "Wolves", numWolfMales + numWolfFemales, numWolfMales, numWolfFemales);
        System.out.printf(rowFormat, "Bobcats", numBobcatMales + numBobcatFemales, numBobcatMales, numBobcatFemales);
        System.out.printf(rowFormat, "Squirrels", numSquirrelMales + numSquirrelFemales, numSquirrelMales, numSquirrelFemales);
        System.out.printf(rowFormat, "Grouse", numGrouseMales + numGrouseFemales, numGrouseMales, numGrouseFemales);

        System.out.print(headerSeparator);

        // Plants table
        String plantHeaderSeparator = "+-----------------+----------+\n"; // Separator for the plants table
        String plantRowFormat = "| %-15s | %-8d |\n"; // Format string for the plant table rows

        System.out.print(plantHeaderSeparator);
        System.out.printf("| %-15s | %-8s |\n", "Plant", "Total");
        System.out.print(plantHeaderSeparator);
        System.out.printf(plantRowFormat, "Seeds", numSeeds);
        System.out.printf(plantRowFormat, "Berries", numBerries);
        System.out.print(plantHeaderSeparator);
    }

    /**
     * Empty the field.
     */
    public void clear() {
        field.clear();
    }

    /**
     * Checks if the field contains at least one living animal
     * This method is used to determine if the ecosystem in the field is still viable in terms of having all necessary animal types.
     * The method iterates through a list of entities present in the field and sets boolean flags if each animal type is found.
     *
     * @return true if there is at least one living Wolf, Bobcat, Squirrel, and Grouse in the field; false otherwise.
     *         Returns false if the field is empty or if none of the animal types are present.
     */
    public boolean isViable() {
        boolean wolfFound = false;      // Flag to indicate if a wolf has been found.  Initialise to false.
        boolean bobcatFound = false;    // Flag to indicate if a bobcat has been found.  Initialise to false.
        boolean squirrelFound = false;  // Flag to indicate if a squirrel has been found. Initialise to false.
        boolean grouseFound = false;    // Flag to indicate if a grouse has been found.   Initialise to false.

        // Iterate through the entities list to find at least one of each animal
        Iterator<Entity> it = entity.iterator(); // Use the class's 'entities' list
        while (it.hasNext() && !(wolfFound && bobcatFound && squirrelFound && grouseFound)) { // Stop if all animals have been found
            Entity anEntity = it.next();

            if (anEntity instanceof Wolf wolf && wolf.isAlive()) {
                wolfFound = true; // Set flag to true if a living wolf is found
            } else if (anEntity instanceof Bobcat bobcat && bobcat.isAlive()) {
                bobcatFound = true; // Set flag to true if a living bobcat is found
            } else if (anEntity instanceof Squirrel squirrel && squirrel.isAlive()) {
                squirrelFound = true; // Set flag to true if a living squirrel is found
            } else if (anEntity instanceof Grouse grouse && grouse.isAlive()) {
                grouseFound = true; // Set flag to true if a living grouse is found
            }
        }
        return wolfFound && bobcatFound && squirrelFound && grouseFound; // Return true only if all animal types have been found alive
    }
    
    /**
     * Get the list of entity.
     */
    public List<Entity> getEntities() {
        return entity;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth() {
        return width;
    }
}
