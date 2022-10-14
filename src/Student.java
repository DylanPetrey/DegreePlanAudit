import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Student {
    // Student Variables
    private String studentName;
    private String studentId;
    private String startDate;
    public List<Course> courseList;


    /**
     * Initializes the student object using basic information and creating a
     * blank list of classes
     *
     * @param studentName Name of the student
     * @param studentId ID number of the student
     * @param startDate Date the student was enrolled to the masters program
     */
    public Student(String studentName, String studentId, String startDate){
        this.studentName = studentName;
        this.studentId = studentId;
        this.startDate = startDate;

        courseList = new ArrayList<>();
    }

    /**
     * This function creates a course from the input on the transcript and adds it to the
     * list of the student's courses.
     *
     * @param line This is the line of the course as read on the transcript.
     * @param semester String identifying the semester.
     * @param transfer true if the course is transfer credit, false if utd credit.
     */
    public void addCourse(String line, String semester, boolean transfer){
        Course newCourse = new Course(line, semester, transfer);
        courseList.add(newCourse);

        //addToGpaTotals(newCourse);
    }

   /**
    * Outputs all the information to the console in a similar style to how it will
    * be displayed the final Audit PDF.
    */
    public void printStudentInformation(){
        System.out.println(studentName);
        System.out.println(studentId);
        System.out.println(startDate);
        System.out.println();

        courseList.forEach((c) -> c.printCourse());
    }

   /**
    * Accessor methods to be used outside the class.
    */
    public String getStudentName(){ return studentName; }
    public String getStudentId(){ return studentId; }
    public String getStartDate(){ return startDate; }
    public List<Course> getCourseList() { return courseList; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudentName(String studentName){ this.studentName = studentName; }
    public void setStudentId(String studentId){ this.studentId = studentId; }
    public void setStartDate(String startDate){ this.startDate = startDate; }

}
