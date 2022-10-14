import java.util.*;

enum Concentration {
    TRADITIONAL,
    NETWORKS,
    INTEL,
    CYBER,
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
    private boolean fasttrack;
    private boolean thesis;
    private List<Course> core;
    private List<Course> addlCore;
    

    /** 
     * Initializes Form object to be used to fill out PDF
     * @param currentStudent
     */
    public Form(Student currentStudent) {
        this.studentName = currentStudent.getStudentName();
        this.studentID = currentStudent.getStudentId();
        this.semesterAdmitted = "";
        this.anticipatedGraduation = "";
        this.fasttrack = false;
        this.thesis = false;



    }
}
