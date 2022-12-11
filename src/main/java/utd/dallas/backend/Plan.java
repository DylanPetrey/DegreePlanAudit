package utd.dallas.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Plan {

    public enum Concentration {
        TRADITIONAL("Traditional"),
        NETWORKS("Network-Telecommunications"),
        INTEL("Intelligent-Systems"),
        INTERACTIVE("Interactive-Computing"),
        SYSTEMS("Systems"),
        DATA("Data-Science"),
        CYBER("Cyber-Security"),
        SOFTWARE("Software-Engineering");


        private final String concenString;
        Concentration(String concentration) {
            this.concenString = concentration;
        }

        public String toString() {
            return this.concenString;
        }

    };

    private final String utdCatalogJSON = "JSONobjects/utd_catalog.json";
    private Set<String> utdCatalogCourseNums = new HashSet<>();
    private final String degreeRequirementsJSON = "JSONobjects/degreeRequirements.json";
    private DocumentContext CatalogFile;
    private Concentration concentration;

    private final String major = "";
    private long numOptional = 0;
    private List<Course> requiredCore = new ArrayList<Course>();
    private List<Course> optionalCore = new ArrayList<Course>();
    private List<Course> admissionPrerequisites = new ArrayList<Course>();
    private List<Course> trackPrerequisites = new ArrayList<Course>();
    private List<String> excludedElectives = new ArrayList<String>();

    /**
     * Initializes empty plan object
     */
    Plan() {
        CatalogFile = JsonPath.parse(getInputStream(utdCatalogJSON));
        utdCatalogCourseNums = getAllCourseNums();
    }

    private InputStream getInputStream(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream =  Plan.class.getResourceAsStream(fileName);
        } catch (NullPointerException npe){
            npe.printStackTrace();
        }
        return inputStream;
    }

    /**
     * Initializes the plan object and sets the objects to the initial value
     *
     * @param concentration List of courses
     */
    Plan(Concentration concentration) {
        setConcentration(concentration);

        CatalogFile = JsonPath.parse(getInputStream(utdCatalogJSON));


        utdCatalogCourseNums = getAllCourseNums();
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
            String hours = getCourseHours(num);
            list.add(new Course(num, title, hours, type));
        }
        return list;
    }

    /**
     * Parses the degree requirements JSON and fills in initial variables from the degree plan
     *
     * @param plan the current plan to parse from the JSON
     */
    public void setConcentration(Concentration plan) {
        this.concentration = plan;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new InputStreamReader(getInputStream(degreeRequirementsJSON)));

            JSONObject jsonObject = (JSONObject) obj;
            JSONObject jsonCurrentPlan = (JSONObject) jsonObject.get(plan.toString());
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

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a new course object if it is an optional course in the degree plan
     *
     * @param courseNum Course number to identify the course
     * @return Optional course from the degreePlan
     */
    public Course getOptionalCourse(String courseNum){
        for(Course optCourse : optionalCore){
            if(optCourse.getCourseNumber().equals(courseNum))
                return optCourse;
        }
        return null;
    }

    /**
     * Checks if a course is a core course
     *
     * @param course Course to check in plan
     * @return true/false if the course is a core course
     */
    public boolean isCore(Course course) {
        for (Course currentClass : requiredCore) {
            if (course.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Checks if a course is an optional core course
     *
     * @param course Course to check in plan
     * @return true/false if the course is an optional core course
     */
    public boolean isOpt(Course course) {
        for (Course currentClass : optionalCore) {
            if (course.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Checks if a course is a track course
     *
     * @param course Course to check in plan
     * @return true/false if the course is a track course
     */
    public boolean isTrack(Course course) {
        for (Course currentClass : trackPrerequisites) {
            if (course.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Checks if a course is a prerequisite
     *
     * @param course Course to check in plan
     * @return true/false if the course is a prerequisite
     */
    public boolean isPre(Course course) {
        for (Course currentClass : admissionPrerequisites) {
            if (course.equals(currentClass))
                return true;
        }
        return false;
    }

    /**
     * Parse current major from the degree plan JSON
     *
     * @return String for the major
     */
    public String getMajor(){
        String path = "$.['" + concentration.toString() + "'].Major";
        try {
            return CatalogFile.read(path);
        }catch (Exception e){
            return "";
        }
    }

    private Set<String> getAllCourseNums(){
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new InputStreamReader(getInputStream(utdCatalogJSON)));

            JSONObject jsonObject = (JSONObject) obj;
            return (Set<String>) jsonObject.keySet();
        } catch (Exception ignore) { }
        return new HashSet<String>();
    }

    /**
     * Parse course title from the JSON
     *
     * @param courseNum Course number to identify the course
     * @return String for the course title
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
     * Parse number of credit hours from the JSON
     *
     * @param courseNum Course number to identify the course
     * @return int of the number of credit hours
     */
    public String getCourseHours(String courseNum){
        String path = "$.['" + courseNum + "'].Hours";
        String hourFromName = courseNum.substring(courseNum.length() - 3, courseNum.length() - 2);
        try {
            String h =  CatalogFile.read(path);
            if(h.matches("^\\d$"))
                return h;
            throw new RuntimeException();
        }catch (Exception e){
            String stringNum = hourFromName;
            if(stringNum.matches("^\\d$"))
                return stringNum;
            else
                return "";
        }
    }

    /**
     * Parse course description from the JSON
     *
     * @param courseNum Course number to identify the course
     * @return Course description
     */
    public String getCourseDescription(String courseNum){
        String path = "$.['" + courseNum + "'].Description";
        try {
            return CatalogFile.read(path);
        }catch (NumberFormatException e){
            return "";
        }
    }

    /**
     * Gets all the courses in the DegreePlan that are a certain type
     *
     * @param type target course type
     * @return List of courses of that type
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

    /**
     * Accessor methods to be used outside the class.
     */
    public long getNumOptional() { return numOptional; }
    public List<Course> getCore() { return requiredCore; }
    public List<Course> getOptionalCore() { return optionalCore; }
    public List<Course> getAdmissionPrerequisites() { return admissionPrerequisites; }
    public List<Course> getTrackPrerequisites() { return trackPrerequisites; }
    public List<String> getExcludedElectives() { return excludedElectives; }
    public Concentration getConcentration() { return concentration; }

    public Set<String> getUtdCatalogCourseNums() {
        return utdCatalogCourseNums;
    }
}
