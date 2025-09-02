

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Course;
import model.TimeSlot;
import output.Output;
import input.DataLoader;
import logic.RankOption;

/**
 * Main entry point for the course scheduling application.
 * <p>
 * This class allows the user to load course data either from a file or interactively
 * via console input. It computes all possible non-overlapping schedules combining
 * lectures, labs, and tutorials for all courses.
 */
public class Main {

    

    /**
     * Main method that drives the program.
     * <p>
     * Steps:
     * <ol>
     *     <li>Prompt user to load course data from a file or enter interactively</li>
     *     <li>Load courses using DataLoader or UserInput</li>
     *     <li>Compute all valid non-overlapping combinations of course time slots</li>
     *     <li>Print all valid scheduling options to the console</li>
     * </ol>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        List<Course> courses = null;
        Scanner scanner = new Scanner(System.in);

        while (courses == null) {
            System.out.print("\nFilepath (or type 'exit' to quit): ");
            String filepath = scanner.nextLine().trim();

            if (filepath.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program.");
                System.exit(0);
            }

            try {
                courses = new DataLoader(filepath).loadCourses();
            } catch (FileNotFoundException e) {
                // Try fallback in src/test/resources
                String fallback = "src/test/resources/" + filepath;
                try {
                    courses = new DataLoader(fallback).loadCourses();
                } catch (FileNotFoundException e2) {
                    System.out.println("File not found: " + filepath + " or " + fallback + ". Please try again.");
                } catch (IOException e2) {
                    System.out.println("Error reading file at fallback path: " + e2.getMessage() + ". Please try again.");
                } catch (Exception e2) {
                    System.out.println("Unexpected error at fallback path: " + e2.getMessage() + ". Please try again.");
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage() + ". Please try again.");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage() + ". Please try again.");
            }
        }

        


        scanner.close();

        System.out.println("\n[DEBUG] Loaded " + courses.size() + " courses:");
        for (Course c : courses) {
            System.out.println("  - " + c);
        }

        List<List<TimeSlot>> options = new ArrayList<>();

        for (Course c : courses) {
            System.out.println("\n[DEBUG] Processing course: " + c);
            List<List<TimeSlot>> courseOptions = c.getOptions();
            System.out.println("[DEBUG]   Found " + courseOptions.size() + " scheduling options for this course");

            List<List<TimeSlot>> newOptions = new ArrayList<>();

            if (options.isEmpty()) {
                System.out.println("[DEBUG]   No previous options — using course options directly");
                options = courseOptions;
            } else {
                for (List<TimeSlot> op : courseOptions) {
                    System.out.println("[DEBUG]   Checking course option: " + op);
                    for (List<TimeSlot> curr : options) {
                        boolean overlap = TimeSlot.checkOverlap(curr, op);
                        System.out.println("[DEBUG]     Comparing with existing option: " + curr);
                        System.out.println("[DEBUG]       Overlap? " + overlap);

                        if (!overlap) {
                            List<TimeSlot> newOption = new ArrayList<>(curr);
                            newOption.addAll(op);
                            newOptions.add(newOption);
                            System.out.println("[DEBUG]       Added new valid combination: " + newOption);
                        }
                    }
                }
            }

            // Update only if we actually found new combinations
            if (!options.isEmpty()) {
                options = newOptions.isEmpty() ? options : newOptions;
            }

            if (newOptions.isEmpty() && !options.equals(courseOptions)) {
                throw new Error("Course could not be added due to no non-overlapping schedules: " + c);
            }
        }

        // Sort options based on their ranking (best first)
        options.sort((o1, o2) -> Integer.compare(
                RankOption.optionScore(o2, true), // higher score first
                RankOption.optionScore(o1, true)
        ));

        // Print final options
        int i = 1;
        for (List<TimeSlot> o : options) {
            System.out.println("\nOption " + i++);
            Output.displaySchedule(o);
        }

    }
}
