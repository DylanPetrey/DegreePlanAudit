public class Plan {
    private int numOptional = 0;
    private concentration currentPlan;
    private Course[] requiredCore = new Course[] {};
    private Course[] optionalCore = new Course[] {};
    private Course[] admissionPrerequisites = new Course[] {};
    private Course[] trackPrerequisites = new Course[] {};
    private String[] excludedElectives = new String[] {};

    public enum concentration {
        TRADITIONAL,
        NETWORKS,
        INTEL,
        INTERACTIVE,
        SYSTEMS,
        DATA,
        CYBER,
        SOFTWARE;
    }


    public Plan(String degreePlan) {
        setConcentration(degreePlan);
    }

    public void setDegreePlan(String degreePlan) {
        setConcentration(degreePlan);
    }

    private void setConcentration(String degreePlan) {
        switch (degreePlan) {
            case "Traditional":
                currentPlan = concentration.TRADITIONAL;
                traditionalComputerScience();
                break;
            case "Networks and Telecommunications":
                currentPlan = concentration.NETWORKS;
                networksAndTelecommunication();
                break;
            case "Intelligent Systems":
                currentPlan = concentration.INTEL;
                intelligentSystems();
                break;
            case "Interactive Computing":
                currentPlan = concentration.INTERACTIVE;
                interactiveComputing();
                break;
            case "Systems":
                currentPlan = concentration.SYSTEMS;
                systemsPlan();
                break;
            case "Data Science":
                currentPlan = concentration.DATA;
                dataScience();
                break;
            case "Cyber Security":
                currentPlan = concentration.CYBER;
                cyberSecurity();
                break;
            case "Software Engineering":
                currentPlan = concentration.SOFTWARE;
                softwareEngineering();
                break;
        }
    }


    private void traditionalComputerScience(){
        numOptional = 2;
        requiredCore = new Course[] {
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE),
                new Course("CS 6378", "Advanced Operating Systems", Course.CourseType.CORE),
                new Course("CS 6390", "Advanced Computer Networks", Course.CourseType.CORE)
                };
        optionalCore = new Course[] {
                new Course("CS 6353", "Compiler Construction", Course.CourseType.OPTIONAL),
                new Course("CS 6360", "Database Design", Course.CourseType.OPTIONAL),
                new Course("CS 6371", "Advanced Programming Languages", Course.CourseType.OPTIONAL)
                };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE),
                new Course("CS 5349", "Automata Theory", Course.CourseType.PRE),
                new Course("CS 5390", "Computer Networks", Course.CourseType.PRE)
        };
    }

    private void networksAndTelecommunication(){
        requiredCore = new Course[] {
                new Course("CS 6352", "Perf. of Computer Systems and Networks", Course.CourseType.CORE),
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE),
                new Course("CS 6378", "Advanced Operating Systems", Course.CourseType.CORE),
                new Course("CS 6385", "Algorithmic Aspects of Telecomm. Networks", Course.CourseType.CORE),
                new Course("CS 6390", "Advanced Computer Networks", Course.CourseType.CORE)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE),
                new Course("CS 5390", "Computer Networks", Course.CourseType.PRE),
                new Course("CS 3341", "Probability & Statistics in CS", Course.CourseType.PRE)
        };
    }

    private void intelligentSystems(){
        numOptional = 1;
        requiredCore = new Course[] {
                new Course("CS 6320", "Natural Language Processing", Course.CourseType.CORE),
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE),
                new Course("CS 6364", "Artificial Intelligence", Course.CourseType.CORE),
                new Course("CS 6375", "Machine Learning", Course.CourseType.CORE)
        };
        optionalCore = new Course[] {
                new Course("CS 6360", "Database Design", Course.CourseType.OPTIONAL),
                new Course("CS 6378", "Advanced Operating Systems", Course.CourseType.OPTIONAL)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE)
        };
    }

    private void interactiveComputing(){
        numOptional = 3;
        requiredCore = new Course[] {
                new Course("CS 6326", "Human Computer Interaction", Course.CourseType.CORE),
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE)
        };
        optionalCore = new Course[] {
                new Course("CS 6323", "Computer Animation and Gaming", Course.CourseType.OPTIONAL),
                new Course("CS 6328", "Modeling and Simulation", Course.CourseType.OPTIONAL),
                new Course("CS 6331", "Multimedia Systems", Course.CourseType.OPTIONAL),
                new Course("CS 6334", "Virtual Reality", Course.CourseType.OPTIONAL),
                new Course("CS 6366", "Computer Graphics", Course.CourseType.OPTIONAL)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE)
        };
    }

    private void systemsPlan(){
        numOptional = 1;
        requiredCore = new Course[] {
                new Course("CS 6304", "Computer Architecture", Course.CourseType.CORE),
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE),
                new Course("CS 6378", "Advanced Operating Systems", Course.CourseType.CORE),
                new Course("CS 6396", "Real-Time Systems", Course.CourseType.CORE)
        };
        optionalCore = new Course[] {
                new Course("CS 6349", "Network Security", Course.CourseType.OPTIONAL),
                new Course("CS 6376", "Parallel Processing", Course.CourseType.OPTIONAL),
                new Course("CS 6380", "Distributed Computing", Course.CourseType.OPTIONAL),
                new Course("CS 6397", "Synthesis and Opt. of High-Perf. Systems", Course.CourseType.OPTIONAL),
                new Course("CS 6399", "Parallel Architectures and Systems", Course.CourseType.OPTIONAL)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE),
                new Course("CS 5390", "Computer Networks", Course.CourseType.PRE)
        };
    }

    private void dataScience(){
        numOptional = 1;
        requiredCore = new Course[] {
                new Course("CS 6313", "Statistical Methods for Data Sciences", Course.CourseType.CORE),
                new Course("CS 6350", "Big Data Management and Analytics", Course.CourseType.CORE),
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE),
                new Course("CS 6375", "Machine Learning", Course.CourseType.CORE)
        };
        optionalCore = new Course[] {
                new Course("CS 6301", "Social Network Analytics", Course.CourseType.OPTIONAL),
                new Course("CS 6320", "Natural Language Processing", Course.CourseType.OPTIONAL),
                new Course("CS 6327", "Video Analytics", Course.CourseType.OPTIONAL),
                new Course("CS 6347", "Statistics for Machine Learning", Course.CourseType.OPTIONAL),
                new Course("CS 6360", "Database Design", Course.CourseType.OPTIONAL)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE),
                new Course("CS 3341", "Probability & Statistics in CS", Course.CourseType.PRE)
        };
    }

    private void cyberSecurity(){
        numOptional = 2;
        requiredCore = new Course[] {
                new Course("CS 6324", "Information Security", Course.CourseType.CORE),
                new Course("CS 6363", "Design and Analysis of Computer Algorithms", Course.CourseType.CORE),
                new Course("CS 6378", "Advanced Operating Systems", Course.CourseType.CORE)
        };
        optionalCore = new Course[] {
                new Course("CS 6332", "System Security & Malicious Code Analysis", Course.CourseType.OPTIONAL),
                new Course("CS 6348", "Data and Applications Security", Course.CourseType.OPTIONAL),
                new Course("CS 6349", "Network Security", Course.CourseType.OPTIONAL),
                new Course("CS 6377", "Introduction To Cryptography", Course.CourseType.OPTIONAL)
        };
        trackPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.TRACK),
                new Course("CS 5330", "Computer Science II", Course.CourseType.TRACK),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.TRACK),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.TRACK),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.TRACK),
                new Course("CS 5390", "Computer Networks", Course.CourseType.TRACK)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5349", "Automata Theory", Course.CourseType.PRE),
                new Course("CS 5354", "Software Engineering", Course.CourseType.PRE),
                new Course("CS 3341", "Probability & Statistics in CS", Course.CourseType.PRE)
        };
    }

    private void softwareEngineering(){
        excludedElectives = new String[]{"CS 6359"};
        requiredCore = new Course[] {
                new Course("SE 6329", "Perf. of Computer Systems and Networks", Course.CourseType.CORE),
                new Course("SE 6361", "Advanced Requirements Engineering", Course.CourseType.CORE),
                new Course("SE 6362", "Adv Software Architecture & Design", Course.CourseType.CORE),
                new Course("SE 6367", "Software Testing, Validation, Verification", Course.CourseType.CORE),
                new Course("SE 6387", "Advanced Software Engineering Project", Course.CourseType.CORE)
        };
        admissionPrerequisites = new Course[]{
                new Course("CS 5303", "Computer Science I", Course.CourseType.PRE),
                new Course("CS 5330", "Computer Science II", Course.CourseType.PRE),
                new Course("CS 5333", "Discrete Structures", Course.CourseType.PRE),
                new Course("CS 5343", "Algorithm Analysis & Data Structures", Course.CourseType.PRE),
                new Course("CS 5348", "Operating System Concepts", Course.CourseType.PRE),
                new Course("CS 5354", "Software Engineering", Course.CourseType.PRE),
        };
    }

    public boolean isCore(Course courseNumber){
        for(Course currentClass : requiredCore){
            if(courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public boolean isOpt(Course courseNumber){
        for(Course currentClass : optionalCore){
            if(courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public boolean isTrack(Course courseNumber){
        for(Course currentClass : trackPrerequisites){
            if(courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }

    public boolean isPre(Course courseNumber){
        for(Course currentClass : admissionPrerequisites){
            if(courseNumber.equals(currentClass))
                return true;
        }
        return false;
    }


    public int getNumOptional() { return numOptional; }
    public Course[] getCore() { return requiredCore;}
    public Course[] getOptionalCore() { return optionalCore;}
    public Course[] getAdmissionPrerequisites() { return admissionPrerequisites; }
    public Course[] getTrackPrerequisites() { return trackPrerequisites; }

}
