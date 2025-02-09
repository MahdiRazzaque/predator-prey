import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Field {
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // Animals mapped by location.
    private final Map<Location, Entity> field = new HashMap<>();
    // The entity.
    private final List<Entity> entity = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param anEntity The entity to be placed.
     * @param location Where to place the animal.
     */
    public void placeEntity(Entity anEntity, Location location)
    {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            entity.remove(other);
        }
        field.put(location, anEntity);
        entity.add(anEntity);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Entity getEntityAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
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
    public List<Location> getAdjacentLocations(Location location)
    {
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
     * Print out the number of foxes and rabbits in the field.
     */
    // In Field.java, replace the existing fieldStats() method with this updated version:
    /**
     * Print out the number of foxes and rabbits in the field, separated by gender.
     */
    public void fieldStats() {
        int numWolfMales = 0, numWolfFemales = 0;
        int numBobcatMales = 0, numBobcatFemales = 0;
        int numSquirrelMales = 0, numSquirrelFemales = 0;
        int numGrouseMales = 0, numGrouseFemales = 0;
        int numSeeds = 0;
        int numBerries = 0;

        for (Entity anEntity : field.values()) {
            if (anEntity.isAlive()) {
                if (anEntity instanceof Wolf wolf) {
                    if (wolf.getGender() == Gender.MALE) {
                        numWolfMales++;
                    } else {
                        numWolfFemales++;
                    }
                } else if (anEntity instanceof Bobcat bobcat) {
                    if (bobcat.getGender() == Gender.MALE) {
                        numBobcatMales++;
                    } else {
                        numBobcatFemales++;
                    }
                } else if (anEntity instanceof Squirrel squirrel) {
                    if (squirrel.getGender() == Gender.MALE) {
                        numSquirrelMales++;
                    } else {
                        numSquirrelFemales++;
                    }
                } else if (anEntity instanceof Grouse grouse) {
                    if (grouse.getGender() == Gender.MALE) {
                        numGrouseMales++;
                    } else {
                        numGrouseFemales++;
                    }
                } else if (anEntity instanceof Seeds seeds) {
                    numSeeds++;
                } else if (anEntity instanceof Berries berries) {
                    numBerries++;
                } else {
                    System.err.println("Warning: Unexpected entity type in field: " + anEntity.getClass().getSimpleName());

                }
            }
        }

        String headerSeparator = "+-----------------+----------+--------+--------+\n";
        String rowFormat = "| %-15s | %-8s | %-6s | %-6s |\n"; // Used for animals table

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
        String plantHeaderSeparator = "+-----------------+----------+\n"; // 2 columns
        String plantRowFormat = "| %-15s | %-8d |\n"; // Only 2 placeholders

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
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one rabbit and one fox in the field.
     * @return true if there is at least one rabbit and one fox in the field.
     */
    public boolean isViable() {
        boolean rabbitFound = false;
        boolean foxFound = false;
        boolean wolfFound = false;
        boolean bobcatFound = false;
        boolean squirrelFound = false;
        boolean grouseFound = false;

        Iterator<Entity> it = entity.iterator(); // Use the class's 'entities' list
        while (it.hasNext() && !(wolfFound && bobcatFound && squirrelFound && grouseFound)) {
            Entity anEntity = it.next();

            if (anEntity instanceof Wolf wolf && wolf.isAlive()) {
                wolfFound = true;
            } else if (anEntity instanceof Bobcat bobcat && bobcat.isAlive()) {
                bobcatFound = true;
            } else if (anEntity instanceof Squirrel squirrel && squirrel.isAlive()) {
                squirrelFound = true;
            } else if (anEntity instanceof Grouse grouse && grouse.isAlive()) {
                grouseFound = true;
            }
        }
        return wolfFound && bobcatFound && squirrelFound && grouseFound;
    }
    
    /**
     * Get the list of entity.
     */
    public List<Entity> getAnimals()
    {
        return entity;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
