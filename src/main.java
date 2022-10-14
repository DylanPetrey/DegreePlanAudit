import java.io.IOException;

public class main {
    public static void main(String args[]) throws IOException {
        transcriptParser transcript =  new transcriptParser(args[0]);
        Student curr = transcript.getStudent();

        Audit auditHelper = new Audit(curr.getCourseList());
        auditHelper.calculateGPA();

        System.out.println(auditHelper.getCombinedGPA());
    }
}
