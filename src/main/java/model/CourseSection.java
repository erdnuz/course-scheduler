package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a section of a course, including its lectures, lab options,
 * and tutorial options. Provides functionality to generate all valid combinations
 * of time slots without overlaps.
 */
public class CourseSection {

    private List<TimeSlot> lectures;
    private List<TimeSlot> laboratoryOptions;
    private List<TimeSlot> tutorialOptions;
    private String name;

    /** Default constructor required by Jackson */
    public CourseSection() {
        this.lectures = new ArrayList<>();
        this.laboratoryOptions = new ArrayList<>();
        this.tutorialOptions = new ArrayList<>();
    }

    /** Constructor with only name */
    public CourseSection(String name) {
        this.name = name;
        this.lectures = new ArrayList<>();
        this.laboratoryOptions = new ArrayList<>();
        this.tutorialOptions = new ArrayList<>();
    }

    /** Jackson-compatible constructor */
    @JsonCreator
    public CourseSection(
            @JsonProperty("name") String name,
            @JsonProperty("lectures") List<TimeSlot> lectures,
            @JsonProperty("laboratoryOptions") List<TimeSlot> laboratoryOptions,
            @JsonProperty("tutorialOptions") List<TimeSlot> tutorialOptions) {
        this.name = name;
        this.lectures = (lectures != null) ? lectures : new ArrayList<>();
        this.laboratoryOptions = (laboratoryOptions != null) ? laboratoryOptions : new ArrayList<>();
        this.tutorialOptions = (tutorialOptions != null) ? tutorialOptions : new ArrayList<>();
    }

    // Getters and setters
    public List<TimeSlot> getLectures() {
        return lectures;
    }

    public void setLectures(List<TimeSlot> lectures) {
        this.lectures = lectures;
    }

    public List<TimeSlot> getLaboratoryOptions() {
        return laboratoryOptions;
    }

    public void setLaboratoryOptions(List<TimeSlot> laboratoryOptions) {
        this.laboratoryOptions = laboratoryOptions;
    }

    public List<TimeSlot> getTutorialOptions() {
        return tutorialOptions;
    }

    public void setTutorialOptions(List<TimeSlot> tutorialOptions) {
        this.tutorialOptions = tutorialOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addLecture(TimeSlot lec) {
        this.lectures.add(lec);
    }

    public void addLaboratory(TimeSlot lab) {
        this.laboratoryOptions.add(lab);
    }

    public void addTutorial(TimeSlot tut) {
        this.tutorialOptions.add(tut);
    }

    public List<List<TimeSlot>> getOptions() {
        List<List<TimeSlot>> options = new ArrayList<>();
        options.add(new ArrayList<>(lectures));

        if (!laboratoryOptions.isEmpty()) {
            List<List<TimeSlot>> newOptions = new ArrayList<>();
            for (TimeSlot lab : laboratoryOptions) {
                for (List<TimeSlot> option : options) {
                    if (!lab.checkOverlap(option)) {
                        List<TimeSlot> newOption = new ArrayList<>(option);
                        newOption.add(lab);
                        newOptions.add(newOption);
                    }
                }
            }
            options = newOptions;
        }

        if (!tutorialOptions.isEmpty()) {
            List<List<TimeSlot>> newOptions = new ArrayList<>();
            for (TimeSlot tut : tutorialOptions) {
                for (List<TimeSlot> option : options) {
                    if (!tut.checkOverlap(option)) {
                        List<TimeSlot> newOption = new ArrayList<>(option);
                        newOption.add(tut);
                        newOptions.add(newOption);
                    }
                }
            }
            options = newOptions;
        }

        return options;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder("Lectures:");
        for (TimeSlot lec : lectures) {
            message.append("\n\t").append(lec);
        }

        message.append("\nLaboratories:");
        for (TimeSlot lab : laboratoryOptions) {
            message.append("\n\t").append(lab);
        }

        message.append("\nTutorials:");
        for (TimeSlot tut : tutorialOptions) {
            message.append("\n\t").append(tut);
        }

        return message.toString();
    }
}
