import java.util.*;

public class Student {
    // Student Variables
    private String studentName;
    private String studentId;
    private String startDate;
    private String currentMajor;
    private Plan currentTrack;
    private List<StudentCourse> courseList;

    /**
     * Initializes the student object using basic information and creating a
     * blank list of classes
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
    }

    /**
     * This function creates a course from the input on the transcript and adds it to the
     * list of the student's courses.
     *
     * @param line This is the line of the course as read on the transcript.
     * @param semester String identifying the semester.
     * @param transfer true if the course is transfer credit, false if utd credit.
     */
    public void addCourse(String line, String semester, boolean transfer, boolean fastTrack){
        StudentCourse newCourse = new StudentCourse(line, semester, transfer, fastTrack);
        courseList.add(newCourse);
    }

    /**
     * Drives the methods that set the course types for each course
     */
    private void evaluateDegreePlan(){
        setInitialCourseTypes();
        setOptionalCore();
        setElectives();
    }


    /**
     * Sets the student's courses to their default type based on the current degree plan
     */
    private void setInitialCourseTypes(){
        for(Course course : getCourseList()) {
            if (currentTrack.isCore(course))
                setCourseType(course.courseNumber, Course.CourseType.CORE);
            else if (currentTrack.isOpt(course))
                setCourseType(course.courseNumber, Course.CourseType.OPTIONAL);
            else if (currentTrack.isPre(course))
                setCourseType(course.courseNumber, Course.CourseType.PRE);
            else if (currentTrack.isTrack(course))
                setCourseType(course.courseNumber, Course.CourseType.TRACK);
            else
                setCourseType(course.courseNumber, Course.CourseType.OTHER);
        }
    }

    /**
     * Changes the maximum number of optional courses into core courses and allows the rest to be
     * used when determining the electives.
     */
    private void setOptionalCore(){
        List<StudentCourse> coreOptList = getCourseType(Course.CourseType.OPTIONAL);
        long numOpt = currentTrack.getNumOptional();

        // Optional courses to be changed into core
        while (numOpt > 0 && coreOptList.size() != 0){
            StudentCourse maxCourse = getMaxCourseGPA(coreOptList);
            setCourseType(maxCourse.getCourseNumber(), Course.CourseType.CORE);
            coreOptList.remove(maxCourse);
            numOpt--;
        }

        // Any extra optional courses
        coreOptList.forEach(StudentCourse -> {
            setCourseType(StudentCourse.getCourseNumber(), Course.CourseType.OTHER);
        });
    }




    /**
     * Fills the electives. It separates them into 6000 level electives and additional electives
     */
    private void setElectives(){
        List<StudentCourse> otherList = getCourseType(Course.CourseType.OTHER);
        int numElectHours = 15;
        int currentElectHours = 0;

        for(int i = 0; i < otherList.size(); i++){
            StudentCourse currentCourse = otherList.get(i);
            try {
                int currentNum = Integer.parseInt(currentCourse.getCourseNumber().split(" ")[1]);

                if(currentNum >= 6000 && currentNum < 7000){
                    if (currentElectHours > numElectHours) {
                        StudentCourse min = getLeastCourseGPA(getCourseType(Course.CourseType.ELECTIVE));
                        setCourseType(min.getCourseNumber(), Course.CourseType.ADDITIONAL);
                    }
                    setCourseType(currentCourse.getCourseNumber(), Course.CourseType.ELECTIVE);
                    currentElectHours += currentTrack.getCourseHours(currentCourse.getCourseNumber());
                } else {
                    setCourseType(currentCourse.getCourseNumber(), Course.CourseType.ADDITIONAL);
                }
            } catch (NumberFormatException e){
                setCourseType(currentCourse.getCourseNumber(), Course.CourseType.ADDITIONAL);
            }


        }
    }

