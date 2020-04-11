import java.util.Random;
import java.lang.String;

/**
 * This class controls the weather conditions in the simulation.
 * It sets the weather conditions and for how many steps of simulation
 * they should last.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23
 */
public class Weather
{
    // list of weather conditions(Testing purposes)
    /*
    private static final String[] weather = {
      "sunny", "stormy", "freezing"
    } ;
    */

    // Probability of a sunny weather.
    private static final double SUNNY_PROBABILITY = 0.70;
    // Probability of a stormy weather.
    private static final double STORMY_PROBABILITY = 0.2;
    // Probability of a freezing weather.
    private static final double FREEZING_PROBABILITY = 0.1;

    // Current weather in the simulation
    private String currentWeather;
    // weatherTimer counts for how many simulation steps a weather condition has been lasting.
    private int weatherTimer;
    // weatherPeriod sets for how many simulation steps a weather condition should last.
    private int weatherPeriod;

    /**
     * Constructor for objects of class Weather.
     *
     */
    public Weather()
    {
        weatherReset();
    }

    /**
     *  Randomly sets the weather conditions in the simulation.
     *  Chooses random Double(0 to 1) adn compares to weather condition probability
     */
    public void setWeather(){
        Random rand = new Random();
        //If random Double is less than or equal to condition probability then weather changes
        if(rand.nextDouble() <= STORMY_PROBABILITY){
            currentWeather = "stormy";
        }
        else if(rand.nextDouble() <= FREEZING_PROBABILITY){
            currentWeather = "freezing";
        }
        else
        {
            currentWeather = "sunny";
        }
    }

    /**
     *  @return currentWeather Current weather condition.
     */
    public String getCurrentWeather (){
        return currentWeather;
    }

    /**
     *  Sets for how many simulation steps a weather condition should last.
     */
    public void setWeatherPeriod(){
        Random rand = new Random();
        //max length for freezing weather is 6 steps
        if(currentWeather == "freezing"){
          weatherPeriod = rand.nextInt(6);
        //other weather conditions last for max amount of 14 steps
        }else{
          weatherPeriod = rand.nextInt(14);
        }
    }

    /**
     *  Checks if a weather condition should change.
     *
     *  If weather condition lasts more simulation steps than it should,
     *  then change a weather condition to a random new one and set its period.
     */
    public void weatherCheck (){
        weatherTimer++;
        if(weatherTimer > weatherPeriod)
        {
            weatherReset();
        }
    }

    /**
     *  Resets a weather condition and its period.
     */
    public void weatherReset(){
        setWeather();
        setWeatherPeriod();
        weatherTimer = 0;
    }
}
