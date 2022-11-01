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

    private JSONObject obj = new JSONObject();
    private long numOptional = 0;
    private Concentration concentration;
    private List<Course> requiredCore = new ArrayList<Course>();
    private List<Course> optionalCore = new ArrayList<Course>();
    private List<Course> admissionPrerequisites = new ArrayList<Course>();
    private List<Course> trackPrerequisites = new ArrayList<Course>();
    private List<String> excludedElectives = new ArrayList<String>();

    Plan(Concentration concentration) {
        this.concentration = concentration;
        setConcentration(concentration);
    }

    private List<String> toList(List<String> obj) {
        List<String> list = new ArrayList<>();
        if(obj.size() == 0)
            return list;

        Iterator<?> it = obj.iterator();
        while (it.hasNext()) {
            String currentNum = (String) it.next();
            if(currentNum != "")
                list.add(currentNum );
        }
        return list;
    }

    private List<Course> toList(JSONArray array, Course.CourseType type) {
        List<Course> list = new ArrayList<>();
        if(array == null)
            return list;

        Iterator<?> it = array.iterator();
        while (it.hasNext()) {
            JSONObject currentCourse = (JSONObject) it.next();
            String num = (String) currentCourse.get("courseNumber");
            String title = (String) currentCourse.get("courseDescription");
            list.add(new Course(num, title, type));
        }
        return list;
    }

    public void setConcentration(Concentration type) {
        String JSONfilename = "JSONobjects/degreeRequirements.json";
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

            List<String> excludedElective = (List) jsonCurrentPlan.get("excludedElectives");
            this.excludedElectives = toList(excludedElective);
            System.out.println();

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

    public long getNumOptional() {
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
