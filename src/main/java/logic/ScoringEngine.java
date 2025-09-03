package logic;

import java.util.ArrayList;
import java.util.List;

import logic.config.Config;
import logic.config.ConfigLoader;
import model.Component;
import model.TimeSlot;
import output.Output;

/**
 * ScoringEngine evaluates schedules based on configurable rules.
 *
 * Factors:
 * <ul>
 *   <li><b>Days off</b>: More days off = higher score.</li>
 *   <li><b>Gaps between classes</b>: Fewer gaps = higher score (configurable penalty per gap).</li>
 *   <li><b>Early/Late classes</b>: Configurable penalties based on time.</li>
 *   <li><b>Lecture-only scoring</b>: Optionally re-rank using only lectures and average the results.</li>
 *   <li><b>Labs/Tutorials</b>: Can be ignored in scoring if disabled in config.</li>
 * </ul>
 *
 * All weights and penalties are loaded from {@code config.json}.
 * Output is controlled by {@code verbose}.
 */
public class ScoringEngine {

    /**
     * Scores a schedule.
     *
     * @param schedule   The schedule (list of {@link TimeSlot}).
     * @param punishGap  Whether to apply gap penalties.
     * @param config     Configurable weights and penalties.
     * @return Score (higher = better).
     */
    private static int score(List<TimeSlot> schedule, boolean punishGap, Config config, boolean verbose) {
        int score = 0;

        if (verbose) {
            System.out.println("\n--- Scoring Schedule ---");
            Output.displaySchedule(schedule);
        }

        // --- Build grid (days x timeslots) ---
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
        int pointsDaysOff = daysOff * config.dayOffReward;
        score += pointsDaysOff;
        if (verbose) {
            System.out.println("Days off: " + daysOff + ", points: +" + pointsDaysOff);
        }

        // --- Factor 2: Gaps ---
        if (punishGap) {
            int gap = 0;
            for (int day = 0; day < 5; day++) {
                int last = -1;
                for (int t = 0; t < 9; t++) {
                    if (grid[day][t] != null) {
                        if (last != -1) {
                            gap += t - last - 1;
                        }
                        last = t;
                    }
                }
            }
            score += gap * config.gapPunishment; // punishment usually negative
            if (verbose) {
                System.out.println("Total gaps between classes: " + gap + " -> " 
                        + (gap * config.gapPunishment) + " points");
            }
        }

        // --- Factor 3: Early & Late classes ---
        for (int day = 0; day < 5; day++) {
            if (grid[day][0] != null) {
                score += config.timePunishments[0];
                if (verbose) {
                    System.out.println("Early class on day " + day + " at 8:30 -> " 
                            + config.timePunishments[0] + " points");
                }
            } else if (grid[day][1] != null) {
                score += config.timePunishments[1];
                if (verbose) {
                    System.out.println("Early class on day " + day + " at 10:00 -> " 
                            + config.timePunishments[1] + " points");
                }
            }
            if (grid[day][8] != null) {
                score += config.timePunishments[3];
                if (verbose) {
                    System.out.println("Late class on day " + day + " at 20:30 -> " 
                            + config.timePunishments[3] + " points");
                }
            } else if (grid[day][7] != null) {
                score += config.timePunishments[2];
                if (verbose) {
                    System.out.println("Late class on day " + day + " at 19:00 -> " 
                            + config.timePunishments[2] + " points");
                }
            }
        }

        // --- Factor 4: Lecture-only rerun ---
        if (config.doLectureOnlyRound) {
            List<TimeSlot> lecturesOnly = new ArrayList<>();
            boolean hasNonLecture = false;

            for (TimeSlot s : schedule) {
                if (s.getComp() == Component.LEC) {
                    lecturesOnly.add(s);
                } else {
                    hasNonLecture = true;
                }
            }

            if (!hasNonLecture) {
                if (verbose) {
                    System.out.println("No labs/tutorials, skipping lecture-only scoring.");
                    System.out.println("Total score: " + score);
                }
                return score;
            }

            int lectureOnlyScore = score(lecturesOnly, false, config, verbose);
            score = (int) ((1 - config.lectureOnlyWeight) * score
                         + config.lectureOnlyWeight * lectureOnlyScore);

            if (verbose) {
                System.out.println("Lecture-only score: " + lectureOnlyScore);
                System.out.println("Total score (weighted): " + score);
            }
        }

        return score;
    }

    /** Apply scoring with default config.json path. */
    public static int apply(List<TimeSlot> schedule, boolean verbose) {
        Config config = ConfigLoader.load("src/main/java/logic/config/config.json");
        return score(schedule, true, config, verbose);
    }

}
