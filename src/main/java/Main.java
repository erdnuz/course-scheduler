import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Course;
import model.TimeSlot;
import output.Output;
import input.DataLoader;
import logic.ScoringEngine;

/**
 * Main entry point for the course scheduling application.
 * 
 * Responsibilities:
 * - Load courses either from a file or interactively.
 * - Compute all valid non-overlapping schedules.
 * - Rank schedules based on configurable scoring rules.
 * - Display schedules to the user interactively.
 */
public class Main {

    public static void main(String[] args) {
        List<Course> courses = null;
        Scanner scanner = new Scanner(System.in);

        // --- Step 1: Prompt user to load courses ---
        while (courses == null) {
            System.out.print("\nEnter course data filepath (or type 'exit' to quit): ");
            String filepath = scanner.nextLine().trim();

            if (filepath.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program.");
                System.exit(0);
            }

            // Attempt to load courses from the given path
            try {
                courses = new DataLoader(filepath).loadCourses();
            } catch (FileNotFoundException e) {
                // Fallback to test resources folder
                String fallback = "src/test/resources/" + filepath;
                try {
                    courses = new DataLoader(fallback).loadCourses();
                } catch (FileNotFoundException e2) {
                    System.out.println("File not found at either path: " + filepath + " or " + fallback);
                } catch (IOException e2) {
                    System.out.println("Error reading file at fallback path: " + e2.getMessage());
                } catch (Exception e2) {
                    System.out.println("Unexpected error at fallback path: " + e2.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }

        // --- Step 2: Verbose mode prompt ---
        System.out.print("\nEnable verbose mode? (Y/n): ");
        final boolean verbose = scanner.nextLine().trim().equalsIgnoreCase("y");

        if (verbose) {
            System.out.println("\nVerbose mode enabled.");
            System.out.println("Loaded " + courses.size() + " courses:");
            for (Course c : courses) {
                System.out.println("  - " + c);
            }
        }

        // --- Step 3: Generate all non-overlapping scheduling options ---
        List<List<TimeSlot>> options = new ArrayList<>();

        for (Course c : courses) {
            List<List<TimeSlot>> courseOptions = c.getOptions();

            if (verbose) {
                System.out.println("\nCourse: " + c.getDisplayName());
                System.out.println("Number of scheduling options: " + courseOptions.size());
            }

            List<List<TimeSlot>> newOptions = new ArrayList<>();

            if (options.isEmpty()) {
                // No previous options: use current course options as base
                options = courseOptions;
                if (verbose) System.out.println("No existing options, using current course options as base.");
            } else {
                // Combine new course options with existing schedules
                for (List<TimeSlot> op : courseOptions) {
                    if (verbose) System.out.println("Checking course option: " + op);

                    for (List<TimeSlot> curr : options) {
                        boolean overlap = TimeSlot.checkOverlap(curr, op);

                        if (verbose) {
                            System.out.println("Comparing with existing schedule: " + curr);
                            System.out.println("Overlap? " + overlap);
                        }

                        if (!overlap) {
                            List<TimeSlot> combined = new ArrayList<>(curr);
                            combined.addAll(op);
                            newOptions.add(combined);

                            if (verbose) {
                                System.out.println("    Added new valid schedule combination: " + combined);
                            }
                        }
                    }
                }
            }

            // Update the master list if new combinations were found
            if (!newOptions.isEmpty()) {
                options = newOptions;
            }

            // If no non-overlapping options exist, error
            if (newOptions.isEmpty() && !options.equals(courseOptions)) {
                throw new Error("No valid non-overlapping schedules could be generated for course: " + c.getDisplayName());
            }
        }

        // --- Step 4: Rank all options using scoring engine ---
        if (verbose) System.out.println("\nScoring all scheduling options...");
        options.sort((o1, o2) -> Integer.compare(
                ScoringEngine.apply(o2, verbose), // higher score first
                ScoringEngine.apply(o1, verbose)
        ));

        if (verbose) System.out.println("\nDisplaying ranked schedule options...");

        // --- Step 5: Display options interactively ---
        int index = 1;
        for (List<TimeSlot> schedule : options) {
            System.out.println("\nOption " + index++);
            Output.displaySchedule(schedule);

            System.out.print("Press Enter to continue, or type 'exit' to quit: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Stopped by user.");
                break;
            }
        }

        scanner.close();
    }
}
