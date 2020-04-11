
/**
 *  This class controls a day time in the simulation.
 *  Sets time to day or night and how many steps of the simulation
 *  they should last.
 *
 * @author Daniel Koch and Jakub Grzelak
 * @version 2020.02.23
 */
public class Time
{
    // length of a day, how many steps is a day
    private static final int dayTime = 8;
    // length of a night, how many steps is a night
    private static final int nightTime = 4;
    // is it a day currently
    private boolean isDay;
    // how many steps the current day period has been lasting for.
    private int timer;

    /**
     * Constructor for objects of class Time
     * 
     * @param timeOfDay Sets to true if it's daytime and false if it's nighttime.
     */
    public Time(boolean timeOfDay) {
        isDay = timeOfDay;
        timer = 0;
    }

    /**
     *  @return isDay If it is a day (true) or night (false).
     */
    public boolean getIsDay(){
        return isDay;
    }
    
    /**
     *  Changes time of a day to a day or a night.
     */
    public void changeTime() {
        isDay = !isDay;
    }
    
    /**
     *  Resets a time in the simulation.
     */
    public void timeReset() {
        isDay = true;
        timer = 0;
    }
    
    /**
     *  Checks the time in the simulation.
     *  
     *  If a time of a day lasts more simulation steps than it is set to,
     *  then it changes the time (i.e. from a day to a night).
     */
    public void timeCheck() {
        timer++;
        
        if(isDay && timer > dayTime)
        {
            changeTime();
        }
        else if(!isDay && timer > (dayTime + nightTime))
        {
            changeTime();
            timer = 0;
        }
    }
}
