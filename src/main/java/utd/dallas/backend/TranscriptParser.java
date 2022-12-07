package utd.dallas.backend;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TranscriptParser {
    Student currentStudent;

    /**
     * This is the main function. Once we create the UI, this will be called as an object.
     * Ideally this would return a filled student object which would then be used in other
     * parts of the project
     *
     * @param inputFile Transcript
     * @throws IOException The readPDF function throws an exception if the PDF can not be read
     */
    public TranscriptParser(File inputFile) throws IOException {
        String transcript[] = readPDF(inputFile);

        // Create the student Object
        this.currentStudent = createStudent(transcript);

        // Fill the course information
        fillCourseInformation(currentStudent, transcript);

        if(currentStudent.getTranscriptList().size() == 0)
            throw new IOException();
    }

    /**
     * This function reads in PDF file and splits the text by lines
     *
     * @param inputFile file object
     * @throws IOException If there is an error reading in the PDF
     * @return An array that is seperated by each line in the transcript
     */
    private String[] readPDF(File inputFile) throws IOException{
        PDDocument document = PDDocument.load(inputFile);

        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);

        document.close();

        return text.split("\n");
    }

    /**
     * Takes the given text and checks if it matches the regular expression
     *
     * @param input this is the line from the transcript
     * @param regex String of the regular expression
     *
     * @return true/false if the regex matches the input string
     */
    private boolean checkRegex(String input, String regex){
        Pattern stringPattern = Pattern.compile(regex);
        Matcher m = stringPattern.matcher(input);
        return m.find();
    }

    /**
     * This method creates the initial student object. The only information that will be filled is the
     * student name, student id, date that the student was admitted to the program, and the current plan.
     *
     * @param transcript The entire transcript split up by lines
     * @return A student object with identifying information filled out
     */
    private Student createStudent(String transcript[]) {
        // Create initial variables
        String studentName = "";
        String studentId = "";
        String startDate = "";
        String currentMajor = "";

        for(int i = 0; i < transcript.length; i++){
            // End the loop as soon as all the information has been filled
            if (!studentName.equals("") && !studentId.equals("") && !startDate.equals(""))
                break;

            if(checkRegex(transcript[i], "Name:")){
                String temp = transcript[i].substring("Name:".length());
                studentName = temp.trim();
                continue;
            }

            if(checkRegex(transcript[i], "Student ID:")){
                studentId = transcript[i].substring("Student ID:".length());
                studentId = studentId.trim();

                // Regex to check if valid student id
                if(!studentId.matches("^[0-9]{10}$")){
                    studentId = "xxxxxxxxxx";
                }
            }

            // Checks for the first major start date
            if(checkRegex(transcript[i], "Active in Program") && checkRegex(transcript[i-1], "Master")) {
                startDate = transcript[i].substring(0, 10);      // The date format in the transcript is 10 characters long
                currentMajor = transcript[i-1].substring(9);
            }
        }

        return new Student(studentName,studentId,startDate, currentMajor);
    }

    /**
     * This function parses the course information from the transcript.
     *
     * @param currentStudent The current student object.
     * @param transcript The entire transcript split up by lines
     */
    private void fillCourseInformation(Student currentStudent, String[] transcript){
        boolean transfer = false;
        boolean fastTrack = false;
        boolean thesis = false;
        boolean graduateCourse = false;
        String transfer_text = "";
        String semester = "";

        for(int i = 0; i < transcript.length; i++){
            // Check for transfer
            if(transcript[i].contains("Transfer Credits") && !transcript[i].contains("Fast Track")) {
                if(transcript[i + 1].contains("Master Program"))
                    graduateCourse = true;
                if(graduateCourse){
                    transfer = true;
                    fastTrack = false;
                    transfer_text = "T";
                }
                continue;
            }else if(transcript[i].contains("Transfer Credit from UT Dallas Fast Track")){
                graduateCourse = true;
                transfer = true;
                fastTrack = true;
                transfer_text = "F/T";
                currentStudent.setFastTrack(true);

                continue;
            } else if(transcript[i].contains("Beginning of Graduate Record")){
                graduateCourse = true;
                transfer = false;
                fastTrack = false;
                transfer_text = "";
                continue;
            }
            if(!graduateCourse)
                continue;

            // Regex to check for semester
            if(checkRegex(transcript[i], "([0-9]{4}.+Spring)|([0-9]{4}.+Summer)|([0-9]{4}.+Fall)")){
                semester = transcript[i].substring(2,4) + transcript[i].substring(5,7).toUpperCase();
                if(semester.indexOf('F') == 2)
                    semester = semester.substring(0, 3);
                continue;
            }

            // Check for class number in the line
            if(checkRegex(transcript[i], "(\\s[0-9]{4}\\s)|(\\s[0-9][vV]([0-9]{2})\\s)|(\\s[0-9]-{3}\\s)")){
                currentStudent.addCourse(transcript[i], semester, transfer_text);
            }
        }
    }

    /**
     * Accessor methods to be used outside the class.
     */
    public Student getStudent() { return currentStudent; }
}