package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a single time slot for a course component (lecture, lab, or tutorial).
 * Includes the start time, day of the week, and whether it is an extended session.
 */
public class TimeSlot {

    private int startTime;
    private boolean extended;
    private int day;
    private Component comp;
    private String courseName;

    /** Default constructor required by Jackson */
    public TimeSlot() {
    }

    /** Jackson-compatible constructor */
    @JsonCreator
    public TimeSlot(
            @JsonProperty("startTime") int startTime,
            @JsonProperty("day") int day,
            @JsonProperty("extended") boolean extended) {
        if (startTime < 0 || startTime > 8) {
            throw new IllegalArgumentException("""
                StartTime should be an int from 0 to 8.
                    8:30 -> 0
                    10:00 -> 1
                    11:30 -> 2
                    13:00 -> 3
                    14:30 -> 4
                    16:00 -> 5
                    17:30 -> 6
                    19:00 -> 7
                    20:30 -> 8
                """);
        }
        if (day < 0 || day > 4) {
            throw new IllegalArgumentException("""
                Day should be an int from 0 to 4.
                    Monday -> 0
                    Tuesday -> 1
                    Wednesday -> 2
                    Thursday -> 3
                    Friday -> 4
                """);
        }
        this.startTime = startTime;
        this.day = day;
        this.extended = extended;
    }

    

    // Getters and setters
    public Component getComp() {
        return comp;
    }

    public void setComp(Component comp) {
        this.comp = comp;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public boolean getExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {"8:30", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30", "19:00", "20:30", "22:00"};
        return days[day] + " " + times[startTime] + "-" + times[startTime + (extended ? 1 : 2)];
    }

    public boolean checkOverlap(TimeSlot other) {
        if (this.day != other.getDay()) return false;
        int diff = this.startTime - other.getStartTime();
        return diff == 0 || (diff == 1 && other.getExtended()) || (diff == -1 && this.extended);
    }

    public boolean checkOverlap(List<TimeSlot> others) {
        for (TimeSlot t : others) {
            if (this.checkOverlap(t)) return true;
        }
        return false;
    }

    /**
     * Checks whether any TimeSlot in the first list overlaps with any TimeSlot
     * in the second list.
     *
     * @param o1 the first list of TimeSlot objects
     * @param o2 the second list of TimeSlot objects
     * @return true if any TimeSlot in o1 overlaps with any in o2; false otherwise
     */
    public static boolean checkOverlap(List<TimeSlot> o1, List<TimeSlot> o2) {
        for (TimeSlot t : o1) {
            if (t.checkOverlap(o2)) {
                return true;
            }
        }
        return false;
    }
}
