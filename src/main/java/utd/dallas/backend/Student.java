package utd.dallas.backend;

import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Student {
    // Student Variables
    private String studentName;
    private String studentId;
    private String startDate;
    private String currentMajor;
    private String currentTrack;
    private String graduation;
    private boolean fastTrack;
    private boolean thesis;

    Pair<Boolean, StudentCourse>[] thesisCourses = new Pair[3];



    // Student objects
    private final Plan currentPlan;
    private List<StudentCourse> courseList;
    private final List<StudentCourse> transcriptList;


    /**
     * Creates empty student object. Used when not uploading transcript
     */
    public Student(){
        this.studentName = "";
        this.studentId = "";
        this.startDate = "";
        this.currentMajor = "";
        fastTrack = false;
        thesis = false;

        courseList = new ArrayList<>();
        transcriptList = new ArrayList<>();
        currentPlan = new Plan();
    }

    /**
     * Initializes the student object using basic information and creating a blank list of courses
     *
     * @param studentName Name of the student
     * @param studentId ID number of the student
     * @param startDate Date the student was enrolled to the masters program
     * @param currentMajor current major of the student (undergraduate, masters, etc.)
     */
    public Student(String studentName, String studentId, String startDate, String currentMajor){
        this.studentName = studentName;
        this.studentId = studentId;
        this.startDate = startDate;
        this.currentMajor = currentMajor;

        courseList = new ArrayList<>();
        transcriptList = new ArrayList<>();
        currentPlan = new Plan();
    }

    /**
     * Adds a course to the courseList.
     *
     * @param newCourse StudentCourse object to be added to the courseList
     */
    public void addCourse(StudentCourse newCourse){
        courseList.add(newCourse);
    }

    /**
     * This function adds courses from the transcript.
     *
     * @param line This is the line of the course as read on the transcript.
     * @param semester String identifying the semester.
     * @param transfer true if the course is transfer credit, false if utd credit.
     */
    public void addCourse(String line, String semester, String transfer){
        StudentCourse newCourse = new StudentCourse(line, semester, transfer);
        try {
            newCourse.setCourseTitle(currentPlan.getCourseTitle(newCourse.getCourseNumber()));
            newCourse.setHours(currentPlan.getCourseHours(newCourse.getCourseNumber()));
        } catch (Exception ignore){ }

        transcriptList.add(newCourse);
    }

    /**
     * Changes the current course plan and updates the courseList accordingly.
     *
     * @param concentration New degree plan that the student is switching to
     */
    public void setCurrentPlan(Plan.Concentration concentration) {
        this.currentPlan.setConcentration(concentration, thesis);

        resetCourseList();
        fillFromTranscript();
        setInitialCourseTypes();
    }



    /**
     * Resets the courseList values and fills in courses from the degreePlan
     */
    public void resetCourseList(){
        this.courseList = new ArrayList<>();
        currentPlan.getCourseOfType(Course.CourseType.CORE).forEach(course -> {
            courseList.add(new StudentCourse(course.getCourseNumber(), course.getCourseTitle(), course.getHours(), course.getType()));
        });
        currentPlan.getCourseOfType(Course.CourseType.OPTIONAL).forEach(course -> {
            courseList.add(new StudentCourse(course.getCourseNumber(), course.getCourseTitle(), course.getHours(), course.getType()));
        });
        currentPlan.getCourseOfType(Course.CourseType.ELECTIVE).forEach(course -> {
            courseList.add(new StudentCourse(course.getCourseNumber(), course.getCourseTitle(), course.getHours(), course.getType()));
        });
        currentPlan.getCourseOfType(Course.CourseType.ADDITIONAL).forEach(course -> {
            courseList.add(new StudentCourse(course.getCourseNumber(), course.getCourseTitle(), course.getHours(), course.getType()));
        });
    }

    /**
     * Fills in courses from the transcript
     */
    public void fillFromTranscript() {
        HashMap<String, List<String>> filledCourses = new HashMap<>();
        for (StudentCourse trans : transcriptList) {
            if(!filledCourses.getOrDefault(trans.getCourseNumber(), new ArrayList<>()).contains(trans.getSemester())){
                fillCourse(filledCourses, trans);
                filledCourses.compute(trans.getCourseNumber(), (id, sem) -> sem != null ? sem : new ArrayList<>())
                        .add(trans.getSemester());
            }
        }
    }

    private void setInitialCourseTypes() {
        for (Course course : courseList) {
            if (currentPlan.isCore(course))
                course.setType(Course.CourseType.CORE);
            else if (currentPlan.isOpt(course))
                course.setType(Course.CourseType.OPTIONAL);
            else if (currentPlan.isElect(course))
                course.setType(Course.CourseType.ELECTIVE);
            else if (currentPlan.isAdd(course))
                course.setType(Course.CourseType.ADDITIONAL);
            else if (currentPlan.isPre(course) || currentPlan.isTrack(course))
                course.setType(Course.CourseType.PRE);
            else
                course.setType(Course.CourseType.OTHER);
        }
    }

    /**
     * Fills in existing values of the courseList from the transcript
     */
    private void fillCourse(HashMap<String, List<String>> filledCourses, StudentCourse trans) {
        boolean found = false;
        for (StudentCourse course : courseList) {
            if (course.equals(trans) && !filledCourses.getOrDefault(course.getCourseNumber(), new ArrayList<>()).contains(course.getSemester())) {
                course.setCourseVariables(trans);
                found = true;
                break;
            }
        }
        if (!found) {
            courseList.add(trans);
        }
    }

    /**
     * Creates a list of all the courses of a specific type
     *
     * @param type target type to retrieve
     * @return list of courses of specified type
     */
    public List<StudentCourse> getCourseType(Course.CourseType type){
        List<StudentCourse> course = new ArrayList<>();
        for(StudentCourse current : courseList)
            if(current.type == type)
                course.add(current);
        return course;
    }




    /**
     * Creates a list of all the nonempty courses
     *
     * @return list of courses of specified type
     */
    public List<StudentCourse> getCleanCourseList(){
        List<StudentCourse> cleanCourses = new ArrayList<>();
        for(StudentCourse course : courseList)
            if(!course.isEmpty())
                cleanCourses.add(course);
        return cleanCourses;
    }

    public void dropEmpty(){
        List<StudentCourse> emptyCourses = new ArrayList<>();
        for(StudentCourse course : courseList)
            if(course.isEmpty())
                emptyCourses.add(course);
        courseList.removeAll(emptyCourses);
    }


    /**
     * Prints all the information in a similar style to how it will
     * be displayed the final Audit PDF.
     */
    public void printStudentInformation(){
        System.out.println(studentName);
        System.out.println("Master");

        System.out.println(studentId);
        System.out.println(startDate);
        System.out.println(currentMajor);
        System.out.println(currentPlan.getConcentration().toString());
        System.out.println();
    }

    public List<StudentCourse> getThesisCourses(){
        List<StudentCourse> thesisList = new ArrayList<>();
        for (StudentCourse course: courseList) {
            if(course.getCourseNumber().contains("6V81") || course.getCourseNumber().contains("6V98")){
                thesisList.add(course);
            }
        }
        return thesisList;
    }

    /**
     * Accessor methods to be used outside the class.
     */
    public List<StudentCourse> getCourseList() { return courseList; }
    public List<StudentCourse> getTranscriptList() { return transcriptList; }
    public Plan getCurrentPlan() { return currentPlan; }
    public String getStudentName(){ return studentName; }
    public String getStudentId(){ return studentId; }
    public String getStartDate(){ return startDate; }
    public String getCurrentMajor() { return currentMajor; }
    public String getGraduation() { return graduation;}
    public boolean isFastTrack() { return fastTrack; }
    public boolean isThesis() { return thesis; }

    public String getSimpleName() {
        String names[] = studentName.split(" ");
        
        if(names.length < 2)
            return studentName;
        else
            return names[0] + names[names.length-1];
    
    }



    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudentName(String studentName){ this.studentName = studentName; }
    public void setStudentId(String studentId){ this.studentId = studentId; }
    public void setStartDate(String startDate){ this.startDate = startDate; }
    public void setCurrentMajor(String currentMajor) { this.currentMajor = currentMajor; }
    public void setGraduation(String graduation) { this.graduation = graduation;}
    // Set thesis from transcript
    public void setThesis(boolean thesis) {
        this.thesis = thesis;
    }

    // Set thesis from UI and populate into plan class
    public void setThesis(boolean thesis, Course core, Course elect, Course add) {
        this.thesis = thesis;

        if(thesis) {
            removeThesis(core, elect, add);
            addThesis(core, elect, add);
        } else {
            removeThesis(core, elect, add);
        }
    }
    private void addThesis(Course core, Course elect, Course add){
        currentPlan.addThesisCourse(core, elect, add);
        thesisCourses[0] = needsToBeAdded(Course.CourseType.OTHER, core, Course.CourseType.CORE);
        thesisCourses[1] = needsToBeAdded(Course.CourseType.OTHER, elect, Course.CourseType.ELECTIVE);
        thesisCourses[2] = needsToBeAdded(Course.CourseType.OTHER, add, Course.CourseType.ADDITIONAL);
        for(int i = 0; i< thesisCourses.length; i++)
            if(thesisCourses[i].getKey())
                courseList.add(thesisCourses[i].getValue());
    }

    private Pair<Boolean, StudentCourse> needsToBeAdded(Course.CourseType initial, Course course, Course.CourseType target) {
        for (StudentCourse targetCourse : courseList) {
            if (targetCourse.getType().equals(initial) && targetCourse.getCourseNumber().equals(course.getCourseNumber())) {
                targetCourse.setType(target);
                return new Pair<>(false, targetCourse);
            }
        }
        return new Pair<>(true, new StudentCourse(course, target));
    }

    private void removeThesis(Course core, Course elect, Course add){
        currentPlan.removeThesisCourse(core, elect, add);
        List<StudentCourse> removeList = new ArrayList<>();
        for(int i = 0; i< thesisCourses.length; i++) {
            try {
                if (needsToBeRemoved(thesisCourses[i].getValue()))
                    removeList.add(thesisCourses[i].getValue());
            } catch (NullPointerException ignore) {}
        }
        courseList.removeAll(removeList);
    }

    private boolean needsToBeRemoved(Course course) {
        for (StudentCourse targetCourse : courseList) {
            if (targetCourse.getCourseNumber().equals(course.getCourseNumber()) && !targetCourse.getSemester().isEmpty()) {
                targetCourse.setType(Course.CourseType.OTHER);
                return false;
            }
        }
        return true;
    }

    public void setFastTrack(boolean fastTrack) { this.fastTrack = fastTrack;}
}