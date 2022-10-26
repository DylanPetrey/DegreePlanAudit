import java.io.IOException;

public class Main {
    public static void main(String args[]) throws Exception {
        TranscriptParser transcript =  new TranscriptParser(args[0]);
        Student curr = transcript.getStudent();
        curr.printStudentInformation();

        Plan track = new Plan("Data Science");
        curr.setCurrentTrack(track);

        Audit auditHelper = new Audit(curr);
        auditHelper.runAudit();
    }
}
