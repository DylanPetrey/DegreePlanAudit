import java.net.ConnectException;
import java.util.*;


public class Form {
    
    private Concentration concentration;
    private String studentName;
    private String studentID;
    private String semesterAdmitted;
    private String anticipatedGraduation;
    private boolean isFastTrack;
    private boolean thesis;
    private List<Course> coreCourses;
    private List<Course> electiveCourses;
    private List<Course> addlCourses;
    

    /** 
     * Initializes Form object to be used to fill out PDF
     * @param currentStudent Student object that the form is generated for
     */
    public Form(Student currentStudent) {
        this.studentName = currentStudent.getStudentName();
        this.studentID = currentStudent.getStudentId();
        this.semesterAdmitted = "";
        this.anticipatedGraduation = "";
        this.isFastTrack = false;
        this.thesis = false;
    }

    /**
     * Accessor methods to be used outside the class.
     */
    public Concentration getConcentration(){ return this.concentration; }
    public String getStudenName(){return studentName; }
    public String getStudentID(){ return studentID; }
    public String getSemesterAdmitted(){ return semesterAdmitted; }
    public String getAnticipatedGraduation(){ return anticipatedGraduation; }
    public boolean isFastTrack(){ return isFastTrack; }
    public boolean isThesis(){ return thesis; }
    public List<Course> getCoreCourses(){ return coreCourses; }
    public List<Course> getElectiveCourses(){ return electiveCourses; }
    public List<Course> getAdditionalCourses(){ return addlCourses; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setConcentration(Concentration concentration){ this.concentration = concentration; }
    public void setStudenName(String studentName){ this.studentName = studentName; }
    public void setStudentID(String studentID){this.studentID = studentID; }
    public void setSemesterAdmitted(String semesterAdmitted){ this.semesterAdmitted = semesterAdmitted; }
    public void setAnticipatedGraduation(String anticipatedGraduation){ this.anticipatedGraduation = anticipatedGraduation; }
    public void setFastTrack(boolean isFastTrack){this.isFastTrack = isFastTrack; }
    public void setThesis(boolean thesis) { this.thesis = thesis; }
    
    public void addCourse(List<Course> courseList, Course course) { courseList.add(course); }
    public void removeCourse(List<Course> courseList, Course course) { this.courseList.remove(course); }

}

