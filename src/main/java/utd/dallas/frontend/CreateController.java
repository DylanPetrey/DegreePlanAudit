package utd.dallas.frontend;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import utd.dallas.backend.Course;
import utd.dallas.backend.Plan;
import utd.dallas.backend.Student;
import utd.dallas.backend.StudentCourse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static utd.dallas.frontend.CourseCard.inHierarchy;

public class CreateController {
    @FXML private AnchorPane basePane;
    @FXML private Button backButton;
    @FXML private TextField studentName;
    @FXML private TextField studentID;
    @FXML private TextField studentSemAdmitted;
    @FXML private CheckBox fastTrack;
    @FXML private CheckBox thesis;
    @FXML private TextField anticipatedGrad;
    @FXML private VBox coreVBox;
    @FXML private VBox approvedVBox;
    @FXML private VBox additionalVBox;
    @FXML private VBox preReqVBox;
    @FXML private ScrollPane OptionalPane;
    @FXML private ScrollPane CorePane;
    @FXML private VBox optionalVBox;
    @FXML private VBox transcriptVBox;
    @FXML private ChoiceBox<String> trackBox;
    public Student currentStudent;
    private Double cardWidth;
    List<FlowObject> ListOfFlowObjects = new ArrayList<>();
    ObservableList<String>
            csTracks = FXCollections.observableArrayList(
                    "Traditional Computer Science", "Network Telecommunications", "Intelligent Systems", "Interactive Computing", "Systems", "Data Science", "Cyber Security"),
            softwareTracks = FXCollections.observableArrayList(
                    "Software Engineering");



    @FXML
    protected void initialize() {
        try {
            currentStudent = Mediator.getInstance().getStudent();
            setInitalFields();
        } catch (Exception e){
            currentStudent = new Student();
        }

        Platform.runLater(() -> {
            FlowPane flow = (FlowPane) CorePane.getContent().lookup("FlowPane");
            cardWidth = flow.getChildren().get(0).getBoundsInParent().getWidth();
            OptionalPane.setMinViewportWidth(cardWidth + 3);
            CorePane.setMinViewportWidth(cardWidth + 3);

        });

        trackBox.setItems(csTracks);

        setListeners();

        if(currentStudent.getCurrentMajor().equals("Software Engineering"))
            onSWEButtonClick();
        else
            onCSButtonClick();


    }



    protected void setInitalFields(){
        studentName.setText(currentStudent.getStudentName());
        studentID.setText(currentStudent.getStudentId());
        studentSemAdmitted.setText(currentStudent.getStartDate());
        fastTrack.setSelected(currentStudent.isFastTrack());
    }

    @FXML
    protected void onSWEButtonClick() {
        trackBox.setItems(FXCollections.observableArrayList(softwareTracks));
        trackBox.setValue(softwareTracks.get(0));
        currentStudent.setCurrentMajor("Software Engineering");
        resetAllVBox();
    }

    @FXML
    protected void onCSButtonClick() {
        trackBox.setItems(FXCollections.observableArrayList(csTracks));
        trackBox.setValue(csTracks.get(0));

        currentStudent.setCurrentMajor("Computer Science");
    }

    @FXML
    protected void onPrintButtonClick() {
        currentStudent.printStudentInformation();
    }

