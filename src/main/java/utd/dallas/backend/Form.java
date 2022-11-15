<<<<<<< HEAD:src/Form.java
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDFieldTree;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
=======
package utd.dallas.backend;

import java.util.*;
>>>>>>> origin/mavenFrontend:src/main/java/utd/dallas/backend/Form.java


public class Form {
    private String studentName;
    private String studentID;
    private String semesterAdmitted;
    private String anticipatedGraduation;
    private Plan.Concentration concentration;
    private boolean isFastTrack;
    private boolean thesis;
    private List<StudentCourse> courseList;



    /**
     * Initializes Form object to be used to fill out PDF
     * @param currentStudent Student object that the form is generated for
     */
    public Form(Student currentStudent) {
        this.studentName = currentStudent.getStudentName();
        this.studentID = currentStudent.getStudentId();
        this.semesterAdmitted = "";
        this.anticipatedGraduation = "";
        //this.concentration = currentStudent.concentration;
        this.isFastTrack = false;
        this.thesis = false;
        this.courseList = currentStudent.getCourseList();
    }

    /**
     * This function returns a list of Courses of a specified Course Type
     * @param type The type of courses requested
     * @return A list of courses of specified type
     */
    public List<Course> getCourseOfType(Course.CourseType type){
        List<Course> courses = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getType() == type) {
                courses.add(course);
            }
        }
        return courses;
    }
    /**
     
     * @param fileName
     * @throws IOException
     */

    public void fillForm(Plan.Concentration concentration) throws IOException {

        String loc = "Forms/DP-" + concentration.toString() + ".pdf"; 
        PDDocument pdfDocument = PDDocument.load(new File(loc));
        PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        Iterator<PDField> fieldTreeIterator = acroForm.getFieldIterator();
        
        while (fieldTreeIterator.hasNext()) {
            PDField f = fieldTreeIterator.next(); 
            COSDictionary obj = f.getCOSObject();
            if (f.getClass().equals(PDTextField.class)){
                f.setValue("run");
            }
        }
        PDAcroForm finalForm =new PDAcroForm(pdfDocument);
        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        pdfDocument.save(new File(".", "test.pdf"));
        
        
    }



    /**
     * Accessor methods to be used outside the class.
     */
    public String getStudenName(){return studentName; }
    public String getStudentID(){ return studentID; }
    public String getSemesterAdmitted(){ return semesterAdmitted; }
    public String getAnticipatedGraduation(){ return anticipatedGraduation; }
    public boolean isFastTrack(){ return isFastTrack; }
    public boolean isThesis(){ return thesis; }
    public List<StudentCourse> getCourseList() {return courseList; }

    /**
     * Mutator methods to be used outside the class.
     */
    public void setStudenName(String studentName){ this.studentName = studentName; }
    public void setStudentID(String studentID){this.studentID = studentID; }
    public void setSemesterAdmitted(String semesterAdmitted){ this.semesterAdmitted = semesterAdmitted; }
    public void setAnticipatedGraduation(String anticipatedGraduation){ this.anticipatedGraduation = anticipatedGraduation; }
    public void setFastTrack(boolean isFastTrack){this.isFastTrack = isFastTrack; }
    public void setThesis(boolean thesis) { this.thesis = thesis; }

    public void addCourse(List<Course> courseList, Course course) { courseList.add(course); }
    public void removeCourse(List<Course> courseList, Course course) { courseList.remove(course); }

}

