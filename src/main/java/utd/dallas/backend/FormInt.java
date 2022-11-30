package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
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
     * 
     * @param b boolean value to be converted to an int.
     * @return integer value 1 if true 0 if false
     */
    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

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

        try {
            //TODO: Fill Courses
            //TODO: Update PDF Form Fields
            acroForm.getField("Name of Student").setValue(student.getStudentName());
            acroForm.getField("Student ID Number").setValue(student.getStudentId());
            acroForm.getField("Anticipated Graduation").setValue(student.getGraduation());
            acroForm.getField("Date Submitted").setValue(formatter.format(new Date()));
            acroForm.getField("Check Box.0." + boolToInt(!student.isFastTrack()))
                    .setValue("Yes");
            acroForm.getField("Check Box.1." + boolToInt(!student.isThesis()))
                    .setValue("Yes");
            List<StudentCourse> studentCore = student.getCourseOfType(CourseType.CORE);
            List<StudentCourse> studentElec = student.getCourseOfType(CourseType.ELECTIVE);
            List<StudentCourse> studentPre = student.getCourseOfType(CourseType.PRE);
            List<StudentCourse> studentAddl = student.getCourseOfType(CourseType.ADDITIONAL);
            for (Course core : plan.getCore()) {
            }
        } catch (IOException ioe) {
        } catch (NullPointerException npe) {}

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
        } catch (IOException e) {
        }
    }
}
