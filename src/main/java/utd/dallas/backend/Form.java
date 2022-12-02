package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import utd.dallas.backend.Course.CourseType;

public interface Form {


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

/**
 * The print function prints a student's form to the console.
 *
 * @param student active student
 *
 * @return Void
 *
 */
    public static void print(Student student) {

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
        PDAcroForm acroForm = docCatalog.getAcroForm();
        Iterator<PDField> fieldTreeIterator = acroForm.getFieldIterator();

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


        List<StudentCourse> studentOther = cloneList(student.getCourseType(CourseType.OTHER));
        i = 0;
        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, "Additional." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Additional." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Additional." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Additional." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Additional." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }

       
        //PDAcroForm finalForm = new PDAcroForm(pdfDocument);
        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        try {
            String name = student.getStudentName().replaceAll("\\s", "");
            pdfDocument.save(new File(".", "DegreePlan_" + name + ".pdf"));
            pdfDocument.close();
        } catch (IOException e) {
        }
    }

    private static List<StudentCourse> cloneList(List<StudentCourse> list) {
        List<StudentCourse> clone = new ArrayList<StudentCourse>(list.size());
        for (StudentCourse c : list)
            clone.add(new StudentCourse(c));
        return clone;
    }

    private static void fillField(PDAcroForm acroForm, String fullyQualifiedName, String value) {
        try {
            acroForm.getField(fullyQualifiedName).setValue(value);
        } catch (IOException ioe) {
            System.out.println("TEST");
            ioe.printStackTrace();
        } catch (NullPointerException npe){
        }

    }

    private static void fillSection(String section, Student student, PDAcroForm acroForm, CourseType type) {
        
        switch (type){

            case CORE:
                break;
        }
    }
}
