package logic;

import java.util.ArrayList;
import java.util.List;

import model.Component;
import model.TimeSlot;
import output.Output;

public class RankOption {

    /**
     * Scores a schedule based on multiple factors using a grid:
     * 1. Days off (more days off = higher score)
     * 2. Minimal waiting time between courses
     * 3. Ignore labs/tutorials in scoring
     * 4. No early classes
     * 5. No late classes
     *
     * Higher score = better schedule
     */
    public static int optionScore(List<TimeSlot> schedule, boolean punishGap) {
        int score = 0;
        System.out.println("\n--- Scoring Schedule ---");
        Output.displaySchedule(schedule);

        // --- Build grid ---
        TimeSlot[][] grid = new TimeSlot[5][9];
        for (TimeSlot slot : schedule) {
            grid[slot.getDay()][slot.getStartTime()] = slot;
            if (slot.getExtended()) {
                grid[slot.getDay()][slot.getStartTime() + 1] = slot;
            }
        }

        // --- Factor 1: Days off ---
        int daysWithClasses = 0;
        for (int day = 0; day < 5; day++) {
            boolean hasClass = false;
            for (int t = 0; t < 9; t++) {
                if (grid[day][t] != null) {
                    hasClass = true;
                    break;
                }
            }
            if (hasClass) daysWithClasses++;
        }
        int daysOff = 5 - daysWithClasses;
        int pointsDaysOff = daysOff * 3;
        score += pointsDaysOff;
        System.out.println("Days off: " + daysOff + ", points: +" + pointsDaysOff);

        if (punishGap) {
            // --- Factor 2: Minimal waiting time ---
            int gap = 0;
            for (int day = 0; day < 5; day++) {
                
                int last = -1;
                for (int t = 0; t < 9; t++) {
                    if (grid[day][t] != null) {
                        if (last != -1)  {
                            gap += t - last - 1;
                        }
                        last = t;
                    }
                }
            }
            score -= gap;
            System.out.println("Total gaps between classes: " + gap + " -> -" + gap + " points");

        }
        
        // --- Factor 4 & 5: Early and Late classes ---
        for (int day = 0; day < 5; day++) {
            if (grid[day][0] != null) {
                score -= 2;
                System.out.println("Early class on day " + day + " at 8:30 -> -2 points");
            } else if (grid[day][1] != null) {
                score -= 1;
                System.out.println("Early class on day " + day + " at 10:00 -> -1 point");
            }
            if (grid[day][8] != null) {
                score -= 2;
                System.out.println("Late class on day " + day + " at 20:30 -> -2 points");
            } else if (grid[day][7] != null) {
                score -= 1;
                System.out.println("Late class on day " + day + " at 19:00 -> -1 point");
            }
        }

        List<TimeSlot> lecturesOnly = new ArrayList<>();
        boolean flag = false;
        for (TimeSlot s : schedule) {
            if (s.getComp() == Component.LEC) {
                lecturesOnly.add(s);
            } else {
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("No labs/tutorials, skipping lec-only scoring.");
            System.out.println("Total score: " + score);
            return score;
        }
        int totalScore = (score + optionScore(lecturesOnly, false)) / 2;
        System.out.println("Total score (with lec-only averaged): " + totalScore);

        return totalScore;
    }
}
