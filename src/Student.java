import java.util.*;

public class Student {
    // Student Variables
    private String studentName;
    private String studentId;
    private String startDate;
    private String currentMajor;
    private Plan currentTrack;
    private List<StudentCourse> courseList;

    /**
     * Initializes the student object using basic information and creating a
     * blank list of classes
     *
     * @param studentName Name of the student
     * @param studentId ID number of the student
     * @param startDate Date the student was enrolled to the masters program
     * @param currentMajor current major of the student (undergraduate, masters, etc.)
     */
    public Student(String studentName, String studentId, String startDate, String currentMajor){
        this.studentName = studentName;
        this.studentId = studentId;
        this.startDate = startDate;
        this.currentMajor = currentMajor;

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
        StudentCourse newCourse = new StudentCourse(line, semester, transfer);
        courseList.add(newCourse);
    }


   /**
    * Outputs all the information to the console in a similar style to how it will
    * be displayed the final Audit PDF.
    */
    public void printStudentInformation(){
        System.out.println(studentName);
        System.out.println(studentId);
        System.out.println(startDate);
        System.out.println(currentMajor);
        System.out.println();

        courseList.forEach((c) -> c.printCourse());
    }


   /**
    * Accessor methods to be used outside the class.
    */
    public List<StudentCourse> getCourseList() { return courseList; }
    public String getStudentName(){ return studentName; }
    public String getStudentId(){ return studentId; }
    public String getStartDate(){ return startDate; }
    public String getCurrentMajor() { return currentMajor; }
    public Plan getCurrentTrack() { return currentTrack; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudentName(String studentName){ this.studentName = studentName; }
    public void setStudentId(String studentId){ this.studentId = studentId; }
    public void setStartDate(String startDate){ this.startDate = startDate; }
    public void setCurrentMajor(String currentMajor) { this.currentMajor = currentMajor; }
    public void setCurrentTrack(Plan currentTrack) { this.currentTrack = currentTrack; }
}
