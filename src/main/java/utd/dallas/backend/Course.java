package utd.dallas.backend;

import java.util.Objects;

public class Course {
    private String courseNumber;
    private String courseTitle;
    private String description;
    private String hours;
    protected CourseType type;
    public enum CourseType {
        CORE("Core"),
        OPTIONAL("Optional"),
        ELECTIVE("Elective"),
        ADDITIONAL("Additional"),
        TRACK("Track"),
        PRE("Prereq"),
        OTHER("Other");

        private final String typeString;
        private CourseType(String st){
            this.typeString = st;
        }

        public String toString() {
            return this.typeString;
            
        }
    }

    /**
     * Empty constructor. Used for the blank forms.
     */
    public Course() {
        this.courseNumber = "";
        this.courseTitle = "";
        this.hours = "";
    }

    public Course(Course another) {
        this.courseNumber = another.courseNumber;
        this.courseTitle = another.courseTitle;
        this.type = another.type;

    }

    /**
     * Filled constructor. Used for parsing the transcript.
     */
    public Course(String courseNumber, String courseTitle, String hours, CourseType type) {
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.hours = hours;
        this.type = type;
    }

    /**
     * Prints the course to the console
     */
    public void printCourse() {
        System.out.println(this.toString());
    }

    /**
     * Creates a string of the course similar to how it will look on the final audit
     * pdf
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
        if (this == obj)
            return true;
        if (obj.getClass().isAssignableFrom(this.getClass()) || this.getClass().isAssignableFrom(obj.getClass())) {
            Course course = (Course) obj;
            return Objects.equals(courseNumber, course.courseNumber);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseNumber);
    }

    /**
     * Accessor methods to be used outside the class.
     */

    public CourseType getType() { return type; }
    public String getCourseNumber() { return courseNumber; }
    public String getCourseTitle() { return courseTitle; }
    public String getDescription() { return description; }
    public String getHours() { return hours; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setCourseNumber(String courseNumber) { this.courseNumber = courseNumber; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public void setDescription(String courseDescription) { this.description = courseTitle; }
    public void setType(Course.CourseType type) {
        this.type = type;
    }
    public void setHours(String hours) { this.hours = hours; }
}