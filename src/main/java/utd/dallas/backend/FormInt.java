package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import utd.dallas.backend.Course.CourseType;

public interface FormInt {


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
        fillField(acroForm, "Anticipated Graduation", student.getGraduation());
        fillField(acroForm, "Date Submitted", formatter.format(new Date()));
        fillField(acroForm, "Check Box.0." + boolToInt(!student.isFastTrack()), "Yes");
        fillField(acroForm, "Check Box.1." + boolToInt(!student.isThesis()), "Yes");

        List<StudentCourse> studentCore = cloneList(student.getCourseType(CourseType.CORE));
        int i = 0;
        for (Course c : plan.getCore()) {
            int index = studentCore.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = studentCore.get(index);
                fillField(acroForm, "Core." + i + ".2", studentCourse.getSemester());
                fillField(acroForm, "Core." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, "Core." + i + ".4", studentCourse.getLetterGrade());
                studentCore.remove(index);
                i++;
            }
        }

        List<StudentCourse> studentOptional = cloneList(student.getCourseType(CourseType.OPTIONAL));
        for (Course c : plan.getOptionalCore()) {
            int index = studentOptional.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = studentOptional.get(index);
                fillField(acroForm, "Core." + i + ".2", studentCourse.getSemester());
                fillField(acroForm, "Core." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, "Core." + i + ".4", studentCourse.getLetterGrade());
                studentOptional.remove(index);
                i++;
            }
        }
        for (StudentCourse studentCourse : studentCore) {
            fillField(acroForm, "Core." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Core." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Core." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Core." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Core." + i + ".4", studentCourse.getLetterGrade());
        }

        i = 0;
        List<StudentCourse> studentElec = cloneList(student.getCourseType(CourseType.ELECTIVE));
        for (StudentCourse studentCourse : studentElec) {
            fillField(acroForm, "Elective." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, "Elective." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, "Elective." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, "Elective." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, "Elective." + i + ".4", studentCourse.getLetterGrade());
        }
            List<StudentCourse> studentPre = cloneList(student.getCourseType(CourseType.PRE));
            List<StudentCourse> studentAddl = cloneList(student.getCourseType(CourseType.ADDITIONAL));
            List<StudentCourse> studentTrack = cloneList(student.getCourseType(CourseType.TRACK));
            List<StudentCourse> studentOther = cloneList(student.getCourseType(CourseType.OTHER));

        for (Course course : student.getTranscriptList()) {

        }

        while (fieldTreeIterator.hasNext()) {
            PDField f = fieldTreeIterator.next();
            COSDictionary obj = f.getCOSObject();
            if (f.getClass().equals(PDTextField.class)) {
                System.out.println(f.getFullyQualifiedName());
                /*
                 * /
                 * try {
                 * f.setValue("run");
                 * } catch (IOException e){}
                 */

            }
        }
        PDAcroForm finalForm = new PDAcroForm(pdfDocument);
        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        try {
            pdfDocument.save(new File(".", "test.pdf"));
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
            npe.printStackTrace();
        }

    }
}
