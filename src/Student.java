import java.util.ArrayList;
import java.util.List;

public class Student {
    String studentName;
    int studentId;
    String startDate;
    List<Course> courseList;


    public Student(String studentName, int studentId, String startDate){
        this.studentName = studentName;
        this.studentId = studentId;
        this.startDate = startDate;
        this.courseList = new ArrayList<Course>();
    }

    // Input the line and then create a course object
    public void addCourse(String line, String semester, boolean transfer){
        System.out.println(line);

        //courseList.add(new Course(line, semester, transfer));
    }
}
