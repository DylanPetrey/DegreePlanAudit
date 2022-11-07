import java.io.IOException;

public class Main {
    public static void main(String args[]) throws Exception {
        String inputFilePath = "SampleTranscripts/Sample3.pdf";
        TranscriptParser transcript =  new TranscriptParser(inputFilePath);
        Student curr = transcript.getStudent();
        curr.printStudentInformation();

        Plan track = new Plan(Plan.Concentration.DATA);
        curr.setCurrentTrack(track);

        Audit auditHelper = new Audit(curr);
        auditHelper.runAudit();
    }
}
