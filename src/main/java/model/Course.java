package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a university course, including its field, code, and sections.
 */
public class Course {

    private Field field;
    private int code;
    private List<CourseSection> sections;

    /** Default constructor required by Jackson */
    public Course() {
        this.sections = new ArrayList<>();
    }

    public Course(Field field, int code) {
        if (code < 1000 || code >= 5000) {
            throw new IllegalArgumentException("Invalid course code. (1000 <= code < 5000)");
        }
        this.field = field;
        this.code = code;
        this.sections = new ArrayList<>();
    }

    /** Jackson-compatible constructor using @JsonCreator */
    @JsonCreator
    public Course(
            @JsonProperty("field") Field field,
            @JsonProperty("code") int code,
            @JsonProperty("sections") List<CourseSection> sections) {
        this.field = field;
        this.code = code;
        this.sections = (sections != null) ? sections : new ArrayList<>();
    }

    // Getters and setters required for Jackson
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CourseSection> getSections() {
        return sections;
    }

    public void setSections(List<CourseSection> sections) {
        this.sections = sections;
    }

    public void addSection(CourseSection section) {
        this.sections.add(section);
    }

    public List<List<TimeSlot>> getOptions() {
        List<List<TimeSlot>> options = new ArrayList<>();
        for (CourseSection sec : sections) {
            options.addAll(sec.getOptions());
        }
        return options;
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder(field.toString() + code + ":");
        for (CourseSection section : sections) {
            message.append("\n(").append(section.getName()).append(")\n").append(section.toString());
        }
        return message.toString();
    }
}
