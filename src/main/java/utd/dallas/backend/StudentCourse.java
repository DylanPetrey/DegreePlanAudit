package utd.dallas.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentCourse extends Course {
    private String letterGrade = "";
    private String semester = "";
    private double attempted = 0;
    private double earned = 0;
    private double points = 0;
    private String transfer = "";
    private boolean isWaived = false;
    private boolean fromTranscript = false;

    public StudentCourse(){
    }

    public StudentCourse(StudentCourse another) {
        super((Course) another);
        this.letterGrade = another.letterGrade;
        this.semester = another.semester;
        this.attempted = another.attempted;
        this.earned = another.earned;
        this.points = another.earned;
        this.transfer = another.transfer;
        this.isWaived = another.isWaived;
        this.fromTranscript = another.fromTranscript;

    } 

    public StudentCourse(CourseType type){
        this.type = type;
    }

    public StudentCourse(String id, String title, String hours, CourseType type) {
        setCourseNumber(id);
        setCourseTitle(title);
        setHours(hours);
        this.type = type;
    }

    public StudentCourse(String input, String semester, String transfer) {
        this.semester = semester;
        this.transfer = transfer;
        this.fromTranscript = true;

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
        setCourseNumber(tokens[0] + " " + tokens[1]);

        // Fills in the course title and checks if there is a letter grade.
        // If there is not a letter grade, then the course is in the current semester
        if (!tokens[tokens.length - 2].matches("[-+]?[0-9]*\\.?[0-9]+")) {
            setCourseTitle(String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 4)));
            letterGrade = tokens[tokens.length - 2];
            tokens = Arrays.copyOfRange(tokens, tokens.length - 4, tokens.length);
        } else {
            setCourseTitle(String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 3)));
            letterGrade = "";
            tokens = Arrays.copyOfRange(tokens, tokens.length - 3, tokens.length);
        }

        attempted = Double.parseDouble(tokens[0]);
        setHours(String.valueOf((int) attempted));
        earned = Double.parseDouble(tokens[1]);
        points = Double.parseDouble(tokens[tokens.length - 1]);
    }

    /**
     * Creates a string of the course similar to how it will look on the final audit pdf
     *
     * @return returns a string of the course
     */
    @Override
    public String toString() {
        String transferText;
        if(isWaived)
            transferText = "Waived";
        else
            transferText = transfer;
        return  getCourseNumber() + " " +
                getCourseTitle() + " " +
                getHours() + " " +
                semester + " " +
                transferText + " " +
                letterGrade;
    }

    public boolean isEmpty(){
        return getCourseNumber().equals("");
    }
    public String getSemester() { return semester; }
    public String getLetterGrade() { return letterGrade; }
    public double getAttempted() { return attempted; }
    public double getEarned() { return earned; }
    public double getPoints() {return points; }
    public String getTransfer() { return transfer; }
    public boolean isFromTranscript() { return fromTranscript; }
    public boolean isWaived() { return isWaived; }



    public void setSemester(String semester) { this.semester = semester; }
    public void setTransfer(String transfer) { this.transfer = transfer; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public void setWaived(boolean waived) { this.isWaived = waived; }

    public void setCourseVariables(StudentCourse newCourse){
        setCourseNumber(newCourse.getCourseNumber());
        setHours(newCourse.getHours());
        semester = newCourse.getSemester();
        attempted = newCourse.getAttempted();
        points = newCourse.getPoints();
        earned = newCourse.getEarned();
        letterGrade = newCourse.getLetterGrade();
        isWaived = newCourse.isWaived();
        fromTranscript = newCourse.isFromTranscript();
    }
}
