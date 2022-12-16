package utd.dallas.backend;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static final DecimalFormat df = new DecimalFormat("0.000");


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

    private HashMap<String, String> WordReplace = new HashMap<>();
    private String filePath;


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
        return GPA;
    }


    /**
     * Prints the GPA as seen on the sample audit
     */
    public String getOutstanding(){
        String res = "";

        int coreHours = getCompletedHours(coreList);
        int electHours = getCompletedHours(currentStudent.getCourseType(Course.CourseType.ELECTIVE));
        int addHours = getCompletedHours(currentStudent.getCourseType(Course.CourseType.ADDITIONAL));

        if(coreHours < REQUIRED_CORE_HOURS)
            res += printCoreRequirements(coreHours);

        if(electHours < REQUIRED_ELECTIVE_HOURS)
            res += printElectRequirements(electHours);

        if(addHours < MIN_ADD_ELECTIVE_HOURS)
            res += printAdditionalRequirements();

        if(res.isEmpty())
            res = "None";

        return res;
    }

    public String printCoreRequirements(int coreHours) {
        StringBuilder res = new StringBuilder();
        double targetGPA = getNeededGPA(coreGPA, coreHours, MIN_CORE_GPA, REQUIRED_CORE_HOURS);

        if(targetGPA <= 0)
            res.append(printPass(coreList));
        else
            res.append(printOutstandingGPA(targetGPA, coreList, currentStudent.getCurrentPlan().getCore()));

        int numOptional = getNumOptional(coreList);
        if(numOptional > 0)
            res.append(printOptional(numOptional));

        if(!res.toString().isEmpty())
            res.append("\n");

        return res.toString();
    }

    public String printElectRequirements(int electHours) {
        StringBuilder res = new StringBuilder();
        double targetGPA = getNeededGPA(electiveGPA, electHours, MIN_ELECT_GPA, REQUIRED_ELECTIVE_HOURS - thesisHours());

        List<StudentCourse> electives = currentStudent.getCourseType(Course.CourseType.ELECTIVE);

        if(targetGPA <= 0)
            res.append(printPass(electives));
        else
            res.append(printOutstandingGPA(targetGPA, electives, currentStudent.getCurrentPlan().getElectives()));

        int remainingHours = getTotalHours(electList) - electHours;

        if(remainingHours > 0){
            res.append("and ").append(remainingHours).append(" more approved elective credit");
            if(remainingHours != 1)
                res.append("s");
        }

        if(!res.toString().isEmpty())
            res.append("\n");

        return res.toString();
    }

    public String printAdditionalRequirements() {
        StringBuilder res = new StringBuilder();

        int remaining = MIN_ADD_ELECTIVE_HOURS - getTotalHours(currentStudent.getCourseType(Course.CourseType.ADDITIONAL));

        res.append(printOutstandingAdditional(remaining));

        return res.toString();
    }

    public String printPass(List<StudentCourse> courseList){
        List<StudentCourse> currentSemester = getCurrentSemesterCourses(courseList);
        if(currentSemester.size() == 0)
            return "";

        return "The student must pass " + printCourses(currentSemester, false);
    }

    public String printOutstandingGPA(double target, List<StudentCourse> courseList, List<Course> plan){
        List<Course> outstandingCore = getOutstandingFromPlan(plan, courseList);
        outstandingCore.addAll(getCurrentSemesterCourses(courseList));

        String courseString = "";
        if(outstandingCore.size() != 0)
            courseString = printCourses(outstandingCore, false) + " ";

        return  "The student needs a GPA >= " + df.format(target) + " in " + courseString;
    }

    private String printOutstandingAdditional(int remainingHours){
        List<Course> outstanding = getOutstandingFromPlan(currentStudent.getCurrentPlan().getAdditional(), filledCourses);
        outstanding.addAll(getCurrentSemesterCourses(currentStudent.getCourseType(Course.CourseType.ADDITIONAL)));

        if(outstanding.size() == 0)
            return printRemainingHours(remainingHours, false);
        else
            return "The student must complete " + printCourses(outstanding, false) + printRemainingHours(remainingHours, true);

    }

    private String printRemainingHours(int remainingHours, boolean hasCourses){
        String res = "";

        if(remainingHours == 0)
            return res;

        if(hasCourses)
            res = " and ";
        else
            res = "The student needs ";

        if(remainingHours == 1)
            res += remainingHours + " hour of additional elective credit.";
        else if (remainingHours > 1)
            res += remainingHours + " hours of additional elective credits.";

        return res;
    }

    private List<StudentCourse> getCurrentSemesterCourses(List<StudentCourse> courseList){
        List<StudentCourse> current = new ArrayList<>();
        // Get
        for(StudentCourse s : courseList) {
            if(!s.getSemester().isEmpty() && s.getLetterGrade().isEmpty())
                current.add(s);
        }

        return current;
    }


    private List<Course> getOutstandingFromPlan(List<Course> plan, List<StudentCourse> student){
        List<Course> out = new ArrayList<>();

        for(Course course : plan) {
            if(!student.contains(course))
                out.add(course);
        }

        return out;
    }

    public String printOptional(int numOptional){
        List<Course> outstanding = getOutstandingFromPlan(currentStudent.getCurrentPlan().getOptionalCore(), filledCourses);
        if(outstanding.size() == 0)
            return "";

        return " and " + numOptional + " of " + printCourses(outstanding, false);
    }

    private int getNumOptional(List<StudentCourse> courseList){
        int num = (int) currentStudent.getCurrentPlan().getNumOptional();

        for(StudentCourse course : courseList) {
            if(currentStudent.getCurrentPlan().isOpt(course))
                num--;
        }

        return num;
    }


    private double getNeededGPA(double currentGPA, double currentHours, double targetGPA, int totalHours){
        return ((targetGPA*totalHours)-(currentGPA*currentHours))/(totalHours - currentHours);
    }


    /**
     * Method to get the number of hours in for thesis courses in the current semester
     *
     * @return
     */
    private int thesisHours(){
        int hours = 0;
        for(StudentCourse course : electList){
            if(course.getCourseNumber().contains("6V98") && !course.getSemester().isEmpty() && course.getLetterGrade().isEmpty() && course.getType() == Course.CourseType.ELECTIVE){
                hours += Integer.parseInt(course.getHours());
            }
        }
        return hours;
    }

    public int getCompletedHours(List<StudentCourse> courseList){
        int total = 0;
        for(StudentCourse c : courseList){
            if(!c.getSemester().isEmpty() && isPassingGrade(c.getLetterGrade()))
                total += Integer.parseInt(c.getHours());
        }
        return total;
    }

    public int getTotalHours(List<StudentCourse> courseList){
        int total = 0;
        for(StudentCourse c : courseList){
            if(!c.getSemester().isEmpty() && !c.getHours().isEmpty())
                total += Integer.parseInt(c.getHours());
        }
        return total;
    }

    private String printCourses(List<? extends Course> courseList, boolean newline){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < courseList.size(); i++){
            res.append(courseList.get(i));
            if(i < courseList.size()-1)
                res.append(", ");
            else if(newline)
                res.append("\n");
        }
        return res.toString();
    }


    private String printPre(){
        StringBuilder res = new StringBuilder();
        if(preList.size() == 0)
            res.append("None");
        for(StudentCourse course : preList)
            res.append(printPrereq(course)).append("\n");
        return res.toString();
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

        WordReplace.put("coregpa", df.format(calcGPA(coreList)));
        WordReplace.put("electivegpa", df.format(calcGPA(electList)));
        WordReplace.put("combinedgpa", df.format(combinedGPA));

        WordReplace.put("corelist", printCourses(coreList,true));
        WordReplace.put("electivelist", printCourses(electList, true));

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
                                r.setFontFamily("Calibri");
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
                try{
                    FileOutputStream fos = new FileOutputStream(filePath);
                    doc.write(fos);
                    fos.close();
                } catch (FileNotFoundException e){
                    saveIncremented(doc, filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Increments the filename until valid
     *
     * @param doc
     * @param filePath
     */
    private void saveIncremented(XWPFDocument doc, String filePath) throws IOException {
        File file = new File(filePath);
        String newFilePath = filePath;

        for (int i = 1; file.exists(); i++) {
            if(i != 1){
                String extension = FilenameUtils.getExtension(newFilePath);
                String increment = String.format("(%d)." + extension, i);
                newFilePath = FilenameUtils.removeExtension(filePath) + increment;
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(newFilePath);
                doc.write(fos);
                this.filePath = newFilePath;
                break;
            } catch (FileNotFoundException e) {
                if(fos != null)
                    fos.close();
                continue;
            } catch (Exception ignore) {}
            finally {
                if(fos != null)
                    fos.close();
            }
            break;
        }
    }
    public String getFilePath(){ return filePath; }
}