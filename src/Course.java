public class Course {
    String courseName;
    String courseDescription;
    boolean isTransfer;
    float gpa;
    String semester;

    public Course(String input, String semester, boolean transfer){
        this.semester = semester;
        this.isTransfer = isTransfer;

        processInput(input);
    }

    private void processInput(String input){


    }


}
