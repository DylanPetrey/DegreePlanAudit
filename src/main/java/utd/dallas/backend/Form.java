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
    private Plan plan;
    private List<StudentCourse> courseList;

    /**
     * Constructs a Form object for the given student
     * 
     * @param student
     */
    public Form(Student student) {
        this.student = student;
        this.plan = student.getCurrentPlan();
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
        PDDocument pdfDocument = null;
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

        fillDefault(CourseType.CORE);
        fillDefault(CourseType.OPTIONAL);
        fillRest(CourseType.ELECTIVE, 0);
        fillRest(CourseType.ADDITIONAL, 0);
        fillDefault(CourseType.OTHER);
        fillDefault(CourseType.PRE);

        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        try {
            String name = student.getStudentName().replaceAll("\\s", "");
            pdfDocument.save(new File(".", "DegreePlan_" + name + ".pdf"));
            pdfDocument.close();
        } catch (IOException e) {}
    }

    private void fillField(PDAcroForm acroForm, String fullyQualifiedName, String value) {
        try {
            acroForm.getField(fullyQualifiedName).setValue(value);
        } catch (IOException ioe) {
            System.out.println("TEST");
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    private void fillDefault(CourseType type) {
        courseList = cloneList(student.getCourseType(type));
        int i = 0;
        for (Course c : plan.getCourseOfType(type)) {
            int index = courseList.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = courseList.get(index);
                fillField(acroForm, type.toString() + "." + i + ".2", studentCourse.getSemester());
                fillField(acroForm, type.toString() + "." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, type.toString() + "." + i + ".4", studentCourse.getLetterGrade());
                courseList.remove(index);
                i++;
            }
        }
        fillRest(type, i);;
    }

    private void fillRest(CourseType type, int i) {
        if(i == 0)
            courseList = cloneList(student.getCourseType(type));
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, type.toString() + "." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, type.toString() + "." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, type.toString() + "." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, type.toString() + "." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, type.toString() + "." + i + ".4", studentCourse.getLetterGrade());
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
