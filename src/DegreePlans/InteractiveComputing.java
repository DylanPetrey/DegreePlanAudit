package DegreePlans;

public class InteractiveComputing extends Plan {
    public InteractiveComputing() {
        core = new String[]{"CS 6326", "CS 6363"};
        optionalCore = new String[]{"CS 6323", "CS 6328", "CS 6331", "CS 6334", "CS 6366"};
        admissionPrerequisites = new String[]{"CS 5303", "CS 5330", "CS 5333", "CS 5343", "CS 5348"};
        numOptional = 3;
    }

}
