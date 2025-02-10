/**
 * Abstract class representing a generic entity within the simulation.  This class provides
 * basic functionality common to all entities, such as tracking alive/dead status and location.
 * All entities in the simulation should inherit from this class.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
abstract public class Entity {
    private boolean alive; // Whether the entity is alive or not.
    private Location location; // The entity's position on the field.
    protected Simulator simulator; // The simulator instance this entity is part of.

    /**
     * Constructor for the Entity class. Initialises a new entity, setting it as alive and
     * storing its location and the simulator instance it belongs to.
     * @param location The entity's initial location on the field.
     * @param simulator The simulator instance managing this entity.
     */
    public Entity(Location location, Simulator simulator) {
        this.alive = true;  // Newly created entities are alive.
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
     * Checks whether the entity is currently alive.
     * @return `true` if the entity is alive, `false` otherwise.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Marks the entity as no longer alive and removes it from its current location.
     */
    protected void setDead() {
        alive = false;
        location = null;
    }

    /**
     * Returns the entity's current location.
     * @return The entity's current location on the field.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the entity's location to the specified location.
     * @param location The new location for the entity.
     */
    protected void setLocation(Location location) {
        this.location = location;
    }
}