    /**
     * Gets the course object that has the highest GPA. This is used when deciding which optional
     * class is core or elective
     *
     * @param listOfCourses List of courses that the function will loop through.
     * @return Course object that has the highest GPA
     */
    public StudentCourse getMaxCourseGPA(List<StudentCourse> listOfCourses){
        double maxGPA = 0;
        StudentCourse maxCourse = new StudentCourse();

        for(int i = 0; i < listOfCourses.size(); i++){
            StudentCourse currentCourse = listOfCourses.get(i);

            double currentGPA = calcGPA(currentCourse.getEarned(), currentCourse.getAttempted());
            if(currentGPA > maxGPA){
                maxGPA = currentGPA;
                maxCourse = currentCourse;
            }

        }
        return maxCourse;
    }

    /**
     * Gets the course object that has the lowest GPA.
     *
     * @param listOfCourses List of courses that the function will loop through.
     * @return Course object that has the highest GPA
     */
    public StudentCourse getLeastCourseGPA(List<StudentCourse> listOfCourses){
        StudentCourse minCourse = new StudentCourse();

        if(listOfCourses.size() == 0)
            return minCourse;

        double minGPA = calcGPA(currentTrack.getCourseHours(listOfCourses.get(0).getCourseNumber()), listOfCourses.get(0).getAttempted());

        for(int i = 0; i < listOfCourses.size(); i++){
            StudentCourse currentCourse = listOfCourses.get(i);

            double currentGPA = calcGPA(currentCourse.getEarned(), currentCourse.getAttempted());
            if(currentGPA < minGPA){
                minGPA = currentGPA;
                minCourse = currentCourse;
            }

        }
        return minCourse;
    }

    /**
     * Calculates the GPA
     *
     * @param gpaPoints number of points
     * @param gpaHours number of hours
     * @return returns the gpa rounded to 3 decimal places
     */
    private double calcGPA(double gpaPoints, double gpaHours){
        double GPA = gpaPoints / gpaHours;

        double scale = Math.pow(10, 3);
        return Math.round(GPA * scale) / scale;
    }


    /**
     * Modifies the courseType of a course in the courseList
     *
     * @param number String of the course number
     * @param type Type to set the course type to
     */
    public void setCourseType(String number, Course.CourseType type){
        for(int i = 0; i < courseList.size(); i++)
            if(courseList.get(i).getCourseNumber().equals(number))
                courseList.get(i).setType(type);
    }


    /**
     * Returns a list of courses of the specified type in the courseList
     *
     * @param type target type to retrieve
     * @return List of courses of specified type
     */
    public List<StudentCourse> getCourseType(Course.CourseType type){
        List<StudentCourse> c = new ArrayList<>();
        for(StudentCourse current : courseList)
            if(current.type == type)
                c.add(current);
        return c;
    }


   /**
    * Outputs all the information to the console in a similar style to how it will
    * be displayed the final Audit PDF.
    */
    public void printStudentInformation(){
        System.out.println(studentName);
        System.out.println(studentId);
        System.out.println(startDate);
        System.out.println(currentMajor);
        System.out.println();

        courseList.forEach((c) -> c.printCourse());
    }


   /**
    * Accessor methods to be used outside the class.
    */
    public List<StudentCourse> getCourseList() { return courseList; }
    public String getStudentName(){ return studentName; }
    public String getStudentId(){ return studentId; }
    public String getStartDate(){ return startDate; }
    public String getCurrentMajor() { return currentMajor; }
    public Plan getCurrentTrack() { return currentTrack; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudentName(String studentName){ this.studentName = studentName; }
    public void setStudentId(String studentId){ this.studentId = studentId; }
    public void setStartDate(String startDate){ this.startDate = startDate; }
    public void setCurrentMajor(String currentMajor) { this.currentMajor = currentMajor; }
    public void setCurrentTrack(Plan currentTrack) {
        this.currentTrack = currentTrack;
        evaluateDegreePlan();
    }
}
