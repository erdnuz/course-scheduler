package output;

import java.util.List;
import model.TimeSlot;

public class Output {
    public static void displaySchedule(List<TimeSlot> schedule) {
        TimeSlot[][] grid = new TimeSlot[5][9];
        for (TimeSlot slot : schedule) {
            grid[slot.getDay()][slot.getStartTime()] = slot;
        }

        int cellWidth = 14; // fixed width per cell
        System.out.println("Time Slot Schedule:");
        System.out.println("-".repeat(cellWidth * 5 + 9)); // header line
        System.out.printf("%-8s |", "Time");
        for (String day : new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"}) {
            System.out.printf(" %-12s|", day);
        }
        System.out.println();
        System.out.println("-".repeat(cellWidth * 5 + 9));

        String[] timeLabels = {"8:30", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30", "19:00", "20:30", "22:00"};

        for (int i = 0; i < timeLabels.length - 1; i++) {

            if (i < timeLabels.length - 2) {
                System.out.printf("%-8s |", timeLabels[i]);
                for (int j = 0; j < 5; j++) {
                    TimeSlot slot = grid[j][i];
                    TimeSlot prevSlot = i > 0 ? grid[j][i - 1] : null;
                    String content = "";

                    if (slot != null) {
                        content = slot.getCourseName() + " " + slot.getComp();
                    } else if (prevSlot != null && prevSlot.getExtended()) {
                        content = prevSlot.getCourseName() + " " + prevSlot.getComp();
                    }

                    if (content.length() > 12) content = content.substring(0, 12);
                    System.out.printf(" %-12s|", content);
                }
                System.out.println();
            }
            System.out.println("-".repeat(cellWidth * 5 + 9));
        }
    }
}
