import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        TranscriptParser transcript =  new TranscriptParser(args[0]);
        Student curr = transcript.getStudent();
        curr.printStudentInformation();

        curr.setCurrentTrack(Concentration.DATA);

        Audit auditHelper = new Audit(curr);
        auditHelper.runAudit();
        auditHelper.printGPA();
    }
}
