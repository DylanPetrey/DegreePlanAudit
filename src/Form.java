import java.util.*;

enum Concentration {
    TRADITIONAL,
    NETWORKS,
    INTEL,
    CYBERSEC,
    INTERACTIVE,
    SYSTEMS,
    DATA_SCIENCE,
    SOFTWARE_ENGINEERING
}
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
    public boolean isThesis() { return thesis; }
    public List<Course> getCoreCourses() { return coreCourses; }
    public List<Course> getElectiveCourses() { return electiveCourses; }
    public List<Course> getAdditionalCourses() { return addlCourses; }
    
}
