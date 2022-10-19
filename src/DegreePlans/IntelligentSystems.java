package DegreePlans;

public class IntelligentSystems extends Plan {
    public IntelligentSystems(){
        core = new String[] {"CS 6320", "CS 6363", "CS 6364", "CS 6375"};
        optionalCore = new String[] {"CS 6360", "CS 6378"};
        admissionPrerequisites = new String[] {"CS 5303", "CS 5330", "CS 5333", "CS 5343", "CS 5348"};
        numOptional = 1;
    }
}
