package DegreePlans;

public class DataScience extends Plan {
    public DataScience(){
        core = new String[] {"CS 6313", "CS 6350", "CS 6363", "CS 6375"};
        optionalCore = new String[] {"CS 6301", "CS 6320", "CS 6327", "CS 6347", "CS 6360"};
        admissionPrerequisites = new String[] {"CS 5303", "CS 5330", "CS 5333", "CS 5343", "CS 5348", "CS 3341"};
        numOptional = 1;
    }
}
