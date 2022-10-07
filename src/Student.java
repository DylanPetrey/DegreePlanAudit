import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Student {
    private String studentName;
    private String studentId;
    private String startDate;
    public List<Course> courseList;


    private HashMap<String, List<Course>> gpaClasses;
    private float utdAttemptedHours;
    private float utdEarnedHours;
    private float utdPoints;
    private float utdGpaUnits;
    private float transferAttemptedHours;
    private float transferEarnedHours;
    private float totalAttemptedHours;
    private float totalEarnedHours;
    private HashMap<String, List<Course>> transferClasses;
    private float gpa;


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

        this.gpa = 0;
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

        Pattern stringPattern = Pattern.compile("(^[A-F]{1})(?=\\+|-|$)");
        Matcher m = stringPattern.matcher(newCourse.getLetterGrade());

        // Don't calculate grades for current semester or transfer courses
        if(newCourse.isTransfer()) {
            transferAttemptedHours += newCourse.getAttempted();
            if (m.find())
                transferClasses.computeIfAbsent(newCourse.getCourseNumber(), k -> new ArrayList<>()).add(newCourse);
            else
                utdEarnedHours += newCourse.getEarned();
        } else {
            utdAttemptedHours += newCourse.getAttempted();
            if (m.find())
                gpaClasses.computeIfAbsent(newCourse.getCourseNumber(), k -> new ArrayList<>()).add(newCourse);
            else
                utdEarnedHours += newCourse.getEarned();
        }


    }

    // this code is messy because its 2am so ill clean it up later
    public void calculateGpa(){
        gpaClasses.forEach((number, l) -> {
            Course best = l.get(getMaxIndex(l));

            utdGpaUnits += best.getAttempted();
            utdEarnedHours += best.getEarned();
            utdPoints += best.getPoints();
        });

        transferClasses.forEach((number, l) -> {
            transferEarnedHours += l.get(l.size()-1).getEarned();
        });

        totalEarnedHours = utdEarnedHours + transferEarnedHours;
        totalAttemptedHours = utdAttemptedHours + transferAttemptedHours;

        System.out.println(utdAttemptedHours +  " " +
                            utdEarnedHours +  " " +
                            utdGpaUnits + " " +
                            utdPoints);

        System.out.println(transferAttemptedHours + " " +  transferEarnedHours);

        System.out.println(totalAttemptedHours + " " + totalEarnedHours);

        gpa = utdPoints /utdGpaUnits;

        System.out.println(gpa);

    }
    public int getMaxIndex(List<Course> identicalCourses){
        if(identicalCourses.size() == 1)   {
            return 0;
        }

        float maxPoints = 0;
        int maxIndex = 0;

        for(int i = 0; i < identicalCourses.size() && i < 3; i++){
            if(maxPoints < identicalCourses.get(i).getPoints()){
                maxPoints = identicalCourses.get(i).getPoints();
                maxIndex = i;
            }
        }

        if(maxPoints == 0)
            maxIndex = identicalCourses.size()-1;

        return maxIndex;
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

    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudentName(String studentName){ this.studentName = studentName; }
    public void setStudentId(String studentId){ this.studentId = studentId; }
    public void setStartDate(String startDate){ this.startDate = startDate; }


}
