import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Orca.
 * Orcas age, move, eat salmons and scubadivers, breeds and die.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public class Orca extends Animal
{
    // Characteristics shared by all Orcas (class variables).

    // The age at which a Orca can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Orca can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a Orca breeding.
    private static final double BREEDING_PROBABILITY = 0.16;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single Salmon or Scubadiver. In effect, this is the
    // number of steps an Orca can go before it has to eat again.
    private static final int FOOD_VALUE = 14;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // A chance of dying after being infected.
    private static final double DEATH_CHANCE = 0.002;

    // Individual characteristics (instance fields).
    // The Orca's age.
    private int age;
    // The Orca's food level, which is increased by eating salmons or scubadivers.
    private int foodLevel;
    // The Orca's gender
    private boolean isMale;

    /**
     * Create a Orca. A Orca can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Orca will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Orca(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge)
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
        Random rd = new Random();
        this.isMale = rd.nextBoolean();
    }

    /**
     * This is what the Orca does most of the time: it hunts for
     * salmons and scubadivers. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newOrcas A list to return newly born Orcas.
     * @param time Time: day or night
     * @param weather Current weather condition.
     */
    public void act(List<Animal> newOrcas, boolean time, String weather)
    {
        incrementAge();
        incrementHunger();

        // orca is twice more active at night.
        int activity = 1;
        if(!time){
            activity = 2;
        }
        if(isAlive() && weather != "freezing") {
            giveBirth(newOrcas);

            // orca's activity loop  (is twice more active at night).
            for(int i=0; i<activity && isAlive(); i++){
              // Move towards a source of food if found.
              Location newLocation = findFood();
              if(newLocation == null && isAlive()) {
                  // No food found - try to move to a free location.
                  newLocation = getField().freeAdjacentLocation(getLocation());
              }
              // See if it was possible to move.
              if(newLocation != null && isAlive()) {
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
     * Increase the age. This could result in the Orca's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Orca more hungry. This could result in the Orca's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for salmons and scubadivers adjacent to the current location.
     * Only the first live Salmon or Scubadiver is eaten.
     * If Orca eats an infected salmon, then both the orca and the
     * salmon die.
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
          if(animal instanceof Salmon) {
              Salmon salmon = (Salmon) animal;
                if(salmon.isAlive() && salmon.getInfection()) {
                    isInfected = true;
                    if(rand.nextDouble() <= DEATH_CHANCE){
                        setDead();
                    }
                    salmon.setDead();
                }
                else if(salmon.isAlive()){
                    salmon.setDead();
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
     * Check whether or not this Orca is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * Orca only gives birth when meets oposite gender Orca.
     *
     * @param newOrcas A list to return newly born Orcas.
     */
    private void giveBirth(List<Animal> newOrcas)
    {
        if(genderCheck())
        {
          // New Orcas are born into adjacent locations.
          // Get a list of adjacent free locations.
          Field field = getField();
          List<Location> free = field.getFreeAdjacentLocations(getLocation());
          int births = breed();
          for(int b = 0; b < births && free.size() > 0; b++) {
              Location loc = free.remove(0);
              Orca young = new Orca(false, field, loc);
              newOrcas.add(young);
            }
        }
    }

    /**
     * Check if the Orca neighbors with another Orca
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
            if (animal instanceof Orca){
                Orca orca = (Orca) animal;
                if(this.isMale!=orca.getGender())
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
     * A Orca can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     *  @return Orca's gender.
     */
    public boolean getGender()
    {
       return isMale;
    }

}
