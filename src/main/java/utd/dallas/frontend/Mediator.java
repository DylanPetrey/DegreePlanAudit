package utd.dallas.frontend;

import javafx.stage.Stage;
import utd.dallas.backend.Student;

import java.io.File;

/**
 * This class is to store values between screens
 */
public class Mediator {
    private static Mediator INSTANCE;
    private Student student;
    private File defaultDirectory;
    private File downloadDirectory;

    /**
     * Gets the current mediator object in the program
     */
    public static Mediator getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Mediator();
        }
        return INSTANCE;
    }

    /**
     * Initializes mediator and sets the default directory location to the home directory
     */
    private Mediator() {
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        defaultDirectory = userDirectory;
    }

    /**
     * Student accessor and mutator method. Used to pass a student object between screens
     */
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    /**
     * File directory accessor and mutator methods. Used to store most recent directory
     */
    public File getDefaultDirectory() { return defaultDirectory; }
    public void setDefaultDirectory(String defaultDirectory) {
        File f = new File(defaultDirectory);
        if(f.isDirectory()) {
            this.defaultDirectory = f;
            if(downloadDirectory == null){
                downloadDirectory = f;
            }
        }
    }

    public File getDownloadDirectory() { return downloadDirectory; }
    public void setDownloadDirectory(String downloadDirectory) {
        File f = new File(downloadDirectory);
        if(f.isDirectory()) {
            this.downloadDirectory = f;
            if(defaultDirectory == null){
                defaultDirectory = f;
            }
        }
    }





}
