package input;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import model.Component;
import model.Course;
import model.CourseSection;
import model.TimeSlot;

/**
 * DataLoader reads course information from a JSON file
 * and converts it into a list of Course objects with their sections, lectures, labs, and tutorials.
 *
 * <p>The expected JSON format:
 * <pre>
 * [
 *   {
 *     "field": "CSI",
 *     "code": 2105,
 *     "sections": [
 *       {
 *         "name": "A",
 *         "lectures": [{"startTime":0,"day":0,"extended":false}, ...],
 *         "laboratoryOptions": [{"startTime":2,"day":2,"extended":false}, ...],
 *         "tutorialOptions": [{"startTime":1,"day":1,"extended":false}, ...]
 *       },
 *       ...
 *     ]
 *   },
 *   ...
 * ]
 * </pre>
 */
public class DataLoader {

    /** Path to the JSON file containing course information. */
    private String filePath;

    /**
     * Constructs a DataLoader with the given file path.
     *
     * @param filePath the path to the JSON file
     */
    public DataLoader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads the JSON course data file and parses it into a list of Course objects.
     *
     * @return a list of Course objects
     * @throws IOException if the file cannot be read or parsed
     */
    public List<Course> loadCourses() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);

        // Reads the JSON array into a list of Course objects
        List<Course> courses = mapper.readValue(file, new TypeReference<List<Course>>() {});

        for (Course course : courses) {
            String courseName = course.getField().name() + course.getCode();
            for (CourseSection section : course.getSections()) {
                String sectionName = courseName + section.getName();

                for (TimeSlot lec : section.getLectures()) {
                    lec.setCourseName(sectionName);
                    lec.setComp(Component.LEC);
                }
                for (TimeSlot lab : section.getLaboratoryOptions()) {
                    lab.setCourseName(sectionName);
                    lab.setComp(Component.LAB);
                }
                for (TimeSlot tut : section.getTutorialOptions()) {
                    tut.setCourseName(sectionName);
                    tut.setComp(Component.TUT);
                }
            }
        }

        return courses;
    }

    /**
     * Example main method to demonstrate usage of DataLoader.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            DataLoader loader = new DataLoader("courses.json");
            List<Course> courses = loader.loadCourses();
            for (Course c : courses) {
                System.out.println(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
