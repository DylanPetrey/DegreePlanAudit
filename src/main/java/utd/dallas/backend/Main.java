package utd.dallas.backend;

public class Main {
    public static void main(String args[]) throws Exception {
        String transcriptFilename = "src/main/resources/utd/dallas/backend/SampleTranscripts/Sample.pdf";
        TranscriptParser transcript =  new TranscriptParser(transcriptFilename);
        Student curr = transcript.getStudent();
        curr.printStudentInformation();

        Plan track = new Plan("Data Science");
        curr.setCurrentTrack(track);

        Audit auditHelper = new Audit(curr);
        auditHelper.runAudit();
    }
}
