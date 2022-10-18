import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * TODO:
 *   1. Separate courses into core and elective
 *        - Implement into existing GPA functions
 *   2. Evaluate requirements still needed to graduate
 *        - Find remaining classes in plan
 *        - Average GPA needed in remaining classes to meet minimum GPA
 */

public class Audit {
    final double MIN_CORE_GPA = 3.19;
    final double MIN_ELECT_GPA = 3.0;
    final double MIN_OVRALL_GPA = MIN_ELECT_GPA;

    // course variables
    private List<Course> courseList;
    private HashMap<String, List<Course>> utdClasses;

    private double combinedGPA;

    /**
     * Audit constructor
     *
     * @param courseList List of all courses that a student has taken
     */
    public Audit(List<Course> courseList){
        this.courseList = courseList;
        this.utdClasses = new HashMap<>();
    }

    /**
     * This function runs the gpa calculation
     */
    public void calculateGPA(){
        fillValidClasses();
        calculateGPAValues();
    }

    /**
     * This function extracts the classes that need to be included in the GPA.
     * Fills a hashmap with all the unique classes. This is mainly to keep track of repeated classes
     */
    private void fillValidClasses(){
        // validate correct grade values
        Pattern stringPattern = Pattern.compile("(^[A-F])(?=\\+|-|$)");

        courseList.forEach(course -> {
            Matcher m = stringPattern.matcher(course.getLetterGrade());
            if(!m.find() || course.isTransfer())
                return;

            utdClasses.computeIfAbsent(course.getCourseNumber(), k -> new ArrayList<>()).add(course);
        });
    }

    /**
     * This function finds the class with the highest grade
     *
     * @param identicalCourses List of identical courses. This is the value in the hashmap
     */
    private int getMaxGradeIndex(List<Course> identicalCourses){
        if(identicalCourses.size() == 1)   {
            return 0;
        }
        double maxPoints = 0;
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
     * This function calculates the GPA given the unique classes and updates the GPA value rounded to 3 decimal places
     */
    private void calculateGPAValues(){
        double cumGradePoints = 0;
        double cumGpaHours = 0;

        // Looping through the HashMap
        // Using for-each loop
        for (Map.Entry<String, List<Course>> mapElement : utdClasses.entrySet()) {
            String key = mapElement.getKey();

            Course current = mapElement.getValue().get(getMaxGradeIndex(mapElement.getValue()));

            cumGradePoints += current.getPoints();
            cumGpaHours += current.getAttempted();
        }

        combinedGPA = cumGradePoints / cumGpaHours;

        // Round gpa
        double scale = Math.pow(10, 3);
        combinedGPA = Math.round(combinedGPA * scale) / scale;
    }

    public void evaluateDegreePlan(Concentration currentConcentration){

    }

    public double getCombinedGPA() { return combinedGPA; }
}
