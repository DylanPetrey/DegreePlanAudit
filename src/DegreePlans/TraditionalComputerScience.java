package DegreePlans;

public class TraditionalComputerScience extends Plan {
    public TraditionalComputerScience(){
        core = new String[] {"CS 6363", "CS 6378", "CS 6390"};
        optionalCore = new String[] {"CS 6353", "CS 6360", "CS 6371"};
        admissionPrerequisites = new String[] {"CS 5303", "CS 5330", "CS 5333", "CS 5343", "CS 5348", "CS 5349", "CS 5390"};
        numOptional = 2;
    }
}
