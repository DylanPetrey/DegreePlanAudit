package utd.dallas.backend;

import java.util.Objects;

public class Course {
    protected String courseNumber;
    protected String courseTitle;
    protected CourseType type;
    public enum CourseType {
        CORE,
        OPTIONAL,
        ELECTIVE,
        ADDITIONAL,
        TRACK,
        PRE,
        OTHER;
    }

    /**
     * Empty constructor. Used for the blank forms.
     */
    public Course() {
        this.courseNumber = "";
        this.courseTitle = "";
    }

    /**
     * Filled constructor. Used for parsing the transcript.
     */
    public Course(String courseNumber, String courseDescription, CourseType type) {
        this.courseNumber = courseNumber;
        this.courseTitle = courseDescription;
        this.type = type;
    }


    /**
     * Prints the course to the console
     */
    public void printCourse() {
        System.out.println(this.toString());
    }

    /**
     * Creates a string of the course similar to how it will look on the final audit pdf
     *
     * @return returns a string of the course
     */
    @Override
    public String toString() {
        return courseNumber + " " +
                courseTitle + " ";
    }

    /**
     * Compares if two objects are courses and have the same course num
     *
     * @param obj Object to compare to
     * @return returns true if the object is the same num and type
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!obj.getClass().isAssignableFrom(this.getClass())) {
            return false;
        }
        Course course = (Course) obj;
        return Objects.equals(courseNumber, course.courseNumber);
    }

    /**
     * Accessor methods to be used outside the class.
     */

    public CourseType getType() {
        return type;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public void setCourseTitle(String courseDescription) {
        this.courseTitle = courseDescription;
    }

    public void setType(Course.CourseType type) {
        this.type = type;
    }
}