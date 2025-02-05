public class Time {
    private int hours;
    private int minutes;
    private int timeStepIncrement; // Minutes to increment per simulation step

    public Time() {
        this(0, 0, 60); // Default to start at 00:00 and increment by 60 minutes (1 hour) per step
    }

    public Time(int startHour, int startMinute, int timeStepIncrement) {
        this.hours = startHour;
        this.minutes = startMinute;
        this.timeStepIncrement = timeStepIncrement;
    }

    public void incrementTime() {
        minutes += timeStepIncrement;
        hours += minutes / 60;
        minutes %= 60;
        hours %= 24;
    }

    public int getHour() {
        return hours;
    }

    public int getMinute() {
        return minutes;
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hours, minutes);
    }

    public boolean isDay() {
        return hours >= 6 && hours < 18;
    }

    public boolean isNight() {
        return !isDay();
    }
}
