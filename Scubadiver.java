import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Scubadiver.
 * Scubadivers age, move, breed, and die.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public class Scubadiver extends Animal
{
    // Characteristics shared by all Scubadivers (class variables).

    // The age at which a Scubadiver can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a Scubadiver can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a Scubadiver breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Scubadiver's age.
    private int age;
    // The Scubadiver's gender
    private boolean isMale;


    /**
     * Create a new Scubadiver. A Scubadiver may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Scubadiver will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Scubadiver(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        Random rd = new Random();
        this.isMale = rd.nextBoolean();
    }

    /**
     * This is what the Scubadiver does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     *
     *  Scubadiver only moves and breeds when the weather is sunny.
     *
     * @param newScubadivers A list to return newly born Scubadivers.
     * @param time Time - day or night
     * @param weather Current weather condition.
     */
    public void act(List<Animal> newScubadivers, boolean time, String weather)
    {
        incrementAge();
        if(isAlive() && time == true && weather == "sunny") {
            giveBirth(newScubadivers);
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Scubadiver's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Scubadiver is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * Scubadiver only gives birth when meets oposite gender Scubadiver.
     *
     * @param newScubadivers A list to return newly born Scubadivers.
     */
    private void giveBirth(List<Animal> newScubadivers)
    {
        if(genderCheck())
        {
            // New Scubadivers are born into adjacent locations.
            // Get a list of adjacent free locations.
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Scubadiver young = new Scubadiver(false, field, loc);
                newScubadivers.add(young);
            }
        }
    }

    /**
     * Check if the Scubadiver neighbors with another Scubadiver
     * of the opposite gender.
     */
    private boolean genderCheck()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal instanceof Scubadiver){
                Scubadiver scuba = (Scubadiver) animal;
                if(this.isMale!=scuba.getGender())
                    return true;
            }
        }
        return false;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Scubadiver can breed if it has reached the breeding age.
     * @return true if the Scubadiver can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     *  @return Gender of the Scubadiver.
     */
    public boolean getGender()
    {
       return isMale;
    }
}
