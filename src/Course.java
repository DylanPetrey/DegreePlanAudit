import java.util.Arrays;

public class Course {
    private String courseNumber;
    private String courseDescription;
    private String letterGrade;
    private String semester;
    private double attempted;
    private double earned;
    private double points;

    private boolean isTransfer;
    private boolean isWaved;
    private boolean isCore;
    private boolean isElective;
    private boolean isPre;

    /**
     * Initializes the course object and then fills in the information.
     *
     * @param input    This is the line of the course as read on the transcript.
     * @param semester String identifying the semester.
     * @param transfer true if the course is transfer credit, false if utd credit.
     */
    public Course(String input, String semester, boolean transfer) {
        this.courseNumber = "";
        this.courseDescription = "";
        this.attempted = 0;
        this.earned = 0;
        this.letterGrade = "";
        this.points = 0;

        this.semester = semester;
        this.isTransfer = transfer;
        this.isWaved = false;

        processInput(input);
    }

    /**
     * Parses through the text and fills in the course information into the object.
     *
     * @param input This is the line of the course as read on the transcript.
     */
    private void processInput(String input) {
        // Course name
        String[] tokens = input.split("\\s+");
        courseNumber = tokens[0] + " " + tokens[1];

        // Fills in the course title and checks if there is a letter grade.
        // If there is not a letter grade, then the course is in the current semester
        if (!tokens[tokens.length - 2].matches("[-+]?[0-9]*\\.?[0-9]+")) {
            courseDescription = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 4));
            letterGrade = tokens[tokens.length - 2];
            tokens = Arrays.copyOfRange(tokens, tokens.length - 4, tokens.length);
        } else {
            courseDescription = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 3));
            letterGrade = "X";
            tokens = Arrays.copyOfRange(tokens, tokens.length - 3, tokens.length);
        }

        attempted = Double.parseDouble(tokens[0]);
        earned = Double.parseDouble(tokens[1]);
        points = Double.parseDouble(tokens[tokens.length - 1]);
    }

    /**
     * Prints the course to the console
     */
    public void printCourse() {
        System.out.println(this.toString());
    }

    /**
     * Creates a string of the course similar to how it will look on the final audit pdf
     *
     * @return returns a string of the course
     */
    @Override
    public String toString() {
        String semesterOutput = (isTransfer) ? "X " + semester : semester + " X";

        return  courseNumber + " " +
                courseDescription + " " +
                semesterOutput + " " +
                letterGrade;
    }

    /**
     * Accessor methods to be used outside the class.
     */
    public String getCourseNumber(){ return courseNumber; }
    public String getCourseDescription() { return courseDescription; }
    public String getSemester() { return semester; }
    public String getLetterGrade() { return letterGrade; }
    public double getAttempted() { return attempted; }
    public double getEarned() { return earned; }
    public double getPoints() {return points; }
    public boolean isTransfer() { return isTransfer; }
    public boolean isWaved() { return isWaved; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setCourseNumber(String courseNumber) { this.courseNumber = courseNumber; }
    public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setTransfer(boolean transfer) { isTransfer = transfer; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public void setAttempted(double attempted) { this.attempted = attempted; }
    public void setPoints(double points) { this.points = points; }
    public void setEarned(double earned) { this.earned = earned; }

    /**
     * Methods to toggle the isWaved boolean
     * can be used on the form as checkboxes
     */
    public void waive(){ isWaved = true; }
    public void unwaive(){ isWaved = false; }
}
