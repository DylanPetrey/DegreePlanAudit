import java.util.*;

/*
 * TODO:
 *   1. Create better test PDFs
 *        - I'm not sure if repeated classes calculate correctly
 *        - The provided samples aren't enough
 *   2. Evaluate requirements still needed to graduate
 *        - Find remaining classes in plan
 *        - Average GPA needed in remaining classes to meet minimum GPA
 */

public class Audit {
    // Student variables
    private Student currentStudent;
    private Plan degreePlan;

    // Course Hashmaps
    private HashMap<String, List<StudentCourse>> courseMap;
    private HashMap<String, List<StudentCourse>> utdGPAMap;


    // GPA Variables
    private double combinedGPA;
    private double coreGPA;
    private double electiveGPA;

    // Requirement Variables
    private final double MIN_CORE_GPA = 3.19;
    private final double MIN_ELECT_GPA = 3.0;
    private final double MIN_OVRALL_GPA = MIN_ELECT_GPA;
    private final int REQUIRED_CORE_HOURS = 15;
    private final int REQUIRED_ELECTIVE_HOURS = 15;
    private final int MIN_ADD_ELECTIVE_HOURS = 3;


    /**
     * Audit constructor
     *
     * @param currentStudent current student object
     */
    public Audit(Student currentStudent){
        this.currentStudent = currentStudent;
        degreePlan = currentStudent.getCurrentTrack();
        courseMap = getClassMap(currentStudent.getCourseList());
        utdGPAMap = new HashMap<>();

        combinedGPA = 0;
        coreGPA = 0;
        electiveGPA = 0;
    }


    /**
     * Performs all three parts of the audit. Basically to a main method for the class
     */
    public void runAudit(){
        //calculateGPA();
    }


    /**
     * This function creates a hashmap of the input courseList based on the unique course
     * names. This is to keep track of repeated classes
     *
     * @param classList List of courses
     * @return Hashmap of each unique course
     */
    private HashMap<String, List<StudentCourse>> getClassMap(List<StudentCourse> classList){
        HashMap<String, List<StudentCourse>> currentMap = new HashMap<>();

        classList.forEach(course -> {
            currentMap.computeIfAbsent(course.getCourseNumber(), k -> new ArrayList<>()).add(course);
        });

        return currentMap;
    }



/*
    /**
     * This function runs the gpa calculation
     *
    private void calculateGPA(){
        validateGPACourses();
        calculateGPAValues();
    }


    /**
     * This function fills in a hashmap of all courses completed at utd with a
     * valid grade A-F
     *
    private void validateGPACourses(){
        // validate correct grade values
        Pattern stringPattern = Pattern.compile("(^[A-F])(?=\\+|-|$)");

        // Loop over Hashmap
        courseMap.forEach((courseNumber, courseList) -> {
            List<StudentCourse> currentCourseList = new ArrayList<>();

            // Loop over each repeated class
            courseList.forEach(course -> {
                Matcher m = stringPattern.matcher(course.getLetterGrade());
                if(!m.find() || course.isTransfer())
                    return;

                currentCourseList.add(course);
            });

            if(currentCourseList.size() != 0)
                utdGPAMap.put(courseNumber, currentCourseList);
        });
    }


    /**
     * This function calculates the GPA given the utd classes and updates the GPA
     * value rounded to 3 decimal places for core, elective and combined
     *
    private void calculateGPAValues(){
        double cumGradePoints = 0;
        double cumGpaHours = 0;
        double coreGradePoints = 0;
        double coreGpaHours = 0;
        double electGradePoints = 0;
        double electGpaHours = 0;

        // Looping over each key in the HashMap
        for (Map.Entry<String, List<StudentCourse>> mapElement : utdGPAMap.entrySet()) {
            StudentCourse current = mapElement.getValue().get(getMaxCourseIndex(mapElement.getValue()));

            cumGradePoints += current.getPoints();
            cumGpaHours += current.getAttempted();

            if(coreReqList.contains(current.getCourseNumber())){
                coreGradePoints += current.getPoints();
                coreGpaHours += current.getAttempted();
            }
            else if(electCourseList.contains(current.getCourseNumber())){
                electGradePoints += current.getPoints();
                electGpaHours += current.getAttempted();
            }
        }

        combinedGPA = calcGPA(cumGradePoints, cumGpaHours);
        coreGPA = calcGPA(coreGradePoints, coreGpaHours);
        electiveGPA = calcGPA(electGradePoints, electGpaHours);
    }


    /**
     * Calculates the GPA
     *
     * @param gpaPoints Total number of points
     * @param gpaHours Total number of hours
     * @return returns the gpa rounded to 3 decimal places
     */
    private double calcGPA(double gpaPoints, double gpaHours ){
        double GPA = gpaPoints / gpaHours;

        double scale = Math.pow(10, 3);
        return Math.round(GPA * scale) / scale;
    }


    /**
     * Prints the GPA as seen on the sample audit
     */
    public void printGPA(){
        System.out.println("Core GPA: " + coreGPA);
        System.out.println("Elective GPA: " + electiveGPA);
        System.out.println("Combined GPA: " + combinedGPA);
    }
}
