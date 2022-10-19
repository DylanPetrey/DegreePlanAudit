package DegreePlans;

public class Plan {
    protected String[] core = new String[] {};
    protected String[] optionalCore = new String[] {};
    protected String[] admissionPrerequisites = new String[] {};
    protected String[] trackPrerequisites = new String[] {};
    protected int numOptional = 0;

    public Plan(){

    }

    public String[] getCore() { return core;}
    public String[] getOptionalCore() { return optionalCore;}
    public String[] getAdmissionPrerequisites() { return admissionPrerequisites; }
    public String[] getTrackPrerequisites() { return trackPrerequisites; }
    public int getNumOptional() { return numOptional; }
}
