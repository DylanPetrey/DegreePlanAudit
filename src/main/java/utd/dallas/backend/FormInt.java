package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public interface FormInt {

    /**
     * This function returns a list of Courses of a specified Course Type
     * 
     * @param type The type of courses requested
     * @return A list of courses of specified type
     * 
     *         public List<Course> getCourseOfType(Course.CourseType type){
     *         List<Course> courses = new ArrayList<>();
     *         for (Course course : courseList) {
     *         if (course.getType() == type) {
     *         courses.add(course);
     *         }
     *         }
     *         return courses;
     *         }
     */
    /**
     * 
     * @param student
     * @throws IOException
     */
    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static void print(Student student) {

        String loc = "";
        PDDocument pdfDocument = null;
        Plan plan = student.getCurrentPlan();

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
            acroForm.getField("Name of Student").setValue(student.getStudentName());
            acroForm.getField("Student ID Number").setValue(student.getStudentId());
            student.setGraduation("Fall 23");
            acroForm.getField("Anticipated Graduation").setValue(student.getGraduation());
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            acroForm.getField("Date Submitted").setValue(formatter.format(new Date()));

            acroForm.getField("Check Box.0." + boolToInt(!student.isFastTrack()))
                    .setValue("Yes");
            acroForm.getField("Check Box.1." + boolToInt(!student.isThesis()))
                    .setValue("Yes");

            
        } catch (IOException e) {

        } catch (NullPointerException npe) {
        }

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
