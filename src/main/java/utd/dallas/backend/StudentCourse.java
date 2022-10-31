package utd.dallas.backend;

import java.util.Arrays;

public class StudentCourse extends Course {
    private String letterGrade = "";
    private String semester = "";
    private double attempted = 0;
    private double earned = 0;
    private double points = 0;
    private boolean isTransfer = false;
    private boolean isWaived = false;

    public StudentCourse(){

    }

    public StudentCourse(String input, String semester, boolean transfer) {
        this.semester = semester;
        this.isTransfer = transfer;

        processInput(input);
    }

    /**
     * Parses through the text and fills in the course information into the object.
     *
     * @param input This is the line of the course as read on the transcript.
     */
    private void processInput(String input) {
        // utd.dallas.backend.Course name
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


    public String getSemester() { return semester; }
    public String getLetterGrade() { return letterGrade; }
    public double getAttempted() { return attempted; }
    public double getEarned() { return earned; }
    public double getPoints() {return points; }
    public boolean isTransfer() { return isTransfer; }
    public boolean isWaived() { return isWaived; }

    public void setSemester(String semester) { this.semester = semester; }
    public void setTransfer(boolean transfer) { isTransfer = transfer; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public void setAttempted(double attempted) { this.attempted = attempted; }
    public void setPoints(double points) { this.points = points; }
    public void setEarned(double earned) { this.earned = earned; }
}
