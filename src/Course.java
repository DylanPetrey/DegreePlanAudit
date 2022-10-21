import java.util.Arrays;

public class Course {
    protected String courseNumber;
    protected String courseDescription;
    protected CourseType type;
    public enum CourseType {
        CORE,
        ELECTIVE, 
        PRE,
        OTHER;
    }

    public Course() {
        this.courseNumber = "";
        this.courseDescription = "";
    }


    public Course(String courseNumber, String courseDescription, CourseType type) {
        this.courseNumber = courseNumber;
        this.courseDescription = courseDescription;
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
        return  courseNumber + " " +
                courseDescription + " ";
    }

    /**
     * Accessor methods to be used outside the class.
     */

    public CourseType getType() {return type;}
    public String getCourseNumber() { return courseNumber; }
    public String getCourseDescription() { return courseDescription; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setCourseNumber(String courseNumber) { this.courseNumber = courseNumber; }
    public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }
    public void setType(Course.CourseType type) { this.type = type; }
}
