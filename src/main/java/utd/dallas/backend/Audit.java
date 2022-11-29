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
    private final Student currentStudent;
    private final List<StudentCourse> filledCourses;

    // List of courses divided up by type
    private final List<StudentCourse> coreList = new ArrayList<>();
    private final List<StudentCourse> electList = new ArrayList<>();
    private final List<StudentCourse> preList = new ArrayList<>();

    // GPA Variables
    private double combinedGPA = 0;
    private double coreGPA = 0;
    private double electiveGPA = 0;

    // Requirement Variables
    private final double MIN_CORE_GPA = 3.19;
    private final double MIN_ELECT_GPA = 3.0;
    private final double MIN_OVRALL_GPA = 3.0;
    private final int REQUIRED_CORE_HOURS = 15;
    private final int REQUIRED_ELECTIVE_HOURS = 15;
    private final int MIN_ADD_ELECTIVE_HOURS = 3;

    /**
     * Performs the audit when created
     *
     * @param currentStudent current student object
     */
    public Audit(Student currentStudent){
        this.currentStudent = currentStudent;
        this.filledCourses = currentStudent.getCleanCourseList();
        calculateGPAValues();
        printGPA();
    }

    /**
     * This function calculates the core, elective, and cumulative GPA values
     */
    private void calculateGPAValues(){
        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.CORE)
                .forEach(coreList::add);
        coreGPA = calcGPA(coreList);

        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.ELECTIVE || studentCourse.getType() == Course.CourseType.ADDITIONAL)
                .forEach(electList::add);
        electiveGPA = calcGPA(electList);

        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.PRE)
                .forEach(preList::add);

        combinedGPA = calcGPA(filledCourses);
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
        AtomicReference<Double> totalHours = new AtomicReference<>((double) 0);

        courseList.forEach(studentCourse -> {
            String letterGrade = studentCourse.getLetterGrade();
            double currentHour = 0;
            if(studentCourse.getAttempted() != 0)
                currentHour = studentCourse.getAttempted();
            else {
                try {
                    currentHour = currentStudent.getCurrentPlan().getCourseHours(studentCourse.getCourseNumber());
                    } catch (Exception e){
                        currentHour = 3;
                }
            }
            double finalCurrentHour = currentHour;
            if (letterGrade.equalsIgnoreCase("A"))
                totalPoints.updateAndGet(v -> (v+(A_GRADEPTS* finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("A-"))
                totalPoints.updateAndGet(v -> (v+(A_MINUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("B+"))
                        totalPoints.updateAndGet(v -> (v+(B_PLUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("B"))
                        totalPoints.updateAndGet(v -> (v+(B_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("B-"))
                        totalPoints.updateAndGet(v -> (v+(B_MINUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("C+"))
                totalPoints.updateAndGet(v -> (v+(C_PLUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("C"))
                totalPoints.updateAndGet(v -> (v+(C_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("F"))
                totalPoints.updateAndGet(v -> (v+(F_GRADEPTS*finalCurrentHour)));
            else
                return;

            totalHours.updateAndGet(v -> (v+finalCurrentHour));
        });

        double GPA = totalPoints.get() / totalHours.get();
        double scale = Math.pow(10, 3);
        return Math.round(GPA * scale) / scale;
    }


    /**
     * Prints the GPA as seen on the sample audit
     */
    public void printGPA(){
        currentStudent.printStudentInformation();
        System.out.println();
        System.out.println("Core: " + coreList.toString());
        System.out.println("Elective: " + electList.toString());
        System.out.println("Pre: " + preList.toString());
        System.out.println();
        System.out.println("Core GPA: " + coreGPA);
        System.out.println("Elective GPA: " + electiveGPA);
        System.out.println("Combined GPA: " + combinedGPA);
    }
}