    @FXML
    protected void setTrackBox(){
        Object value = trackBox.getValue();
        currentStudent.newFormList();
        if ("Traditional Computer Science".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.TRADITIONAL);
        } else if ("Network Telecommunications".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.NETWORKS);
        } else if ("Intelligent Systems".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.INTEL);
        } else if ("Interactive Computing".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.INTERACTIVE);
        } else if ("Systems".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.SYSTEMS);
        } else if ("Data Science".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.DATA);
        } else if ("Cyber Security".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.CYBER);
        } else if ("Software Engineering".equals(value)) {
            currentStudent.setCurrentPlan(Plan.Concentration.SOFTWARE);
        }

        resetAllVBox();
    }

    private void resetAllVBox(){
        resetVbox(coreVBox, Course.CourseType.CORE);
        resetVbox(approvedVBox, Course.CourseType.ELECTIVE);
        resetVbox(additionalVBox, Course.CourseType.ADDITIONAL);
        resetVbox(preReqVBox, Course.CourseType.PRE);
        resetVbox(optionalVBox, Course.CourseType.OPTIONAL);
        resetVbox(transcriptVBox, Course.CourseType.OTHER);
    }


    private void resetVbox(VBox current, Course.CourseType t){
        current.getChildren().remove(current.lookup("FlowPane"));

        FlowObject g = new FlowObject(t);
        ListOfFlowObjects.add(g);
        List<StudentCourse> courseOfType = currentStudent.getCourseType(t);
        if(current != optionalVBox) {
            courseOfType.add(new StudentCourse(t));
        }
        addCoursesToObject(g, courseOfType);
        current.getChildren().add(g.getFlowPane());

        for(CourseCard card : g.getObservableCard()){
            createCourseCardListener(card, g);
        }
    }

    private void createCourseCardListener(CourseCard c, FlowObject g){
        Scene currentScene = basePane.getScene();
        if(currentScene != null) {
            currentScene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                if (inHierarchy(evt.getPickResult().getIntersectedNode(), c.summaryCard.getDeleteButton())) {
                    if (g.getType() != Course.CourseType.OPTIONAL){
                        currentStudent.getCourseList().remove(c.getCurrentCourse());
                        g.getObservableCard().remove(c);
                    }
                }

                if (!inHierarchy(evt.getPickResult().getIntersectedNode(), c.stackContainer)) {
                    c.card.setVisible(false);

                    List<CourseCard> empty = g.removeEmpty();
                    g.getObservableCard().removeAll(empty);
                    empty.forEach(courseCard -> currentStudent.getCourseList().remove(courseCard.getCurrentCourse()));

                    if(!g.hasAddCourse() && g.getType() != Course.CourseType.OPTIONAL){
                        StudentCourse newCourse = new StudentCourse(g.getType());
                        currentStudent.getCourseList().add(newCourse);
                        g.addCard(newCourse);
                    }
                }
            });
        }

        Platform.runLater(() -> {
            createCourseCardListener(c, g);
        });
    }

    private void addCoursesToObject(FlowObject g, List<StudentCourse> courseOfType) {
        for (StudentCourse studentCourse : courseOfType) {
            StudentCourse currentCourse;
            currentCourse = studentCourse;
            g.addCard(currentCourse);
        }
    }

    protected void setListeners(){
        trackBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                setTrackBox();
            } catch (NullPointerException e){
                setTrackBox();
            }
        });

        studentName.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentStudent.setStudentName((String) newValue);
            } catch (NullPointerException e){
                currentStudent.setStudentName((String) oldValue);
            }
        });

        studentID.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentStudent.setStudentId((String) newValue);
            } catch (NullPointerException e){
                currentStudent.setStudentId((String) oldValue);
            }
        });

        studentSemAdmitted.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentStudent.setStartDate((String) newValue);
            } catch (NullPointerException e){
                currentStudent.setStartDate((String) oldValue);
            }
        });

        anticipatedGrad.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentStudent.setGraduation((String) newValue);
            } catch (NullPointerException e){
                currentStudent.setGraduation((String) oldValue);
            }
        });

        fastTrack.selectedProperty().addListener((observable, oldValue, newValue) -> {
            currentStudent.setFastTrack(fastTrack.isSelected());
        });

        thesis.selectedProperty().addListener((observable, oldValue, newValue) -> {
            currentStudent.setThesis(thesis.isSelected());
        });
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        Mediator.getInstance().setStudent(new Student());

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("IntroScreen.fxml")));
        Stage stage = (Stage) backButton.getScene().getWindow();
        Scene currentStage = basePane.getScene();
        stage.setScene(new Scene(root, currentStage.getWidth(), currentStage.getHeight()));
    }

    @FXML protected void setStudentName(){
        currentStudent.setStudentName(studentName.getText());
    }
    @FXML protected void setStudentID(){
        currentStudent.setStudentId(studentID.getText());
    }
    @FXML protected void setStudentSemAdmitted(){
        currentStudent.setStartDate(studentSemAdmitted.getText());
    }
}