import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GPA {
    // GPA variables
    private HashMap<String, List<Course>> gpaClasses;
    private HashMap<String, List<Course>> transferClasses;

    private float utdAttemptedHours;
    private float utdEarnedHours;
    private float utdPoints;
    private float utdGpaUnits;
    private float transferAttemptedHours;
    private float transferEarnedHours;
    private float totalAttemptedHours;
    private float totalEarnedHours;
    private float coreGPA;
    private float electiveGPA;
    private float combinedGPA;

    public List<Course> courseList;

    public GPA(List<Course> courseList){
        this.courseList = courseList;

        this.coreGPA = 0;
        this.electiveGPA = 0;
        this.combinedGPA = 0;

        this.utdAttemptedHours = 0;
        this.utdEarnedHours = 0;
        this.utdPoints = 0;
        this.utdGpaUnits = 0;
        this.transferAttemptedHours = 0;
        this.transferEarnedHours = 0;
        this.totalAttemptedHours = 0;
        this.totalEarnedHours = 0;

        this.gpaClasses = new HashMap<>();
        this.transferClasses = new HashMap<>();
        this.courseList = new ArrayList<Course>();
    }
}
