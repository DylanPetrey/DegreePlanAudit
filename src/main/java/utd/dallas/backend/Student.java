package utd.dallas.backend;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Student {
    // Student Variables
    private String studentName;
    private String studentId;
    private String startDate;
    private String currentMajor;
    private String graduation;
    private boolean fastTrack;
    private boolean thesis;


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
        this.currentPlan.setConcentration(concentration);

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
            courseList.add(new StudentCourse(course.getCourseNumber(), course.getCourseTitle(), course.getHours(), Course.CourseType.OPTIONAL));
        });
    }

    /**
     * Fills in courses from the transcript
     */
    public void fillFromTranscript(){
        transcriptList.forEach(studentCourse -> {
            if (courseList.contains(studentCourse))
                setFormListValue(studentCourse);
            else
                courseList.add(studentCourse);
        });
    }

    /**
     * Fills in existing values of the courseList from the transcript
     */
    public void setFormListValue(StudentCourse value){
        courseList.forEach(studentCourse -> {
            if (studentCourse.equals(value))
                studentCourse.setCourseVariables(value);
        });
    }

    /**
     * Assigns default course types from transcript based on degree plan
     */
    private void setInitialCourseTypes(){
        for(Course course : courseList) {
            if (currentPlan.isCore(course))
                setCourseType(course, Course.CourseType.CORE);
            else if (currentPlan.isOpt(course))
                setCourseType(course, Course.CourseType.OPTIONAL);
            else if (currentPlan.isPre(course) || currentPlan.isTrack(course))
                setCourseType(course, Course.CourseType.PRE);
            else
                setCourseType(course, Course.CourseType.OTHER);
        }
    }

    /**
     * Modifies the courseType of a course in the courseList
     *
     * @param course course to modify
     * @param type new course type for the course
     */
    public void setCourseType(Course course, Course.CourseType type){
        for (StudentCourse studentCourse : courseList)
            if (studentCourse.equals(course))
                studentCourse.setType(type);
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

    /**
     * Prints all the information in a similar style to how it will
     * be displayed the final Audit PDF.
     */
    public void printStudentInformation(){
        System.out.println(studentName);
        System.out.println(studentId);
        System.out.println(startDate);
        System.out.println(currentMajor);

        courseList.forEach(StudentCourse-> {
            if(!StudentCourse.isEmpty()) {
                System.out.print(StudentCourse.toString());
                System.out.println(" " + StudentCourse.getType());
            }
        });
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



    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudentName(String studentName){ this.studentName = studentName; }
    public void setStudentId(String studentId){ this.studentId = studentId; }
    public void setStartDate(String startDate){ this.startDate = startDate; }
    public void setCurrentMajor(String currentMajor) { this.currentMajor = currentMajor; }
    public void setGraduation(String graduation) { this.graduation = graduation;}
    public void setThesis(boolean thesis) { this.thesis = thesis;}
    public void setFastTrack(boolean fastTrack) { this.fastTrack = fastTrack;}
}