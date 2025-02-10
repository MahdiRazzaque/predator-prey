/**
 * Represents the simulation's time, including hours, minutes, and days.
 * Provides methods for incrementing time and determining whether it is day or night.
 *
 * @author Mahdi Razzaque
 * @version 10.02.2025
 */
public class Time {
    private int hours;             // The current hour of the day (0-23).
    private int minutes;           // The current minute of the hour (0-59).
    private int days;              // The number of simulation days that have passed.
    private int timeStepIncrement; // Minutes to increment per simulation step.

    /**
     * Constructs a Time object with default values.
     * Sets the time to 00:00, day 0, and increments the time by 60 minutes (1 hour) per step.
     */
    public Time() {
        this(0, 0, 0, 60); // Default to start at 00:00 and increment by 60 minutes (1 hour) per step.
    }

    /**
     * Constructs a Time object with specified starting values and time step increment.
     *
     * @param startHour         The initial hour (0-23).
     * @param startMinute       The initial minute (0-59).
     * @param startDay          The initial day (typically 0).
     * @param timeStepIncrement The number of minutes to increment the time with each step.
     */
    public Time(int startHour, int startMinute, int startDay, int timeStepIncrement) {
        this.hours = startHour; // Set the initial hour.
        this.minutes = startMinute; // Set the initial minute.
        this.days = 0; // Initialise the day counter to 0.
        this.timeStepIncrement = timeStepIncrement; // Set the time step increment.
    }

    /**
     * Increments the time based on the `timeStepIncrement`.
     * This method updates the minutes, hours, and days, handling rollovers as needed.
     */
    public void incrementTime() {
        minutes += timeStepIncrement;   // Add the time step increment to the current minutes.
        hours += minutes / 60;          // Add any overflow from minutes to hours.
        days += hours / 24;             // Add any overflow from hours to days.
        minutes %= 60;                  // Keep minutes within the range 0-59.
        hours %= 24;                    // Keep hours within the range 0-23.
    }

    /**
     * Returns the current hour.
     *
     * @return The current hour (0-23).
     */
    public int getHour() {
        return hours;  // Return the current hour.
    }

    /**
     * Returns the current minute.
     *
     * @return The current minute (0-59).
     */
    public int getMinute() {
        return minutes;  // Return the current minute.
    }

    /**
     * Returns the number of days that have passed.
     *
     * @return The number of days.
     */
    public int getDays() {
        return days;  // Return the number of days.
    }

    /**
     * Returns the time formatted as a string (HH:MM).
     *
     * @return The formatted time string.
     */
    public String getFormattedTime() {
        return String.format("%02d:%02d", hours, minutes);  // Return the formatted time string.
    }

    /**
     * Determines if it is currently day.
     * Day is defined as being between 6:00 and 18:00 (exclusive).
     *
     * @return true if it is day, false otherwise.
     */
    public boolean isDay() {
        return hours >= 6 && hours < 18;  // Return true if the current hour is between 6 and 18, false otherwise.
    }

    /**
     * Determines if it is currently night.
     * Night is defined as the opposite of day.
     *
     * @return true if it is night, false otherwise.
     */
    public boolean isNight() {
        return !isDay();  // Return the opposite of isDay().
    }
}
