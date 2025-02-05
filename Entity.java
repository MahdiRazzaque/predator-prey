abstract public class Entity {
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;

    protected Simulator simulator;

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Entity(Location location, Simulator simulator)
    {
        this.alive = true;
        this.location = location;
        this.simulator = simulator;
    }

    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract protected void act(Field currentField, Field nextFieldState);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
}
