import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Plan {

    public enum Concentration {

        TRADITIONAL("Traditional"),
        NETWORKS("Networks and Telecommunications"),
        INTEL("Intelligent Systems"),
        CYBER("Cyber Security"),
        INTERACTIVE("Interactive Computing"),
        SYSTEMS("Systems"),
        DATA("Data Science"),
        SOFTWARE("Software Engineering");

        private String concenString;

        Concentration(String concentration) {
            this.concenString = concentration;
        }

        public String toString() {
            return this.concenString;
        }

    };

    private int numOptional = 0;
    private Concentration concentration;
    private List<Course> requiredCore = new ArrayList<Course>();
    private List<Course> optionalCore = new ArrayList<Course>();
    private List<Course> admissionPrerequisites = new ArrayList<Course>();
    private List<Course> trackPrerequisites = new ArrayList<Course>();
    private List<Course> excludedElectives = new ArrayList<Course>();

    Plan(Concentration concentration) {
        this.concentration = concentration;
        setConcentration(concentration);
    }

    private static Course parseCourse(JSONObject obj) {
        Course course = new Course();
        course.setCourseNumber((String) obj.get("courseNumber"));
        course.setCourseDescription((String) obj.get("courseDescription"));
        return course;
    }

    private List<Course> toList(JSONArray array, Course.CourseType type) {
        List<Course> list = new ArrayList<>();
        Iterator<?> it = array.iterator();
        while (it.hasNext()) {
            list.add(parseCourse((JSONObject) it.next()));
        }
        return list;
    }

    public void setConcentration(Concentration type) {
        String s = "Plans/" + type.toString().toLowerCase() + ".json";
        System.out.println(s);
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(s));

            JSONObject jsonObject = (JSONObject) obj;
            Integer temp = (Integer) jsonObject.get("numOptional");
            if (temp != null) {
                this.numOptional = temp;
            }

            JSONArray requiredCore = (JSONArray) jsonObject.get("requiredCore");
            this.requiredCore = toList(requiredCore, Course.CourseType.CORE);

            JSONArray optionalCore = (JSONArray) jsonObject.get("optionalCore");
            this.optionalCore = toList(optionalCore, Course.CourseType.OPTIONAL);

            JSONArray admissionPrerequisite = (JSONArray) jsonObject.get("admissionPrerequisite");
            this.admissionPrerequisites = toList(admissionPrerequisite, Course.CourseType.PRE);

            JSONArray trackPrerequisite = (JSONArray) jsonObject.get("trackPrerequisite");
            this.trackPrerequisites = toList(trackPrerequisite, Course.CourseType.TRACK);

            JSONArray excludedElective = (JSONArray) jsonObject.get("excludedElective");
            this.excludedElectives = toList(excludedElective, Course.CourseType.ELECTIVE);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isCore(Course courseNumber) {
        for (Course currentClass : requiredCore) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public boolean isOpt(Course courseNumber) {
        for (Course currentClass : optionalCore) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public boolean isTrack(Course courseNumber) {
        for (Course currentClass : trackPrerequisites) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public boolean isPre(Course courseNumber) {
        for (Course currentClass : admissionPrerequisites) {
            if (courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public int getNumOptional() {
        return numOptional;
    }

    public List<Course> getCore() {
        return requiredCore;
    }

    public List<Course> getOptionalCore() {
        return optionalCore;
    }

    public List<Course> getAdmissionPrerequisites() {
        return admissionPrerequisites;
    }

    public List<Course> getTrackPrerequisites() {
        return trackPrerequisites;
    }

}
