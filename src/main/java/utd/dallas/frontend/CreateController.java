package utd.dallas.frontend;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utd.dallas.backend.*;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
    List<FlowObject> ListOfFlowObjects = new ArrayList<>();
    DragDropHandler DDHandler;
    double cardWidth;

    ObservableList<String>
            csTracks = FXCollections.observableArrayList(
                    "Traditional Computer Science", "Network Telecommunications", "Intelligent Systems", "Interactive Computing", "Systems", "Data Science", "Cyber Security"),
            softwareTracks = FXCollections.observableArrayList("Software Engineering"),
            semesterValues = FXCollections.observableArrayList();




    @FXML
    protected void initialize() {
        try {
            currentStudent = Mediator.getInstance().getStudent();
            if(currentStudent == null)
                currentStudent = new Student();
            else
                setInitalFields();
        } catch (Exception e){
            currentStudent = new Student();
        }

        DDHandler = new DragDropHandler();
        semesterValues.addAll(getSemesterValues());

        Platform.runLater(() -> {
            double maxWidth = 0;
            for (FlowObject listOfFlowObject : ListOfFlowObjects) {
                CourseCard current = listOfFlowObject.getObservableCard().get(0);
                double cardWidth = current.getCard().localToScene(current.getCard().getBoundsInLocal()).getWidth();

                if (cardWidth > maxWidth)
                    maxWidth = cardWidth;
            }

            CorePane.setMinViewportWidth(maxWidth + 10);
            OptionalPane.setMinViewportWidth(maxWidth + 10);

            DDHandler.setupScrolling(CorePane);
            DDHandler.setupScrolling(OptionalPane);
        });

        trackBox.setItems(csTracks);

        setListeners();

        if(currentStudent.getCurrentMajor().equals("Software Engineering"))
            onSWEButtonClick();
        else
            onCSButtonClick();

    }

    private List<String> getSemesterValues(){
        LocalDate currentdate = LocalDate.now();
        double day = currentdate.getDayOfYear();
        int year = currentdate.getYear();

        List<String> semesterList = new ArrayList<>();
        semesterList.add("Semester");


        int currentYear = year % 2000;
        String[] semesters = new String[]{"SP", "SU", "F"};
        int currentSemester = (int) (day / (366.0/3));

        for(double i = currentYear + (currentSemester+1)/3.0; i > currentYear-10; i = i - 1/3.0){
            String currentSem = (int)i + semesters[(int) ((i - (int) i) * 10 / 3)];
            semesterList.add(currentSem);
        }


        return semesterList;
    }

    protected void setInitalFields(){
        studentName.setText(currentStudent.getStudentName());
        studentID.setText(currentStudent.getStudentId());
        studentSemAdmitted.setText(currentStudent.getStartDate());
        fastTrack.setSelected(currentStudent.isFastTrack());
        thesis.setSelected(currentStudent.isThesis());
    }



    @FXML
    protected void onSWEButtonClick() {
        trackBox.setItems(FXCollections.observableArrayList(softwareTracks));
        trackBox.setValue(softwareTracks.get(0));
        currentStudent.setCurrentMajor("Software Engineering");
    }

    @FXML
    protected void onCSButtonClick() {
        trackBox.setItems(FXCollections.observableArrayList(csTracks));
        trackBox.setValue(csTracks.get(0));
        currentStudent.setCurrentMajor("Computer Science");
    }

    @FXML
    protected void onPrintButtonClick() {
        new Audit(currentStudent);
        new Form(currentStudent).print();
    }

    @FXML
    protected void setTrackBox(){
        Object value = trackBox.getValue();
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
        ListOfFlowObjects = new ArrayList<>();
        resetVbox(coreVBox, Course.CourseType.CORE);
        resetVbox(approvedVBox, Course.CourseType.ELECTIVE);
        resetVbox(additionalVBox, Course.CourseType.ADDITIONAL);
        resetVbox(preReqVBox, Course.CourseType.PRE);
        resetVbox(optionalVBox, Course.CourseType.OPTIONAL);
        resetVbox(transcriptVBox, Course.CourseType.OTHER);
    }


    private void resetVbox(VBox current, Course.CourseType t){
        // Replace old flow pane
        current.getChildren().remove(current.lookup("FlowPane"));
        FlowObject currentFlow = new FlowObject(t, current, semesterValues);
        ListOfFlowObjects.add(currentFlow);

        // Add empty courses to modify
        if(current != optionalVBox) {
            currentStudent.addCourse(new StudentCourse(t));
        }

        // Add courses to current flow
        for (StudentCourse currentCourse : currentStudent.getCourseType(t)) {
            currentFlow.addCard(currentCourse);
        }

        // Add flow to pane
        current.getChildren().add(currentFlow.getFlowPane());

        // Create listeners for cards
        Platform.runLater(() -> {
            DDHandler.createDropListener(currentFlow);

            for (CourseCard card : currentFlow.getObservableCard()) {
                createCourseCardListener(card);
            }

            currentFlow.getObservableCard().addListener((ListChangeListener<? super CourseCard>) change -> {
                while(change.next()){
                    if(change.wasAdded())
                        change.getAddedSubList().forEach(this::createCourseCardListener);
                }
            });
        });
    }

    private void createCourseCardListener(CourseCard card){
        screenClickListener(card);
        addRemoveBlankCardListener(card);

        /*
            make sure the following command is added to the VM classpath:
            --add-exports javafx.base/com.sun.javafx.event=org.controlsfx.controls
         */
        TextFields.bindAutoCompletion(
                card.getCourseNumField(),
                currentStudent.getCurrentPlan().getUtdCatalogCourseNums().toArray())
                .setOnAutoCompleted(e -> autoFillValues(card));
    }

    /**
     * Autofill values from course catalog if they exist
     *
     * @param target target course to fill
     */
    public void autoFillValues(CourseCard target){
        try {
            String num = target.currentCourse.getCourseNumber();
            String title = currentStudent.getCurrentPlan().getCourseTitle(num);
            String hours = currentStudent.getCurrentPlan().getCourseHours(num);
            target.getCurrentCourse().setCourseTitle(title);
            target.getCourseTitleField().setText(title);
            target.getCurrentCourse().setHours(hours);
            target.getCourseHoursField().setText(String.valueOf(hours));

        } catch (Exception ignore) { }
    }

    public void screenClickListener(CourseCard card){
        Scene currentScene = basePane.getScene();
        if(currentScene != null) {
            DDHandler.createDragListener(card);
            currentScene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                if (inHierarchy(evt.getPickResult().getIntersectedNode(), card.summaryCard.getDeleteButton())) {
                    String courseNum = card.getCurrentCourse().getCourseNumber();
                    currentStudent.getCourseList().remove(card.getCurrentCourse());
                    card.getParent().getObservableCard().remove(card);

                    evt.consume();
                    return;
                }

                if (!inHierarchy(evt.getPickResult().getIntersectedNode(), card.stackContainer)) {
                    card.card.setVisible(false);
                }
            });
        }
    }

    // This creates/removes a new card based on if the course is empty after you finish editing the card
    public void addRemoveBlankCardListener(CourseCard card){
        AtomicBoolean isEmpty = new AtomicBoolean(card.summaryCard.isEmptyCourse().get());
        card.summary.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                boolean cardEmpty = card.summaryCard.isEmptyCourse().get();
                if(cardEmpty != isEmpty.get()){
                    if(!cardEmpty){
                        card.getParent().removeCard(card);
                        currentStudent.getCourseList().remove(card.getCurrentCourse());
                    } else {
                        StudentCourse newCourse = new StudentCourse(card.parent.getType());
                        card.getParent().addCard(newCourse);
                        currentStudent.getCourseList().add(newCourse);
                    }
                }
            } else {
                isEmpty.set(card.summaryCard.isEmptyCourse().get());
            }
        });
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