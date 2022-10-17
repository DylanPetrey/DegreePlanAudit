public enum Concentration {

        TRADITIONAL("Traditional"),
        NETWORKS("Networks and Telecommunications"),
        INTEL("Intelligent Systems"),
        CYBER("Cyber Security"),
        INTERACTIVE("Interactive Computing"),
        SYSTEMS("Systems"),
        DATA("Data Science"),
        SOFTWARE("Software Engineering");

    private String concentration;

    Concentration(String concentration) {
        this.concentration = concentration;
    }

    public String getConcentration() { return this.concentration; }
}
