package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

import utd.dallas.backend.Course.CourseType;

public class Form {

    private PDAcroForm acroForm;
    private Student student;

    /**
     * Constructs a Form object for the given student
     * 
     * @param student
     */
    public Form(Student student) {
        this.student = student;
    }

    /**
     * The print function prints a student's form to the console.
     *
     * @param student active student
     *
     * @return Void
     *
     */
    public void print() {

        String loc = "";
        List<StudentCourse> courseList;
        PDDocument pdfDocument = null;
        Plan plan = student.getCurrentPlan();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        try {
            String fileName = new String("Forms/DP-" + plan.getConcentration().toString() + ".pdf");
            loc = URLDecoder.decode(Form.class.getResource(fileName).getFile(), "UTF-8");
            pdfDocument = PDDocument.load(new File(loc));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        this.acroForm = docCatalog.getAcroForm();

        fillField(acroForm, "Name of Student", student.getStudentName());
        fillField(acroForm, "Student ID Number", student.getStudentId());
        fillField(acroForm, "Semester Admitted to Program", student.getStartDate());
        fillField(acroForm, "Anticipated Graduation", student.getGraduation());
        fillField(acroForm, "Date Submitted", formatter.format(new Date()));
        fillField(acroForm, "Check Box.0." + boolToInt(!student.isFastTrack()), "Yes");
        fillField(acroForm, "Check Box.1." + boolToInt(!student.isThesis()), "Yes");

        courseList = cloneList(student.getCourseType(CourseType.CORE));
        int i = 0;
        for (Course c : plan.getCore()) {
            int index = courseList.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = courseList.get(index);
                fillField(acroForm, "Core." + i + ".2", studentCourse.getSemester());
                fillField(acroForm, "Core." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, "Core." + i + ".4", studentCourse.getLetterGrade());
                courseList.remove(index);
                i++;
            }
        }
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Core." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Core." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Core." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Core." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Core." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

        courseList = cloneList(student.getCourseType(CourseType.OPTIONAL));
        i = 0;
        for (Course c : plan.getOptionalCore()) {
            int index = courseList.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = courseList.get(index);
                fillField(acroForm, "Optional." + i + ".2", studentCourse.getSemester());
                fillField(acroForm, "Optional." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, "Optional." + i + ".4", studentCourse.getLetterGrade());
                courseList.remove(index);
                i++;
            }
        }
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Optional." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Optional." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Optional." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Optional." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Optional." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

        courseList = cloneList(student.getCourseType(CourseType.ELECTIVE));
        i = 0;
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Elective." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Elective." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Elective." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Elective." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Elective." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

        courseList = cloneList(student.getCourseType(CourseType.ADDITIONAL));
        i = 0;
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Additional." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Additional." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Additional." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Additional." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Additional." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

        courseList = cloneList(student.getCourseType(CourseType.PRE));
        i = 0;
        for (Course c : plan.getAdmissionPrerequisites()) {
            int index = courseList.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = courseList.get(index);
                fillField(acroForm, "Prereq." + i + ".2", studentCourse.getSemester());
                fillField(acroForm, "Prereq." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, "Prereq." + i + ".4", studentCourse.getLetterGrade());
                courseList.remove(index);
                i++;
            }
        }

        courseList = cloneList(student.getCourseType(CourseType.ADDITIONAL));
        i = 0;
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Additional." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Additional." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Additional." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Additional." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Additional." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

        courseList = cloneList(student.getCourseType(CourseType.OTHER));
        i = 0;
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Other." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Other." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Other." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Other." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Other." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

        // PDAcroForm finalForm = new PDAcroForm(pdfDocument);
        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        try {
            String name = student.getStudentName().replaceAll("\\s", "");
            pdfDocument.save(new File(".", "DegreePlan_" + name + ".pdf"));
            pdfDocument.close();
        } catch (IOException e) {
        }
    }

    private static void fillField(PDAcroForm acroForm, String fullyQualifiedName, String value) {
        try {
            acroForm.getField(fullyQualifiedName).setValue(value);
        } catch (IOException ioe) {
            System.out.println("TEST");
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    private static void fillPartialLine() {
    }

    private static void fillFullLine(Student student, PDAcroForm acroForm) {
        List<StudentCourse> courseList = cloneList(student.getCourseType(CourseType.OTHER));
        int i = 0;
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Other." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Other." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Other." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Other." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Other." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }
    }

    /**
     * Converts a boolean value to an integer.
     * 
     * @param b Determine whether the return value should be 1 or 0
     *
     * @return 1 if the boolean value is true and 0 if it is false
     */
    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    private static List<StudentCourse> cloneList(List<StudentCourse> list) {
        List<StudentCourse> clone = new ArrayList<StudentCourse>(list.size());
        for (StudentCourse c : list)
            clone.add(new StudentCourse(c));
        return clone;
    }

}
