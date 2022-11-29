package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;


public interface FormInt {

    /**
     * This function returns a list of Courses of a specified Course Type
     * @param type The type of courses requested
     * @return A list of courses of specified type
    
    public List<Course> getCourseOfType(Course.CourseType type){
        List<Course> courses = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getType() == type) {
                courses.add(course);
            }
        }
        return courses;
    }
    */
    /**
     
     * @param fileName
     * @throws IOException
     */

    public static void print(Student currentStudent) throws IOException{

        String loc = "";
        PDDocument pdfDocument = null;
        try {
            String conString = currentStudent.getCurrentPlan().getConcentration().toString();
            StringBuilder sb = new StringBuilder("Forms/DP-");
            sb.append(conString);
            if(conString.equals("Software-Engineering"))
                sb.append("-Program");
            sb.append(".pdf");
            loc = URLDecoder.decode(Form.class.getResource(sb.toString()).getFile(), "UTF-8");
            pdfDocument = PDDocument.load(new File(loc));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        Iterator<PDField> fieldTreeIterator = acroForm.getFieldIterator();
        
        while (fieldTreeIterator.hasNext()) {
            PDField f = fieldTreeIterator.next(); 
            COSDictionary obj = f.getCOSObject();
            if (f.getClass().equals(PDTextField.class)){
                System.out.println(f.getFullyQualifiedName());
                f.setValue("run");

            }
        }
        PDAcroForm finalForm = new PDAcroForm(pdfDocument);
        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        pdfDocument.save(new File(".", "test.pdf"));
    }
}

