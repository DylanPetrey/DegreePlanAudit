import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadTranscript {
    public static void main(String args[]) throws IOException {
        // Read the input pdf
        File file = new File(args[0]);
        PDDocument document = PDDocument.load(file);

        // Extract pdf text
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);

        // Close the document
        document.close();

        // Print the read text
        System.out.println(text);

        // Split the text into lines
        String[] lines = text.split("\n");

        // Get student's personal information
        // Create initial variables
        String studentName = "";
        int studentId = -1;
        String startDate = "";

        // Loop over each line searching for key phrases to identify the information
        for(int i = 0; i < lines.length; i++){
            // Check to end the loop early
            if (!studentName.equals("") && studentId != -1 && !startDate.equals("")) {
                System.out.println(studentName);
                System.out.println(studentId);
                System.out.println(startDate);
                break;
            }

            if(lines[i].indexOf("Name:") != -1){
                String temp = lines[i].substring("Name:".length());
                studentName = temp.trim();
                continue;
            }

            if(lines[i].indexOf("Student ID:") != -1){
                String temp = lines[i].substring("Student ID:".length());
                try{
                    studentId = Integer.parseInt(temp.trim());
                    continue;
                }
                catch (NumberFormatException ex){
                    ex.printStackTrace();
                }
            }

            if(lines[i].indexOf("Active in Program") != -1) {
                startDate = lines[i].substring(0, 10);      // The date format in the transcript is 10 characters long
            }
        }

        Student currentStudent = new Student(studentName,studentId,startDate);

        // initialize course variables
        String semester = "";
        String[] seasons = {"Fall", "Summer", "Spring"};
        boolean transfer = false;
        int start_pointer = 0;
        int left_pointer = 0;


        // Loop over each line and add the classes
        for(int i = 0; i < lines.length; i++){
            // Check for transfer
            if(lines[i].indexOf("Transfer Credits") != -1){
                transfer = true;
                System.out.println(lines[i]);
                continue;
            } else if(lines[i].indexOf("Beginning of") != -1 && lines[i].indexOf("Record") != 1){
                transfer = false;
                System.out.println(lines[i]);
                continue;
            }


            // Check for current semester
            if(Arrays.stream(seasons).anyMatch(lines[i]::contains)){
                semester = lines[i].substring(2,4) + lines[i].substring(5,7).toUpperCase();
                if(semester.indexOf('F') == 2)
                    semester = semester.substring(0, 3);
                System.out.println(semester);
                continue;
            }

            // Regex to find classes
            // Full course number (4355):           \s[0-9]{4,4}\s
            // Research course number (4V34):       \s[0-9][vV]([0-9]{2,2})\s
            // Transfer dashes (1---):              \s[0-9][-]{3,3}\s
            // All together:                  (\s[0-9]{4,4}\s)|(\s[0-9][vV]([0-9]{2,2})\s)|(\s[0-9][-]{3,3}\s)
            Pattern stringPattern = Pattern.compile("(\\s[0-9]{4}\\s)|(\\s[0-9][vV]([0-9]{2})\\s)|(\\s[0-9]-{3}\\s)");
            Matcher m = stringPattern.matcher(lines[i]);
            boolean isClass = m.find();
            if(isClass){
                currentStudent.addCourse(lines[i], semester, transfer);
            }
        }


    }
}