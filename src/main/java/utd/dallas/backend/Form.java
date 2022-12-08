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
    public void print(String filePath) {

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

        List<StudentCourse> courseList = cloneList(student.getCourseType(CourseType.CORE));
        fillDefault(CourseType.CORE, courseList);
        fillDefault(CourseType.OPTIONAL, courseList);

        courseList = cloneList(student.getCourseType(CourseType.ELECTIVE));
        fillRest(CourseType.ELECTIVE, courseList,  0);

        courseList = cloneList(student.getCourseType(CourseType.ADDITIONAL));
        fillRest(CourseType.ADDITIONAL, courseList, 0);


        // fillRest(CourseType.OTHER);

        courseList = cloneList(student.getCourseType(CourseType.PRE));
        fillDefault(CourseType.PRE, courseList);

        pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
        try {
            pdfDocument.save(new File(filePath));
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
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
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
    private void fillDefault(CourseType type, List<StudentCourse> courseList) {
        String typeString = type.toString();

        List<Course> planCourses = plan.getCourseOfType(type);
        int i = 0;
        for (Course c : planCourses) {
            int index = courseList.indexOf(c);
            if (index >= 0) {
                StudentCourse studentCourse = courseList.get(index);
                fillField(acroForm, typeString + "." + i + ".2", studentCourse.getSemester());
                if(studentCourse.getType() == CourseType.PRE)
                    fillField(acroForm, typeString + "." + i + ".3" + boolToInt(studentCourse.isWaived()), "Waived");
                else
                    fillField(acroForm, typeString + "." + i + ".3", studentCourse.getTransfer());
                fillField(acroForm, typeString + "." + i + ".4", studentCourse.getLetterGrade());
                courseList.remove(index);
                i++;
            }
        }
        if(type == CourseType.PRE)
            fillRest(type, courseList, i);
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
    private int fillRest(CourseType type, List<StudentCourse> courseList, int i) {
        String typeString = type.toString();

        if(i == 0)
            courseList = cloneList(student.getCourseType(type));

        for (StudentCourse studentCourse : courseList) {
            fillField(acroForm, typeString + "." + i + ".0", studentCourse.getCourseTitle());
            fillField(acroForm, typeString + "." + i + ".1", studentCourse.getCourseNumber());
            fillField(acroForm, typeString + "." + i + ".2", studentCourse.getSemester());
            fillField(acroForm, typeString + "." + i + ".3", studentCourse.getTransfer());
            fillField(acroForm, typeString + "." + i + ".4", studentCourse.getLetterGrade());
            i++;
        }
        return i;
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
