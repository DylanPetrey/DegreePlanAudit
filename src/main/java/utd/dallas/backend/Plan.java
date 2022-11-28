package utd.dallas.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Plan {

    public enum Concentration {
        TRADITIONAL("Traditional Computer Science"),
        NETWORKS("Network Telecommunications"),
        INTEL("Intelligent Systems"),
        INTERACTIVE("Interactive Computing"),
        SYSTEMS("Systems"),
        DATA("Data Science"),
        CYBER("Cyber Security"),
        SOFTWARE("Software Engineering");

        private String concenString;

        Concentration(String concentration) {
            this.concenString = concentration;
        }

        public String toString() {
            return this.concenString;
        }

    };

    private long numOptional = 0;

    File jsonFile = new File("src/main/resources/utd/dallas/backend/JSONobjects/utd_catalog.json").getAbsoluteFile();
    DocumentContext CatalogFile;

    private Concentration concentration;
    private List<Course> requiredCore = new ArrayList<Course>();
    private List<Course> optionalCore = new ArrayList<Course>();
    private List<Course> admissionPrerequisites = new ArrayList<Course>();
    private List<Course> trackPrerequisites = new ArrayList<Course>();
    private List<String> excludedElectives = new ArrayList<String>();

    Plan() {
        try {
            CatalogFile = JsonPath.parse(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the plan object and sets the objects to the initial value
     *
     * @param concentration List of courses
     */
    Plan(Concentration concentration) {
        setConcentration(concentration);

        try {
            CatalogFile = JsonPath.parse(jsonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a list of course objects from the list of course numbers
     *
     * @param obj List of course numbers from the JSON
     */
    private List<String> toList(List<String> obj) {
        List<String> list = new ArrayList<>();
        if(obj.size() == 0)
            return list;

        for (String currentNum : obj) {
            if (!Objects.equals(currentNum, ""))
                list.add(currentNum);
        }
        return list;
    }


    /**
     * Creates a list of courses from a JSON object
     *
     * @param array JSON object of a courses from the degreeRequirements json
     * @param type The course type to initialize the courses with
     * @return a list of courses
     */
    private List<Course> toList(JSONArray array, Course.CourseType type) {
        List<Course> list = new ArrayList<>();
        if(array == null)
            return list;

        for (Object o : array) {
            JSONObject currentCourse = (JSONObject) o;
            String num = (String) currentCourse.get("courseNumber");
            String title = (String) currentCourse.get("courseTitle");
            list.add(new Course(num, title, type));
        }
        return list;
    }

    /**
     * Parses the JSON and fills in the plan variables
     *
     * @param type The course type to get the degree requirements from the json
     */
    public void setConcentration(Concentration type) {
        this.concentration = type;
        String JSONfilename = "src/main/resources/utd/dallas/backend/JSONobjects/degreeRequirements.json";
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(JSONfilename));

            JSONObject jsonObject = (JSONObject) obj;
            JSONObject jsonCurrentPlan = (JSONObject) jsonObject.get(type.toString());
            this.numOptional = (long) jsonCurrentPlan.get("numOptional");

            JSONArray requiredCore = (JSONArray) jsonCurrentPlan.get("requiredCore");
            this.requiredCore = toList(requiredCore, Course.CourseType.CORE);

            JSONArray optionalCore = (JSONArray) jsonCurrentPlan.get("optionalCore");
            this.optionalCore = toList(optionalCore, Course.CourseType.OPTIONAL);

            JSONArray admissionPrerequisite = (JSONArray) jsonCurrentPlan.get("admissionPrerequisites");
            this.admissionPrerequisites = toList(admissionPrerequisite, Course.CourseType.PRE);

            JSONArray trackPrerequisite = (JSONArray) jsonCurrentPlan.get("trackPrerequisites");
            this.trackPrerequisites = toList(trackPrerequisite, Course.CourseType.TRACK);

            List<String> excludedElective = (List<String>) jsonCurrentPlan.get("excludedElectives");
            this.excludedElectives = toList(excludedElective);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a course is a core course
     *
     * @param courseNumber Course number to identify the course
     * @return true/false if the course is a core course
     */
    public boolean isCore(Course courseNumber) {
        for (Course currentClass : requiredCore) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Checks if a course is an optional core course
     *
     * @param courseNumber Course number to identify the course
     * @return true/false if the course is an optional core course
     */
    public boolean isOpt(String courseNumber) {
        for (Course currentClass : optionalCore) {
            if (courseNumber.equals(currentClass.getCourseNumber()))
                return true;
        }
        return false;
    }

    /**
     * Checks if a course is a track course
     *
     * @param courseNumber Course number to identify the course
     * @return true/false if the course is a track course
     */
    public boolean isTrack(Course courseNumber) {
        for (Course currentClass : trackPrerequisites) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Checks if a course is a prerequisite
     *
     * @param courseNumber Course number to identify the course
     * @return true/false if the course is a prerequisite
     */
    public boolean isPre(Course courseNumber) {
        for (Course currentClass : admissionPrerequisites) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Parse number of credit hours from the JSON
     *
     * @param courseNum Course number to identify the course
     * @return number of credit hours
     */
    public int getCourseHours(String courseNum){
        String path = "$.['" + courseNum + "'].Hours";
        try {
            return Integer.parseInt(CatalogFile.read(path));
        }catch (Exception e){
            return 3;
        }
    }

    /**
     * Parse course description from the JSON
     *
     * @param courseNum Course number to identify the course
     * @return Course description
     */
    public String getCourseTitle(String courseNum){
        String path = "$.['" + courseNum + "'].Title";
        try {
            return CatalogFile.read(path);
        }catch (Exception e){
            return "";
        }
    }

    /**
     * Accessor methods to be used outside the class.
     */

    public List<Course> getCourseOfType(Course.CourseType type) {
        switch (type){
            case CORE:
                return requiredCore;
            case OPTIONAL:
                return optionalCore;
            case PRE:
                List<Course> prereq = new ArrayList<>();
                prereq.addAll(admissionPrerequisites);
                prereq.addAll(trackPrerequisites);
                return prereq;
        }

        return new ArrayList<>();
    }

    public Course getOptionalCourse(String courseNum){
        for(Course optCourse : optionalCore){
            if(optCourse.getCourseNumber().equals(courseNum))
                return optCourse;
        }
        return null;
    }

    public long getNumOptional() { return numOptional; }
    public List<Course> getCore() { return requiredCore; }
    public List<Course> getOptionalCore() { return optionalCore; }
    public List<Course> getAdmissionPrerequisites() { return admissionPrerequisites; }
    public List<Course> getTrackPrerequisites() { return trackPrerequisites; }
    public List<String> getExcludedElectives() { return excludedElectives; }
    public Concentration getConcentration() { return concentration; }
}
