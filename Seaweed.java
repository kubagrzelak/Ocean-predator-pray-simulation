import java.util.List;
import java.util.Random;


/**
 * A simple model of a Seaweed.
 * Seaweed grow (age) and breed.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public class Seaweed extends Plants
{
    // Characteristics shared by all Seaweed (class variables).

    // The age at which a Seaweed can start to breed.
    private static final int BREEDING_AGE = 5;
    // The likelihood of a Seaweed breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Probability that the seaweed is infected.
    private static final double INFECTION_PROBABILITY = 0.0005;

    // Individual characteristics (instance fields).

    // The Seaweed's age.
    private int age;

    /**
     * Create a new Seaweed. A Seaweed may be created with age
     * zero (a new born) or with a random age.
     *
     * Seaweed can be also infected by the disease.
     *
     * @param randomAge If true, the Seaweed will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seaweed(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(5);
        }
        if(rand.nextDouble() <= INFECTION_PROBABILITY) {
            isInfected = true;
        }
    }

    /**
     * This is what the Seaweed does most of the time - it gets older.
     * Sometimes it will breed (only on a sunny weather) or die of old age.
     * @param newSeaweeds A list to return newly born Seaweeds.
     * @param time: day or night.
     * @param weather Current weather condition.
     */
    public void act(List<Plants> newSeaweeds, boolean time, String weather)
    {
        incrementAge();

        // Seaweed breeds only when it's sunny weather.
        if(isAlive() && weather == "sunny") {
            giveBirth(newSeaweeds);
        }
    }

    /**
     * Increase the age.
     * This cannot result in the Seaweed's death.
     */
    private void incrementAge()
    {
        age++;
    }

    /**
     * Check whether or not this Seaweed is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeaweeds A list to return newly born Seaweeds.
     */
    private void giveBirth(List<Plants> newSeaweeds)
    {
        // New Seaweeds are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seaweed young = new Seaweed(false, field, loc);
            newSeaweeds.add(young);
        }
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
     * A Seaweed can breed if it has reached the breeding age.
     * @return true if the Seaweed can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
