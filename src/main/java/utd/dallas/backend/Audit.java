package utd.dallas.backend;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Audit {
    // Student variables
    private final Student currentStudent;
    private final List<StudentCourse> filledCourses;

    // List of courses divided up by type
    private final List<StudentCourse> coreList = new ArrayList<>();
    private final List<StudentCourse> electList = new ArrayList<>();
    private final List<StudentCourse> preList = new ArrayList<>();
    List<StudentCourse> currentSemester = new ArrayList<>();


    // GPA Variables
    private double combinedGPA = 0;
    private double coreGPA = 0;
    private double electiveGPA = 0;

    // Requirement Variables
    private final double MIN_CORE_GPA = 3.19;
    private final double MIN_ELECT_GPA = 3.0;
    private final double MIN_OVRALL_GPA = 3.0;
    private final int REQUIRED_CORE_HOURS = 15;
    private final int REQUIRED_ELECTIVE_HOURS = 15;
    private final int MIN_ADD_ELECTIVE_HOURS = 3;

    HashMap<String, String> WordReplace = new HashMap<>();


    /**
     * Performs the audit when created
     *
     * @param currentStudent current student object
     */
    public Audit(Student currentStudent){

        this.currentStudent = currentStudent;
        this.filledCourses = currentStudent.getCleanCourseList();
        calculateGPAValues();
        printGPA();
        findAndReplace();
    }

    /**
     * This function calculates the core, elective, and cumulative GPA values
     */
    private void calculateGPAValues(){
        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.CORE)
                .forEach(coreList::add);
        coreGPA = calcGPA(coreList);

        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.ELECTIVE || studentCourse.getType() == Course.CourseType.ADDITIONAL)
                .forEach(electList::add);
        electiveGPA = calcGPA(electList);

        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.PRE)
                .forEach(preList::add);

        combinedGPA = calcGPA(filledCourses);
    }


    /**
     * Calculates the gpa for the courseList
     *
     * @param courseList list of courses to get the GPA from
     * @return gpa value rounded to 3 digits
     */
    private double calcGPA(List<StudentCourse> courseList){
        final double A_GRADEPTS = 4.000;
        final double A_MINUS_GRADEPTS = 3.670;
        final double B_PLUS_GRADEPTS = 3.330;
        final double B_GRADEPTS = 3.000;
        final double B_MINUS_GRADEPTS = 2.670;
        final double C_PLUS_GRADEPTS = 2.330;
        final double C_GRADEPTS = 2.000;
        final double F_GRADEPTS = 0.000;
        AtomicReference<Double> totalPoints = new AtomicReference<>((double) 0);
        AtomicReference<Double> totalHours = new AtomicReference<>((double) 0);

        courseList.forEach(studentCourse -> {
            String letterGrade = studentCourse.getLetterGrade();
            int finalCurrentHour = Integer.parseInt(studentCourse.getHours());
            if (letterGrade.equalsIgnoreCase("A") || letterGrade.equalsIgnoreCase("A+"))
                totalPoints.updateAndGet(v -> (v+(A_GRADEPTS* finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("A-"))
                totalPoints.updateAndGet(v -> (v+(A_MINUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("B+"))
                        totalPoints.updateAndGet(v -> (v+(B_PLUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("B"))
                        totalPoints.updateAndGet(v -> (v+(B_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("B-"))
                        totalPoints.updateAndGet(v -> (v+(B_MINUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("C+"))
                totalPoints.updateAndGet(v -> (v+(C_PLUS_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("C"))
                totalPoints.updateAndGet(v -> (v+(C_GRADEPTS*finalCurrentHour)));
            else if (letterGrade.equalsIgnoreCase("F") || letterGrade.equalsIgnoreCase("D+") || letterGrade.equalsIgnoreCase("D") || letterGrade.equalsIgnoreCase("D-"))
                totalPoints.updateAndGet(v -> (v+(F_GRADEPTS*finalCurrentHour)));
            else
                return;

            totalHours.updateAndGet(v -> (v+finalCurrentHour));
        });

        double GPA = totalPoints.get() / totalHours.get();
        double scale = Math.pow(10, 3);
        return Math.round(GPA * scale) / scale;
    }


    /**
     * Prints the GPA as seen on the sample audit
     */
    public void printGPA(){
        WordReplace.put("$$name$$", currentStudent.getStudentName());
        WordReplace.put("$$id$$", currentStudent.getStudentId());
        WordReplace.put("$$plan$$", "Master") ;
        WordReplace.put("$$major$$", currentStudent.getCurrentMajor());
        WordReplace.put("$$track$$", currentStudent.getCurrentPlan().getConcentration().toString());

        String coreGPAString = String.valueOf(calcGPA(coreList));
        String electiveGPAString = String.valueOf(calcGPA(electList));
        String combinedGPAString = String.valueOf(calcGPA(filledCourses));

        WordReplace.put("$$coregpa$$", coreGPAString);
        WordReplace.put("$$electivegpa$$", electiveGPAString);
        WordReplace.put("$$combinedgpa$$", combinedGPAString);

        WordReplace.put("$$core$$", printCourses(coreList));
        WordReplace.put("$$elective$$", printCourses(electList));

        WordReplace.put("$$prereq$$", printPre());
        WordReplace.put("$$outstanding$$", getOutstanding());
    }

    public String getOutstanding(){
        String res = "Student must pass ";
        fillCurrentSemester();
        res += printCourses(currentSemester);
        return res;
    }

    private void fillCurrentSemester(){
        for(StudentCourse s : currentStudent.getCourseList()) {
            if(!s.getSemester().isEmpty() && s.getLetterGrade().isEmpty())
                currentSemester.add(s);
        }
    }

    private String printPrereq(StudentCourse pre){
        String res = "";
        if(pre.isWaived())
            res = pre.getCourseNumber() + " - " + "Waived";
        else if(!pre.getSemester().isEmpty() && isPassingGrade(pre.getLetterGrade())){
            res = pre.getCourseNumber() + " - " + pre.getSemester();
        }
        else
            res = pre.getCourseNumber() + " - " + "Not satisfied";
        return res;
    }

    private String printCourses(List<StudentCourse> courseList){
        String res = "";
        for(int i = 0; i < courseList.size(); i++){
            res += courseList.get(i);
            if(i < courseList.size()-1)
                res += ", ";
            else
                res += "\n";
        }
        return res;
    }

    private String printPre(){
        String res = "";
        if(preList.size() == 0)
            res += "N/A";
        for(StudentCourse course : preList)
            res += printPrereq(course) + "\n";
        System.out.println(res);
        return res;
    }

    private boolean isPassingGrade(String grade){
        Pattern stringPattern = Pattern.compile("(^[A-C].?)|P|CR");
        Matcher m = stringPattern.matcher(grade);
        return m.find();
    }

    private void findAndReplace() {
        String pathOriginal = "src/main/resources/utd/dallas/backend/SampleAudit/";
        String templateDoc = "EmptyReport.docx";
        try {
            // finds the path of the operating system temp folder to create a temporary file
            String tempPath = System.getenv("TEMP") + "\\temp.docx";
            Path dirOrigem = Paths.get(pathOriginal + templateDoc);
            Path dirDestino = Paths.get(tempPath);
            Files.copy(dirOrigem, dirDestino, StandardCopyOption.REPLACE_EXISTING); // copy the template to temporary
            // directory

            try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(tempPath))) {
                for (XWPFParagraph p : doc.getParagraphs()) {
                    List<XWPFRun> runs = p.getRuns();
                    if (runs != null) {
                        for (XWPFRun r : runs) {
                            for (String key : WordReplace.keySet()) {
                                try {
                                    String text = r.getText(0);
                                    if (text != null && text.contains(key)) {
                                        text = text.replace(key, WordReplace.getOrDefault(key, ""));//your content
                                        System.out.println(text);
                                        r.setText(text, 0);
                                    }
                                } catch (Exception notFound) { }
                            }
                        }
                    }
                }
                doc.write(new FileOutputStream(pathOriginal + currentStudent.getStudentName().replace(" ", "") + templateDoc));
                doc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}