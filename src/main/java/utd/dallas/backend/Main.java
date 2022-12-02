package utd.dallas.backend;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String args[]) throws Exception {
        String inputFilePath = "src/main/resources/utd/dallas/backend/SampleTranscripts/Sample3.pdf";
        File inputFile = new File(inputFilePath);
        TranscriptParser transcript =  new TranscriptParser(inputFile);
        Student curr = transcript.getStudent();
        curr.setCurrentPlan(Plan.Concentration.TRADITIONAL);
        curr.printStudentInformation();
        FormInt.print(curr);

        
        System.out.println(Arrays.toString(curr.getCurrentPlan().getUtdCatalogCourseNums().toArray()));

        new Audit(curr);
    }
}
