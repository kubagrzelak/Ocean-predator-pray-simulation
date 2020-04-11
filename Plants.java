import java.util.List;

/**
 * A class representing shared characteristics of Plants.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public abstract class Plants
{
    // Whether the Plants is alive or not.
    private boolean alive;
    // The Plants's field.
    private Field field;
    // The Plants's position in the field.
    private Location location;
    // Is the plant infected.
    protected boolean isInfected;

    /**
     * Create a new Plants at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plants(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        isInfected = false;
    }

    /**
     * Make this Plants act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly born Plants.
     * @param time time - day or night.
     * @param weather Current weather condition.
     */
    abstract public void act(List<Plants> newPlants, boolean time, String weather);

    /**
     * Check whether the Plants is alive or not.
     * @return true if the Plants is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the Plants is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the Plants's location.
     * @return The Plants's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the Plants at the new location in the given field.
     * @param newLocation The Plants's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the Plants's field.
     * @return The Plants's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     *  Return the plants's infection state.
     *  @return If the plant is infected.
     */
    public boolean getInfection()
    {
       return isInfected;
    }
}
