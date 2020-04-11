import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Salmon.
 * Salmons age, eat Seaweed, move, breed, and die.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public class Salmon extends Animal
{
    // Characteristics shared by all Salmons (class variables).

    // The age at which a Salmon can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a Salmon can live.
    private static final int MAX_AGE = 10;
    // The likelihood of a Salmon breeding.
    private static final double BREEDING_PROBABILITY = 0.14;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single seaweed. In effect, this is the
    // number of steps a Salmon can go before it has to eat again.
    private static final int FOOD_VALUE = 8;

    // Individual characteristics (instance fields).

    // The Salmon's age.
    private int age;
    // The Salmon's food level, which is increased by eating sardines and scubadivers.
    private int foodLevel;


    /**
     * Create a new Salmon. A Salmon may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Salmon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
    }

    /**
     * This is what the Salmon does most of the time - it runs
     * around and eats plants. Sometimes it will breed or die of old age.
     * @param newSalmons A list to return newly born Salmons.
     * @param time Time: day or night.
     * @param weather Current weather condition.
     */
    public void act(List<Animal> newSalmons, boolean time, String weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(weather != "freezing" && isAlive()){
                giveBirth(newSalmons);
                if(newLocation == null) {
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Salmon's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Shark more hungry. This could result in the Shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this Salmon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSalmons A list to return newly born Salmons.
     */
    private void giveBirth(List<Animal> newSalmons)
    {
        // New Salmons are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Salmon young = new Salmon(false, field, loc);
            newSalmons.add(young);
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
     * A Salmon can breed if it has reached the breeding age.
     * @return true if the Salmon can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live Seaweed is eaten.
     * Seaweed can infect Salmon, but it doesn't kill Salmon.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Seaweed) {
                Seaweed seaweed = (Seaweed) plant;

                // eating seaweed can infect Salmon.
                if(seaweed.isAlive() && seaweed.getInfection()) {
                    this.isInfected = true;
                    seaweed.setDead();
                }
                else if(seaweed.isAlive()){
                    seaweed.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
}
