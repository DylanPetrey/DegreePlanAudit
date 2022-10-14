import java.io.IOException;

public class main {
    public static void main(String args[]) throws IOException {
        TranscriptParser transcript =  new TranscriptParser(args[0]);
        Student curr = transcript.getStudent();

        Audit auditHelper = new Audit(curr.getCourseList());
        auditHelper.calculateGPA();

        System.out.println(auditHelper.getCombinedGPA());
    }
}
