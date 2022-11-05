import java.io.IOException;

public class Main {
    public static void main(String args[]) throws Exception {


        String st = args[0];
        if(args.length == 0)
            st = "SampleTranscripts/Sample.pdf";
        TranscriptParser transcript = new TranscriptParser(st);
        
        Student curr = transcript.getStudent();
        curr.printStudentInformation();

        Plan track = new Plan(Plan.Concentration.DATA);
        curr.setCurrentTrack(track);
        Form f = new Form(curr);
        f.fillForm(Plan.Concentration.DATA);
        Audit auditHelper = new Audit(curr);
        auditHelper.runAudit();
    }
}
