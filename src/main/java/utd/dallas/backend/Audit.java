package utd.dallas.backend;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Audit {
    // Student variables
    private final Student currentStudent;
    private final List<StudentCourse> filledCourses;
    private static final DecimalFormat df = new DecimalFormat("0.00");


    // List of courses divided up by type
    private final List<StudentCourse> coreList = new ArrayList<>();
    private final List<StudentCourse> electList = new ArrayList<>();
    private final List<StudentCourse> preList = new ArrayList<>();

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
    String filePath;


    /**
     * Performs the audit when created
     *
     * @param currentStudent current student object
     */
    public Audit(Student currentStudent, String filePath){
        this.currentStudent = currentStudent;
        this.filePath = filePath;
        this.filledCourses = currentStudent.getCleanCourseList();
        fillStudentPlanCourses();
        fillReplacementValues();
    }

    /**
     * This function calculates the core, elective, and cumulative GPA values
     */
    private void fillStudentPlanCourses(){
        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.CORE && !studentCourse.getSemester().isEmpty())
                .forEach(coreList::add);
        filledCourses.stream()
                .filter(studentCourse -> (studentCourse.getType() == Course.CourseType.ELECTIVE || studentCourse.getType() == Course.CourseType.ADDITIONAL) && !studentCourse.getSemester().isEmpty())
                .forEach(electList::add);
        filledCourses.stream()
                .filter(studentCourse -> studentCourse.getType() == Course.CourseType.PRE)
                .forEach(preList::add);

        coreGPA = calcGPA(coreList);
        electiveGPA = calcGPA(electList);
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
    public String getOutstanding(){
        String res = "";

        int coreHours = getTotalHours(coreList);
        int electHours = getTotalHours(currentStudent.getCourseType(Course.CourseType.ELECTIVE));

        if(coreHours < REQUIRED_CORE_HOURS) {
            double target = getNeededGPA(coreGPA, coreHours, MIN_CORE_GPA, REQUIRED_CORE_HOURS);
            res += printRequirements(target, Course.CourseType.CORE);
        }
        if(electHours < REQUIRED_ELECTIVE_HOURS){
            double target = getNeededGPA(electiveGPA, electHours, MIN_ELECT_GPA, REQUIRED_ELECTIVE_HOURS);
            res += printRequirements(target, Course.CourseType.ELECTIVE);
        }
        if(getTotalHours(currentStudent.getCourseType(Course.CourseType.ADDITIONAL)) < 3)
            res += printRequirements();

        return res;
    }

    /**
     * String for additional requirements
     */
    public String printRequirements() {
        int remaining = 3 - getTotalHours(currentStudent.getCourseType(Course.CourseType.ADDITIONAL));
        if(remaining <= 0)
            return printRequirements(0, Course.CourseType.ADDITIONAL);
        else
            if(remaining == 1)
                return "The student needs " + remaining + " hour of additional elective credit.";
            else
                return "The student needs " + remaining + " hours of additional elective credit.";

    }


    public String printRequirements(double target, Course.CourseType type){
        String res = "";
        if(target <= 0 || type == Course.CourseType.ADDITIONAL) {
            List<StudentCourse> currentSemester = getCurrentSemesterCourses(currentStudent.getCourseType(type));
            if(!currentSemester.isEmpty())
                res += "The student must pass " + printCourses(currentSemester) + "\n";
        } else
            res += "The student needs a GPA >= " + df.format(target) + " in the remaining courses\n";
        return res;
    }

    private double getNeededGPA(double currentGPA, double currentHours, double targetGPA, int totalHours){
        return ((targetGPA*totalHours)-(currentGPA*currentHours))/(totalHours - currentHours);
    }

    public int getTotalHours(List<StudentCourse> courseList){
        int total = 0;
        for(StudentCourse c : courseList){
            if(!c.getSemester().isEmpty() && isPassingGrade(c.getLetterGrade()))
                total += Integer.parseInt(c.getHours());
        }
        return total;
    }


    private List<StudentCourse> getCurrentSemesterCourses(List<StudentCourse> courseList){
        List<StudentCourse> current = new ArrayList<>();
        for(StudentCourse s : courseList) {
            if(!s.getSemester().isEmpty() && !isPassingGrade(s.getLetterGrade()))
                current.add(s);
        }

        return current;
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
        return res;
    }

    private String printPrereq(StudentCourse pre){
        String res = "";
        if(pre.isWaived())
            res = pre.getCourseNumber() + " - " + "Waived";
        else if(!pre.getSemester().isEmpty() && isPassingGrade(pre.getLetterGrade()))
            res = pre.getCourseNumber() + " - " + pre.getSemester();
        else if (!pre.getSemester().isEmpty())
            res = pre.getCourseNumber() + " - " + "In progress";
        else
            res = pre.getCourseNumber() + " - " + "Not satisfied";
        return res;
    }

    private boolean isPassingGrade(String grade){
        Pattern stringPattern = Pattern.compile("(^[A-C].?)|P|CR");
        Matcher m = stringPattern.matcher(grade);
        return m.find();
    }

    public void fillReplacementValues(){
        WordReplace.put("$$name$$", currentStudent.getStudentName());
        WordReplace.put("$$id$$", currentStudent.getStudentId());
        WordReplace.put("$$plan$$", "Master") ;
        WordReplace.put("$$major$$", currentStudent.getCurrentMajor());
        WordReplace.put("$$track$$", currentStudent.getCurrentPlan().getConcentration().toString().replace('-', ' '));

        WordReplace.put("coregpa", String.valueOf(calcGPA(coreList)));
        WordReplace.put("electivegpa", String.valueOf(calcGPA(electList)));
        WordReplace.put("combinedgpa", String.valueOf(combinedGPA));

        WordReplace.put("corelist", printCourses(coreList));
        WordReplace.put("electivelist", printCourses(electList));

        WordReplace.put("prelist", printPre());
        WordReplace.put("outstandingreq", getOutstanding());

        findAndReplace();
    }

    private void findAndReplace() {
        String pathOriginal = "src/main/resources/utd/dallas/backend/SampleAudit/";
        String templateDoc = "EmptyReport.docx";
        try {
            // finds the path of the operating system temp folder to create a temporary file
            String tempPath = System.getenv("TEMP") + "\\temp.docx";
            Path dirOrigem = Paths.get(pathOriginal + templateDoc);
            Path dirDestino = Paths.get(tempPath);
            Files.copy(dirOrigem, dirDestino, StandardCopyOption.REPLACE_EXISTING);

            try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(tempPath))) {
                for (XWPFParagraph p : doc.getParagraphs()) {
                    List<XWPFRun> runs = p.getRuns();
                    if (runs != null) {
                        for (String key : WordReplace.keySet()) {
                            for (XWPFRun r : runs) {
                                String text = r.getText(0);
                                if (text != null && text.contains(key)) {
                                    text = text.replace(key, WordReplace.getOrDefault(key, " "));
                                    String[] textArray = text.split("\n");
                                    r.setText(textArray[0], 0);
                                    for(int i = 1; i < textArray.length; i++){
                                        r.addBreak();
                                        r.setText(textArray[i]);
                                    }
                                }
                            }
                        }

                    }
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                doc.write(fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}