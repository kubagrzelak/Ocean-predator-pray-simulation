import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Prey (Scubadiver, Sardine, Salmon), Predator (Shark, Orcas) and Plant (Seaweed).
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 100;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 60;

    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.01;
    // The probability that a orca will be created in any given grid position.
    private static final double ORCA_CREATION_PROBABILITY = 0.02;
    // The probability that a sardine will be created in any given grid position.
    private static final double SARDINE_CREATION_PROBABILITY = 0.05;
    // The probability that a salmon will be created in any given grid position.
    private static final double SALMON_CREATION_PROBABILITY = 0.04;
    // The probability that a scubadiver will be created in any given grid position.
    private static final double SCUBADIVER_CREATION_PROBABILITY = 0.02;
    // The probability that a seaweed will be created in any given grid position.
    private static final double SEAWEED_CREATION_PROBABILITY = 0.2;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plants> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private List<SimulatorView> views;
    // Creates object Time stating whether time of a day (day or night).
    private Time time = new Time(true);
    // Creates object Weather stating the weather conditions.
    private Weather weather = new Weather();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);

    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);

        views = new ArrayList<>();

        // Create a view of the state of each location in the field.
        SimulatorView view = new GridView(depth, width);
        view.setColor(Sardine.class, Color.YELLOW);
        view.setColor(Salmon.class, Color.ORANGE);
        view.setColor(Scubadiver.class, Color.RED);
        view.setColor(Shark.class, Color.GRAY);
        view.setColor(Orca.class, Color.BLACK);
        view.setColor(Seaweed.class, Color.GREEN);
        views.add(view);

        view = new GraphView(500, 150, 500);
        view.setColor(Sardine.class, Color.YELLOW);
        view.setColor(Salmon.class, Color.ORANGE);
        view.setColor(Scubadiver.class, Color.RED);
        view.setColor(Shark.class, Color.GRAY);
        view.setColor(Orca.class, Color.BLACK);
        view.setColor(Seaweed.class, Color.GREEN);
        views.add(view);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && views.get(0).isViable(field); step++) {
            simulateOneStep();
            delay(200);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Controls time of a day.
     * Controls weather conditions.
     * Iterate over the whole field updating the state of each
     * predator and prey.
     */
    public void simulateOneStep()
    {
        step++;

        // controls time (day or night).
        time.timeCheck();

        // controls weather conditions.
        weather.weatherCheck();

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, time.getIsDay(), weather.getCurrentWeather());
            if(! animal.isAlive()) {
                it.remove();
            }
        }

        // Provide space for newgrown plants.
        List<Plants> newPlants = new ArrayList<>();
        // Let all plants act.
        for(Iterator<Plants> it = plants.iterator(); it.hasNext(); ) {
            Plants plant = it.next();
            plant.act(newPlants, time.getIsDay(), weather.getCurrentWeather());
            if(! plant.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
        // Add the newly grown plants to the main lists.
        plants.addAll(newPlants);
        // Updates the view
        updateViews();
    }

    /**
     * Reset the simulation to a starting position.
     * Including reseting time of a day and weather.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();
        // Show the starting state in the view.
        updateViews();

        // Resets day time.
        time.timeReset();
        // Resets weather.
        weather.weatherReset();
    }

    /**
     * Update all existing views.
     */
    private void updateViews()
    {
        for (SimulatorView view : views) {
            view.showStatus(step, time.getIsDay(), field, weather.getCurrentWeather());
        }
    }

    /**
     * Randomly populate the field with Animals and Plants.
     * Animals (Orca, Shark, Salmon, Sardine, Scubadiver).
     * Plants (Seaweed).
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= ORCA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Orca orca = new Orca(true, field, location);
                    animals.add(orca);
                }
                else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    animals.add(shark);
                }
                else if(rand.nextDouble() <= SCUBADIVER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Scubadiver scubadiver = new Scubadiver(true, field, location);
                    animals.add(scubadiver);
                }
                else if(rand.nextDouble() <= SALMON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Salmon salmon = new Salmon(true, field, location);
                    animals.add(salmon);
                }
                else if(rand.nextDouble() <= SARDINE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Sardine sardine = new Sardine(true, field, location);
                    animals.add(sardine);
                }
                else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(true, field, location);
                    plants.add(seaweed);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }

}
