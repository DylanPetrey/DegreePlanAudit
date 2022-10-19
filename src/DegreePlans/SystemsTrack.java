package DegreePlans;

public class SystemsTrack extends Plan {
    public SystemsTrack(){
        core = new String[] {"CS 6304", "CS 6363", "CS 6378", "CS 6396"};
        optionalCore = new String[] {"CS 6349", "CS 6376", "CS 6380", "CS 6397", "CS 6399"};
        admissionPrerequisites = new String[] {"CS 5303", "CS 5330", "CS 5333", "CS 5343", "CS 5348", "CS 5390"};
        numOptional = 1;
    }
}
