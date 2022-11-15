package utd.dallas.backend;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * TODO:
 *   1. Evaluate requirements still needed to graduate
 *        - Find remaining classes in plan
 *        - Average GPA needed in remaining classes to meet minimum GPA
 *   2. Configure to include repeated courses (Need to make a custom transcript for this)
 */

public class Audit {
    // Student variables
    private Student currentStudent;
    private Plan degreePlan;

    // Course Hashmaps
    private HashMap<String, List<StudentCourse>> courseMap = new HashMap<>();
    private HashMap<String, List<StudentCourse>> utdGPAMap = new HashMap<>();


    // GPA Variables
    private double combinedGPA = 0;
    private double coreGPA = 0;
    private double electiveGPA = 0;

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
        degreePlan = currentStudent.getCurrentPlan();
        courseMap = getClassMap(currentStudent.getCourseList());
    }


    /**
     * Performs all three parts of the audit. Basically to a main method for the class
     */
    public void runAudit(){
        calculateGPA();
        printGPA();
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



    /**
     * This function runs the gpa calculation
     */
    private void calculateGPA(){
        validateGPACourses();
        calculateGPAValues();
    }


    /**
     * This function fills in a hashmap of all courses completed at utd with a
     * valid grade A-F
     */
    private void validateGPACourses(){
        // validate correct grade values
        Pattern stringPattern = Pattern.compile("(^[A-F])(?=\\+|-|$)");

        // Loop over Hashmap
        courseMap.forEach((courseNumber, courseList) -> {
            List<StudentCourse> currentCourseList = new ArrayList<>();

            // Loop over each repeated class
            courseList.forEach(course -> {
                Matcher m = stringPattern.matcher(course.getLetterGrade());
                if(!m.find() || course.getTransfer().equals("T"))
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
     */
    /**
     * This function calculates the core, elective, and cumulative GPA values
     */
    private void calculateGPAValues(){
        List<StudentCourse> coreList = new ArrayList<>();
        currentStudent.getCourseList().stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.CORE)
                .forEach(coreList::add);
        coreGPA = calcGPA(coreList);

        List<StudentCourse> electList = new ArrayList<>();
        currentStudent.getCourseList().stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.ELECTIVE || studentCourse.getType() == Course.CourseType.ADDITIONAL)
                .forEach(electList::add);
        electiveGPA = calcGPA(electList);

        combinedGPA = calcGPA(currentStudent.getCourseList());
    }


    /**
     * Calculates the gpa for the courseList
     *
     * @param courseList list of courses to get the GPA from
     * @return gpa value rounded to 3 digits
     */
    private double calcGPA(List<StudentCourse> courseList){
        final double A_GRADEPTS = 4.000;
        final double A_MINUS_GRADEPTS = 3.670;
        final double B_PLUS_GRADEPTS = 3.330;
        final double B_GRADEPTS = 3.000;
        final double B_MINUS_GRADEPTS = 2.670;
        final double C_PLUS_GRADEPTS = 2.330;
        final double C_GRADEPTS = 2.000;
        final double F_GRADEPTS = 0.000;
        AtomicReference<Double> totalPoints = new AtomicReference<>((double) 0);
        AtomicReference<Integer> totalHours = new AtomicReference<>((Integer) 0);

        courseList.forEach(studentCourse -> {
                    String letterGrade = studentCourse.getLetterGrade();
                    int currentHour = currentStudent.getCurrentPlan().getCourseHours(studentCourse.getCourseNumber());
                    if (letterGrade.equalsIgnoreCase("A"))
                        totalPoints.updateAndGet(v -> (v+(A_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("A-"))
                        totalPoints.updateAndGet(v -> (v+(A_MINUS_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("B+"))
                        totalPoints.updateAndGet(v -> (v+(B_PLUS_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("B"))
                        totalPoints.updateAndGet(v -> (v+(B_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("B-"))
                        totalPoints.updateAndGet(v -> (v+(B_MINUS_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("C+"))
                        totalPoints.updateAndGet(v -> (v+(C_PLUS_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("C"))
                        totalPoints.updateAndGet(v -> (v+(C_GRADEPTS*currentHour)));
                    else if (letterGrade.equalsIgnoreCase("F"))
                        totalPoints.updateAndGet(v -> (v+(F_GRADEPTS*currentHour)));
                    else
                        return;

                    totalHours.updateAndGet(v -> (v+currentHour));
                }
        );

        double GPA = totalPoints.get() / totalHours.get();
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