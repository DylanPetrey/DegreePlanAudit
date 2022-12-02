package utd.dallas.frontend;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import utd.dallas.backend.StudentCourse;


public class CourseCard {
    private static final int MAX_TITLE_LENGTH = 42;
    private final double maxTextWidth;
    StudentCourse currentCourse;
    StackPane stackContainer;
    StackPane card;
    StackPane summary;
    SummaryCard summaryCard;
    FlowObject parent;
    TextField courseNumField = new TextField();
    TextField courseTitleField = new TextField();
    TextField courseHoursField = new TextField();
    ObservableList<String>
            gradeValues = FXCollections.observableArrayList("Grade", "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F", "I", "CR", "NC", "W", "WL"),
            semesterValues = FXCollections.observableArrayList();


    CourseCard(StudentCourse currentCourse, ObservableList<String> semesterValues) {
        // Initialize max width
        Text t = new Text("Design and Analysis of Computer Algorithms");
        t.setFont(Font.getDefault());
        maxTextWidth = t.getLayoutBounds().getWidth() + 20;

        this.semesterValues = semesterValues;

        // Initialize objects
        this.currentCourse = currentCourse;
        this.summaryCard = new SummaryCard();
        this.summary = summaryCard.getSummaryCard();
        this.card = createCard();

        card.setMaxWidth(Region.USE_PREF_SIZE);
        summaryCardConstraints();

        card.setStyle("-fx-border-color: black");
        summary.visibleProperty().bind(card.visibleProperty().not());
        card.setVisible(false);

        stackContainer = new StackPane(summary, card);
        stackContainer.getChildren().forEach(child -> VBox.setVgrow(child, Priority.ALWAYS));
        stackContainer.getChildren().forEach(child -> HBox.setHgrow(child, Priority.ALWAYS));

        stackContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    summary.setStyle("-fx-border-color: black; -fx-border-radius:  5;  -fx-padding: 5");
                    card.setVisible(true);
                }
            }
        });
    }


    private void summaryCardConstraints(){
        double scale = -0.15;
        summary.setScaleX(summary.getScaleX() + scale);
        summary.setScaleY(summary.getScaleY() + scale);

        summary.setLayoutX(card.getLayoutX());
        summary.setLayoutY(card.getLayoutY());
        summary.setStyle("-fx-border-color: black; -fx-border-radius: 5; -fx-padding: 5");

        summary.setOnMouseEntered(event -> {
            if(!card.isVisible())
                summary.setStyle("-fx-background-color: #c6c6c6; -fx-background-radius: 5; -fx-border-color: black; -fx-border-radius:  5; -fx-padding: 5");
        });
        summary.setOnMouseExited(event -> {
            if(!card.isVisible())
                summary.setStyle("-fx-border-color: black; -fx-border-radius:  5;  -fx-padding: 5");
        });
    }

    public static boolean inHierarchy(Node node, Node potentialHierarchyElement) {
        if (potentialHierarchyElement == null) {
            return true;
        }
        while (node != null) {
            if (node == potentialHierarchyElement) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    private HBox createTopHBox(){
        HBox current = new HBox();
        current.setAlignment(Pos.CENTER);
        current.setSpacing(10);
        current.setMaxWidth(maxTextWidth);

        courseNumField = createNumField();

        courseHoursField = createHoursField();
        CheckBox waiverCheckBox = createWaiverBox();


        current.getChildren().addAll(courseNumField, courseHoursField, waiverCheckBox);

        return current;
    }

    private TextField createNumField(){
        TextField numField = new TextField();
        numField.setPromptText("Course Number");
        numField.setText(currentCourse.getCourseNumber());
        summaryCard.setNumText(currentCourse.getCourseNumber());

        Text t = new Text("Course Number");
        t.setFont(Font.getDefault());
        double maxWidth = t.getLayoutBounds().getWidth() + 20;
        numField.setPrefWidth(maxWidth);
        HBox.setHgrow(numField, Priority.ALWAYS);

        numField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseNumber(newValue);
                summaryCard.setNumText(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseNumber(oldValue);
                summaryCard.setNumText(oldValue);
            }
        });
        return numField;
    }
    private TextField createHoursField() {
        TextField hours = new TextField();
        hours.setPromptText("Hours");
        Text t = new Text("Hours");

        t.setFont(Font.getDefault());
        double maxWidth = t.getLayoutBounds().getWidth() + 20;
        hours.setPrefWidth(maxWidth);
        HBox.setHgrow(hours, Priority.ALWAYS);

        hours.setText(currentCourse.getHours());
        summaryCard.setHoursText(currentCourse.getHours());

        hours.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (newValue.matches("[0-9]$")) {
                    hours.setText(newValue.replaceAll("[^\\d]", ""));
                    summaryCard.setHoursText(newValue);
                    currentCourse.setHours(newValue);
                }

                if (newValue.length() > 1) {
                    newValue = newValue.substring(0, 1);
                    hours.setText(newValue);
                    summaryCard.setHoursText(newValue);
                    currentCourse.setHours(newValue);

                }
            }
        });

        return hours;
    }

    private TextField createTitleField(){
        courseTitleField = new TextField();
        courseTitleField.setPromptText("Title");
        courseTitleField.setText(currentCourse.getCourseTitle());
        summaryCard.setTitleText(currentCourse.getCourseTitle());

        courseTitleField.setMinWidth(maxTextWidth);
        courseTitleField.setPrefWidth(maxTextWidth);
        courseTitleField.setMaxWidth(maxTextWidth);

        courseTitleField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                String currentText = courseTitleField.getText();
                if (currentText.length() > MAX_TITLE_LENGTH) {
                    currentText = currentText.substring(0, MAX_TITLE_LENGTH);
                    courseTitleField.setText(currentText);
                }

                currentCourse.setCourseTitle(currentText);
                summaryCard.setTitleText(currentText);

            }
        });

        courseTitleField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseTitle(newValue);
                summaryCard.setTitleText(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseTitle(oldValue);
                summaryCard.setTitleText(oldValue);

            }
        });
        return courseTitleField;
    }



    private HBox createBottomHBox(){
        HBox current = new HBox();
        current.setAlignment(Pos.CENTER);
        current.setSpacing(10);
        current.setMaxWidth(maxTextWidth);

        ComboBox<String> semBox = createSemesterBox();
        TextField transferField = createTransferField();
        ComboBox<String> gradeBox = createGradeBox();


        current.getChildren().addAll(semBox, gradeBox, transferField);

        return current;
    }


    private ComboBox<String> createSemesterBox(){
        ComboBox<String> semesterBox = new ComboBox<>();
        semesterBox.setItems(semesterValues);
        try {
            if (currentCourse.getSemester().equals(""))
                semesterBox.setValue(semesterValues.get(0));
            else
                semesterBox.setValue(currentCourse.getSemester());
        }catch (Exception e) {
            semesterBox.setValue(semesterValues.get(0));
        }

        summaryCard.setSemesterText(currentCourse.getSemester());
        semesterBox.setVisibleRowCount(10);

        semesterBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                String semValue = "";
                if(!newValue.equals(semesterValues.get(0)))
                    semValue = newValue;
                currentCourse.setSemester(semValue);
                summaryCard.setSemesterText(semValue);
            } catch (NullPointerException e){
                String semValue = "";
                if(!oldValue.equals(semesterValues.get(0)))
                    semValue = oldValue;
                currentCourse.setSemester(semValue);
                summaryCard.setSemesterText(semValue);
            }
        });

        return semesterBox;
    }

    private TextField createTransferField(){
        TextField transferField = new TextField();
        transferField.setPromptText("Transfer");
        transferField.setText(currentCourse.getTransfer());
        summaryCard.setTransferText(currentCourse.getTransfer());

        Text t = new Text("Transfer");
        t.setFont(Font.getDefault());
        double maxTransferWidth = t.getLayoutBounds().getWidth() + 20;
        transferField.setPrefWidth(maxTransferWidth);
        HBox.setHgrow(transferField, Priority.ALWAYS);

        transferField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setTransfer(newValue);
                summaryCard.setTransferText(newValue);
            } catch (NullPointerException e){
                currentCourse.setTransfer(oldValue);
                summaryCard.setTransferText(oldValue);
            }
        });

        return transferField;
    }

    public CheckBox createWaiverBox (){
        CheckBox waiverCheckBox = new CheckBox();
        waiverCheckBox.setText("Waiver");
        waiverCheckBox.setSelected(currentCourse.isWaived());
        summaryCard.setTransferText("");
        summaryCard.setWaiveText(waiverCheckBox.isSelected());

        Text t = new Text("Waiver");
        t.setFont(Font.getDefault());
        double maxWidth = t.getLayoutBounds().getWidth() + 30;
        waiverCheckBox.setPrefWidth(maxWidth);
        HBox.setHgrow(waiverCheckBox, Priority.ALWAYS);


        waiverCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            currentCourse.setWaived(waiverCheckBox.isSelected());
            summaryCard.setWaiveText(waiverCheckBox.isSelected());
            summaryCard.setTransferText("");
        });

        return waiverCheckBox;
    }


    private ComboBox<String> createGradeBox(){
        ComboBox<String> gradeBox = new ComboBox<>();
        gradeBox.setItems(gradeValues);
        gradeBox.setValue(gradeValues.get(0));
        summaryCard.setGradeText(currentCourse.getLetterGrade());

        try {
            if (currentCourse.getLetterGrade().equals(""))
                gradeBox.setValue(gradeValues.get(0));
            else
                gradeBox.setValue(currentCourse.getLetterGrade());
        }catch (Exception e) {
            gradeBox.setValue(gradeValues.get(0));
        }

        gradeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                String gradeValue = "";
                if(!newValue.equals("Grade"))
                    gradeValue = newValue;
                currentCourse.setLetterGrade(gradeValue);
                summaryCard.setGradeText(gradeValue);
            } catch (NullPointerException e){
                String gradeValue = "";
                if(!newValue.equals("Grade"))
                    gradeValue = newValue;
                currentCourse.setLetterGrade(gradeValue);
                summaryCard.setGradeText(gradeValue);
            }
        });

        return gradeBox;
    }

    private StackPane createCard(){
        StackPane current_card = new StackPane();
        VBox vAlign = new VBox();
        vAlign.setAlignment(Pos.CENTER);
        vAlign.setSpacing(10);
        vAlign.setPadding(new Insets(10,10,10,10));

        HBox hAlign1 = createTopHBox();
        TextField titleField = createTitleField();
        HBox hAlign2 = createBottomHBox();

        HBox.setHgrow(hAlign1, Priority.ALWAYS);
        HBox.setHgrow(hAlign2, Priority.ALWAYS);

        vAlign.getChildren().addAll(hAlign1, titleField, hAlign2);
        current_card.getChildren().add(vAlign);

        return current_card;
    }
    public StackPane getCard() {
        return stackContainer;
    }

    public StudentCourse getCurrentCourse() {
        return currentCourse;
    }

    public void setParent(FlowObject parent) {
        this.parent = parent;
    }

    public FlowObject getParent() {
        return parent;
    }

    public TextField getCourseNumField() { return courseNumField; }
    public TextField getCourseTitleField() { return courseTitleField; }
    public TextField getCourseHoursField() { return courseHoursField; }
}
