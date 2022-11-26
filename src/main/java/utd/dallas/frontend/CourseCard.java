package utd.dallas.frontend;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import utd.dallas.backend.Course;
import utd.dallas.backend.StudentCourse;


public class CourseCard {
    private static final int MAX_TITLE_LENGTH = 42;
    private final double maxTextWidth;
    StudentCourse currentCourse;
    StackPane stackContainer;
    StackPane card;
    StackPane summary;
    SummaryCard summaryCard;

    CourseCard(StudentCourse currentCourse) {
        // Initialize max width
        Text t = new Text("Design and Analysis of Computer Algorithms");
        t.setFont(Font.getDefault());
        maxTextWidth = t.getLayoutBounds().getWidth() + 20;

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
            if(!card.isVisible()) {
                summary.setStyle("-fx-background-color: #c6c6c6; -fx-background-radius: 5; -fx-border-color: black; -fx-border-radius:  5; -fx-padding: 5");
            }
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

        TextField courseNumField = new TextField();
        courseNumField.setPromptText("Course Number");
        courseNumField.setText(currentCourse.getCourseNumber());
        summaryCard.setNumText(currentCourse.getCourseNumber());

        TextField semesterField = new TextField();
        semesterField.setPromptText("Semester");
        semesterField.setText(currentCourse.getSemester());
        summaryCard.setSemesterText(currentCourse.getSemester());

        courseNumField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseNumber(newValue);
                summaryCard.setNumText(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseNumber(oldValue);
                summaryCard.setNumText(oldValue);
            }
        });

        semesterField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setSemester(newValue);
                summaryCard.setSemesterText(newValue);
            } catch (NullPointerException e){
                currentCourse.setSemester(oldValue);
                summaryCard.setSemesterText(oldValue);
            }
        });

        current.getChildren().addAll(courseNumField, semesterField);

        return current;
    }

    private TextField createTitleField(){
        TextField current = new TextField();
        current.setPromptText("Title");
        current.setText(currentCourse.getCourseTitle());
        summaryCard.setTitleText(currentCourse.getCourseTitle());

        current.setMinWidth(maxTextWidth);
        current.setPrefWidth(maxTextWidth);
        current.setMaxWidth(maxTextWidth);

        current.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                String currentText = current.getText();
                if (currentText.length() > MAX_TITLE_LENGTH) {
                    currentText = currentText.substring(0, MAX_TITLE_LENGTH);
                    current.setText(currentText);
                }

                currentCourse.setCourseTitle(currentText);
                summaryCard.setTitleText(currentText);

            }
        });

        current.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseTitle(newValue);
                summaryCard.setTitleText(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseTitle(oldValue);
                summaryCard.setTitleText(oldValue);

            }
        });
        return current;
    }



    private HBox createBottomHBox(){
        HBox current = new HBox();
        current.setAlignment(Pos.CENTER);
        current.setSpacing(10);
        current.setMaxWidth(maxTextWidth);

        TextField gradeField = new TextField();
        gradeField.setPromptText("Grade");
        current.getChildren().add(gradeField);
        gradeField.setText(currentCourse.getLetterGrade());
        summaryCard.setGradeText(currentCourse.getLetterGrade());

        gradeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setLetterGrade(newValue);
                summaryCard.setGradeText(newValue);

            } catch (NullPointerException e){
                currentCourse.setLetterGrade(oldValue);
                summaryCard.setGradeText(oldValue);
            }
        });

        if(currentCourse.getType() != Course.CourseType.PRE && currentCourse.getType() != Course.CourseType.TRACK) {
            TextField transferField = new TextField();
            transferField.setPromptText("Transfer");
            transferField.setText(currentCourse.getTransfer());
            summaryCard.setTransferText(currentCourse.getTransfer());
            summaryCard.setWaiveText(false);

            current.getChildren().addAll(transferField);

            transferField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    currentCourse.setTransfer(newValue);
                    summaryCard.setTransferText(newValue);
                    summaryCard.setWaiveText(false);
                } catch (NullPointerException e){
                    currentCourse.setTransfer(oldValue);
                    summaryCard.setTransferText(oldValue);
                    summaryCard.setWaiveText(false);
                }
            });

        } else {
            CheckBox waiverCheckBox = new CheckBox();
            waiverCheckBox.setText("Waiver");
            waiverCheckBox.setSelected(currentCourse.isWaived());
            summaryCard.setTransferText("");
            summaryCard.setWaiveText(waiverCheckBox.isSelected());

            waiverCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                currentCourse.setWaived(waiverCheckBox.isSelected());
                summaryCard.setWaiveText(waiverCheckBox.isSelected());
                summaryCard.setTransferText("");
            });

            current.getChildren().addAll(waiverCheckBox);

        }
        return current;
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
}
