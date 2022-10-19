import DegreePlans.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private Concentration degreeTrack;

    // Course Hashmaps
    private HashMap<String, List<Course>> courseMap;
    private HashMap<String, List<Course>> utdGPAMap;

    // GPA Variables
    private List<String> coreCourseNumbers;
    private List<String> electCourseNumbers;
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
        degreeTrack = currentStudent.getCurrentTrack();
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
        evaluateDegreePlan();
        calculateGPA();
    }

    /**
     * This function creates a hashmap of the input courseList based on the unique course
     * names. This is to keep track of repeated classes
     *
     * @param classList List of courses
     * @return Hashmap of each unique course
     */
    private HashMap<String, List<Course>> getClassMap(List<Course> classList){
        HashMap<String, List<Course>> currentMap = new HashMap<>();

        classList.forEach(course -> {
            currentMap.computeIfAbsent(course.getCourseNumber(), k -> new ArrayList<>()).add(course);
        });

        return currentMap;
    }

    /**
     * Finds the course with the highest GPA. This method specifically used for a list of courses.
     * Ideal for the hashmap.
     *
     * @param listOfCourses List of courses
     * @return index of the course with the highest GPA
     */
    private int getMaxCourseIndex(List<Course> listOfCourses){
        if(listOfCourses.size() == 1){
            return 0;
        }
        double maxPoints = 0;
        int maxIndex = 0;
        for(int i = 0; i < listOfCourses.size() && i < 3; i++){
            if(maxPoints < listOfCourses.get(i).getPoints()){
                maxPoints = listOfCourses.get(i).getPoints();
                maxIndex = i;
            }
        }
        if(maxPoints == 0)
            maxIndex = listOfCourses.size()-1;
        return maxIndex;
    }


    /**
     * Finds the course with the highest GPA. This method specifically used to find the optional
     * core course with the highest grade.
     * Ideal for the degree plans.
     *
     * @param listOfCourseNums List of courses numbers
     * @return index of the course with the highest GPA
     */
    private int getMaxStringIndex(List<String> listOfCourseNums){
        if(listOfCourseNums.size() == 1){
            return 0;
        }
        int maxIndex = 0;
        double maxGrade = courseMap.get(listOfCourseNums.get(maxIndex)).get(maxIndex).getPoints();

        for(int i = 1; i < listOfCourseNums.size(); i++){
            int maxCourseIndex = getMaxCourseIndex(courseMap.get(listOfCourseNums.get(i)));
            Course maxCourse = courseMap.get(listOfCourseNums.get(i)).get(maxCourseIndex);

            if(maxGrade < maxCourse.getPoints()){
                maxIndex = i;
                maxGrade = maxCourse.getPoints();
            }
        }

        return maxIndex;
    }


    /**
     * This function evaluates the courses based on the degree plan and fills in the
     * variables for the completed requirements
     */
    public void evaluateDegreePlan(){
        Plan currentPlan = createDegreePlan();

        fillCoreCourses(currentPlan);
        fillElectives();
    }


    /**
     * This function retrieves all the core courses that the student has taken and populates
     * coreCourseNumbers with the mandatory core courses with the highest GPA value
     *
     * @param currentPlan The current degree plan as it is represented on the form
     */
    private void fillCoreCourses(Plan currentPlan){
        String[] totalCore = currentPlan.getCore();
        List<String> corePlanNum = getIntersectionNumbers(courseMap, totalCore);
        List<String> optPlanNum = getIntersectionNumbers(courseMap, currentPlan.getOptionalCore());

        // Fill in core courses
        coreCourseNumbers = new ArrayList<>(corePlanNum);
        int fillOpt = currentPlan.getNumOptional();

        while(fillOpt != 0 && optPlanNum.size() != 0){
            int maxIndex = getMaxStringIndex(optPlanNum);
            coreCourseNumbers.add(optPlanNum.get(maxIndex));
            optPlanNum.remove(maxIndex);

            fillOpt--;
        }
    }

    /**
     * Finds the courses that a student has taken
     *
     * @param original Hashmap of courses
     * @param compare String list of course numbers to search for
     * @return List of course numbers that a student has taken in the list of course numbers
     */
    private List<String> getIntersectionNumbers(HashMap<String, List<Course>> original, String[]  compare){
        List<String> currentList = new ArrayList<>();

        for(String currentCourseNumber : compare){
            if(original.containsKey(currentCourseNumber)){
                currentList.add(currentCourseNumber);
            }
        }

        return currentList;
    }

    /**
     * Calculates the number of hours a student has taken in the hashmap
     *
     * @param currentCourses List of course numbers
     * @return number of hours
     */
    private double getCourseHours(List<String> currentCourses){
        AtomicReference<Double> currentHours = new AtomicReference<>((double) 0);

        currentCourses.forEach(course -> {
            List<Course> currentCourseList = courseMap.get(course);
            int highestIndex = getMaxCourseIndex(currentCourseList);
            currentHours.updateAndGet(v -> v + currentCourseList.get(highestIndex).getEarned());
        });

        return currentHours.get();
    }

    /**
     * Fills the electives. The electives are basically anything that is not a core course
     */
    private void fillElectives(){
        electCourseNumbers = new ArrayList<>(courseMap.keySet());

        coreCourseNumbers.forEach(course -> {
            electCourseNumbers.remove(course);
        });

    }


    /**
     * Creates a degree plan object with the corresponding subclass
     *
     * @return Plan object that holds the degree requirements
     */
    private Plan createDegreePlan(){
        Plan currentPlan = new Plan();

        switch (degreeTrack){
            case TRADITIONAL:
                currentPlan = new TraditionalComputerScience();
                break;
            case NETWORKS:
                currentPlan = new NetworksAndTelecommunication();
                break;
            case INTEL:
                currentPlan = new IntelligentSystems();
                break;
            case INTERACTIVE:
                currentPlan = new InteractiveComputing();
                break;
            case SYSTEMS:
                currentPlan = new SystemsTrack();
                break;
            case DATA:
                currentPlan = new DataScience();
                break;
            case CYBER:
                currentPlan = new CyberSecurity();
                break;
            case SOFTWARE:
                currentPlan = new SoftwareEngineering();
                break;
        }

        return currentPlan;
    }


    /**
     * This function runs the gpa calculation
     */
    public void calculateGPA(){
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
            List<Course> currentCourseList = new ArrayList<>();

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
     */
    private void calculateGPAValues(){
        double cumGradePoints = 0;
        double cumGpaHours = 0;
        double coreGradePoints = 0;
        double coreGpaHours = 0;
        double electGradePoints = 0;
        double electGpaHours = 0;

        // Looping over each key in the HashMap
        for (Map.Entry<String, List<Course>> mapElement : utdGPAMap.entrySet()) {
            Course current = mapElement.getValue().get(getMaxCourseIndex(mapElement.getValue()));

            cumGradePoints += current.getPoints();
            cumGpaHours += current.getAttempted();

            if(coreCourseNumbers.contains(current.getCourseNumber())){
                coreGradePoints += current.getPoints();
                coreGpaHours += current.getAttempted();
            }
            else if(electCourseNumbers.contains(current.getCourseNumber())){
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
