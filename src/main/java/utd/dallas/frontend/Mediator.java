package utd.dallas.frontend;

import javafx.stage.Stage;
import utd.dallas.backend.Student;

import java.io.File;

public class Mediator {
    private static Mediator INSTANCE;

    private Stage stage;
    private Student student;
    private File defaultDirectory;

    public static Mediator getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Mediator();
        }
        return INSTANCE;
    }

    private Mediator() {
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        defaultDirectory = userDirectory;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public File getDefaultDirectory() { return defaultDirectory; }

    public void setDefaultDirectory(String defaultDirectory) {
        File f = new File(defaultDirectory);
        if(f.isDirectory());
            this.defaultDirectory = f;
    }
}
