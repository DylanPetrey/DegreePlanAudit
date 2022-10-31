package utd.dallas.backend;

import javafx.fxml.FXMLLoader;
import utd.dallas.frontend.HelloApplication;

import java.net.URL;

public class Main {
    public static void main(String args[]) throws Exception {
        String transcriptFilename = "/home/dylan/Documents/GitHub/DegreePlanAudit/src/main/resources/utd/dallas/backend/SampleTranscripts/Sample.pdf";
        TranscriptParser transcript =  new TranscriptParser(transcriptFilename);
        Student curr = transcript.getStudent();
        curr.printStudentInformation();

        Plan track = new Plan("Data Science");
        curr.setCurrentTrack(track);

        Audit auditHelper = new Audit(curr);
        auditHelper.runAudit();
    }
}
