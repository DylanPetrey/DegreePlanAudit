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
     * The print function fills the degree plan for the selected concentration and saves it to a new PDF file.
     * 
     *
     * @return Nothing
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


    /**
     * The fillField function fills the field with the given value.
     * 
     *
     * @param acroForm AcroForm object
     * @param String Fully qualified name of the PDField trying to be written to
     * @param String Value to be filled in.
     *
     * @return Nothing
     */
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

    /**
     * The fillDefault function fills in the default values for a given course type.
     * 
     *
     * @param type Determine which course type to fill
     *
     * @return The list of courses that are not in the plan
     */
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

    

    /**
     * The fillRest function fills the rest of the form with student's courses.
     * 
     *
     * @param type Determine which course type to fill
     * @param int Keep track of the number of courses in each course type
     *
     * @return The number of courses that are added to the form
     */
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
     * The boolToInt function converts a boolean value to an integer.
     * 
     *
     * @param b Determine whether or not the return value should be 1 or 0
     *
     * @return 1 if the boolean parameter is true, and 0 otherwise
     */
    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    
    /**
     * The cloneList function takes a list of StudentCourse objects and returns a new list containing
     * copies of the original objects. This is useful when you want to make changes to the copy, but not
     * change the original. For example, if you have an array of StudentCourse objects that are used in multiple places, 
     * it's best practice to clone them before making any changes so that your code doesn't break elsewhere. 
     * 
     *
     * @param list Store the list of studentcourse objects that will be returned by the function
     *
     * @return A new list containing a copy of the elements in the original list
     *
     */
    private static List<StudentCourse> cloneList(List<StudentCourse> list) {
        List<StudentCourse> clone = new ArrayList<StudentCourse>(list.size());
        for (StudentCourse c : list)
            clone.add(new StudentCourse(c));
        return clone;
    }

}
