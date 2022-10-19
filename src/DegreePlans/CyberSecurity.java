package DegreePlans;

public class CyberSecurity extends Plan {
    public CyberSecurity(){
        core = new String[] {"CS 6324", "CS 6363", "CS 6378"};
        optionalCore = new String[] {"CS 6332", "CS 6348", "CS 6349", "CS 6377"};
        admissionPrerequisites = new String[] {"CS 5349", "CS 5354", "CS 3341"};
        trackPrerequisites = new String[] {"CS 5303", "CS 5330", "CS 5333", "CS 5343", "CS 5348", "CS 5390"};
        numOptional = 2;
    }
}

