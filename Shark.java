import java.util.List;
import java.util.Iterator;
import java.util.Random;


/**
 * A simple model of a Shark.
 * Sharks age, move, eat sardines and scubadivers, breed, and die.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public class Shark extends Animal
{
    // Characteristics shared by all Sharks (class variables).

    // The age at which a Shark can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a Shark can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a Shark breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single sardine or a single scubadiver. In effect, this is the
    // number of steps a Shark can go before it has to eat again.
    private static final int FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The Shark's age.
    private int age;
    // The Shark's food level, which is increased by eating sardines and scubadivers.
    private int foodLevel;

    /**
     * Create a Shark. A Shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location);
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
     * This is what the Shark does most of the time: it hunts for
     * sardines and scubadivers. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newSharks A list to return newly born Sharks.
     * @param time Time: day or night.
     * @param weather Current weather condition.
     */
    public void act(List<Animal> newSharks, boolean time, String weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive() && weather!="freezing") {
            giveBirth(newSharks);
            // Move towards a source of food if found.
            Location newLocation = findFood();
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

    /**
     * Increase the age. This could result in the Shark's death.
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
     * Look for sardines and scubadivers adjacent to the current location.
     * Only the first live Sardine or Scubadiver is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Sardine) {
                Sardine sardine = (Sardine) animal;
                if(sardine.isAlive()) {
                    sardine.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Scubadiver) {
                Scubadiver scubadiver = (Scubadiver) animal;
                if(scubadiver.isAlive()) {
                    scubadiver.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSharks A list to return newly born Sharks.
     */
    private void giveBirth(List<Animal> newSharks)
    {
        // New Sharks are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newSharks.add(young);
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
     * A Shark can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

}
