import java.io.IOException;

public class main {
    public static void main(String args[]) throws IOException {
        ReadTranscript transcript =  new ReadTranscript(args[0]);
        Student curr = transcript.getCurrentStudent();

        GPA gpaCalc = new GPA(curr.getCourseList());
        gpaCalc.calculateGPA();

        System.out.println(gpaCalc.getCombinedGPA());
    }
}
