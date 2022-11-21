package utd.dallas.frontend;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import utd.dallas.backend.Course;
import utd.dallas.backend.StudentCourse;


public class CourseCard {
    private static class SummaryCard {
        private final Pane miniCard = new Pane();
        private final Label courseNumLabel = new Label();
        private final TextFlow summarySemFlow = new TextFlow();
        private final Label summaryTitleLabel = new Label();
        private final TextFlow summaryGradeFlow = new TextFlow();
        private final Label summaryTranferWaiveLabel = new Label();

        SummaryCard(){
            VBox summaryContainer = new VBox();
            summaryContainer.setAlignment(Pos.CENTER_LEFT);
            summaryContainer.setPadding(new Insets(5,0,5,0));

            HBox summaryTop = new HBox();
            HBox summaryMid = new HBox();
            HBox summaryBot = new HBox();
            HBox summaryBotLeft = new HBox();
            HBox summaryBotRight = new HBox();

            summaryContainer.setSpacing(5);
            summaryTop.setAlignment(Pos.CENTER_LEFT);
            summaryMid.setAlignment(Pos.CENTER_LEFT);
            summaryBotLeft.setAlignment(Pos.CENTER_LEFT);
            summaryBotRight.setAlignment(Pos.CENTER_RIGHT);

            HBox.setHgrow(summaryTop,Priority.ALWAYS);
            HBox.setHgrow(summaryMid,Priority.ALWAYS);
            HBox.setHgrow(summaryBotLeft,Priority.ALWAYS);
            HBox.setHgrow(summaryBotRight,Priority.ALWAYS);

            HBox.setHgrow(courseNumLabel,Priority.ALWAYS);
            HBox.setHgrow(summaryTitleLabel,Priority.ALWAYS);
            HBox.setHgrow(summarySemFlow,Priority.ALWAYS);
            HBox.setHgrow(summaryGradeFlow,Priority.ALWAYS);

            summarySemFlow.setTextAlignment(TextAlignment.LEFT);
            summaryGradeFlow.setTextAlignment(TextAlignment.RIGHT);


            summaryBotLeft.getChildren().add(summarySemFlow);
            summaryBotRight.getChildren().add(summaryGradeFlow);

            summaryTop.getChildren().addAll(courseNumLabel);
            summaryMid.getChildren().addAll(summaryTitleLabel);
            summaryBot.getChildren().addAll(summaryBotLeft, summaryBotRight);

            summaryContainer.getChildren().addAll(summaryTop, summaryMid, summaryBot);
            miniCard.getChildren().add(summaryContainer);
        }
        public Label getSummaryTranferWaiveLabel() {
            return summaryTranferWaiveLabel;
        }

        public void setCourseNumLabel(String courseNumLabel) {
            this.courseNumLabel.setText(courseNumLabel);
            this.courseNumLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        }
        public void setCourseSemLabel(String courseSemLabel) {
            summarySemFlow.getChildren().clear();
            Text text1 = new Text("Semester: ");
            Text text2 = new Text(courseSemLabel.trim());

            text1.setTextAlignment(TextAlignment.LEFT);
            text1.setTextAlignment(TextAlignment.LEFT);

            if(text2.getText().equals(""))
                summarySemFlow.getChildren().addAll(text2);
            else {
                text2.setStyle("-fx-font-weight: bold");
                summarySemFlow.getChildren().addAll(text1, text2);
            }
        }
        public void setSummaryTitleLabel(String summaryTitleLabel) {
            this.summaryTitleLabel.setText(summaryTitleLabel);

        }
        public void setCourseGradeLabel(String courseGradeLabel) {
            summaryGradeFlow.getChildren().clear();
            Text text1 = new Text("Grade: ");
            Text text2 = new Text(courseGradeLabel.trim());

            text1.setTextAlignment(TextAlignment.RIGHT);
            text1.setTextAlignment(TextAlignment.RIGHT);

            if(text2.getText().equals(""))
                summaryGradeFlow.getChildren().addAll(text2);
            else {
                text2.setStyle("-fx-font-weight: bold");
                summaryGradeFlow.getChildren().addAll(text1, text2);
            }
        }
        public void setSummaryTranferWaiveLabel(String summaryTranferWaiveLabel) { this.summaryTranferWaiveLabel.setText(summaryTranferWaiveLabel); }

        public Pane getMiniCard() {
            return miniCard; }
    }
    StackPane stackContainer = new StackPane();
    StudentCourse currentCourse;
    private static final int MAX_TITLE_LENGTH = 42;
    private final double maxTextWidth;
    SummaryCard miniCard = new SummaryCard();
    Text courseDescription = new Text();
    Pane card = new Pane();

    CourseCard(StudentCourse currentCourse) {
        this.currentCourse = currentCourse;

        Text t = new Text("Design and Analysis of Computer Algorithms");
        t.setFont(Font.getDefault());
        maxTextWidth = t.getLayoutBounds().getWidth() + 20;

        createCard();
        card.setMaxWidth(Region.USE_PREF_SIZE);

        miniCard.getMiniCard().setLayoutX(card.getLayoutX());
        miniCard.getMiniCard().setLayoutY(card.getLayoutY());

        miniCard.getMiniCard().visibleProperty().bind(card.visibleProperty().not());

        VBox centerMini = new VBox(new HBox(miniCard.getMiniCard()));
        VBox.setVgrow(centerMini,Priority.ALWAYS);
        HBox.setHgrow(centerMini,Priority.ALWAYS);
        card.setStyle("-fx-background-color: white; -fx-border-color: black");

        double scale = -0.15;
        centerMini.setScaleX(centerMini.getScaleX() + scale);
        centerMini.setScaleY(centerMini.getScaleY() + scale);
        centerMini.setStyle("-fx-border-color: black; -fx-border-radius: 5; -fx-padding: 5");

        centerMini.setOnMouseEntered(event -> {
                if(!card.isVisible())
                    centerMini.setStyle("-fx-background-color: #c6c6c6; -fx-background-radius: 5; -fx-border-color: black; -fx-border-radius:  5; -fx-padding: 5; -fx-cursor: hand");

                });
        centerMini.setOnMouseExited(event -> {
            if(!card.isVisible())
                centerMini.setStyle("-fx-border-color: black; -fx-border-radius:  5;  -fx-padding: 5");
        });
        card.setVisible(false);
        stackContainer = new StackPane(centerMini, card);

        centerMini.onContextMenuRequestedProperty().addListener(observable ->
                    System.out.println("test")
                );

        stackContainer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    centerMini.setStyle("-fx-border-color: black; -fx-border-radius:  5;  -fx-padding: 5");
                    card.setVisible(true);
                }
            }
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
        miniCard.setCourseNumLabel(currentCourse.getCourseNumber());

        TextField semesterField = new TextField();
        semesterField.setPromptText("Semester");
        semesterField.setText(currentCourse.getSemester());
        miniCard.setCourseSemLabel(currentCourse.getSemester());

        courseNumField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseNumber(newValue);
                miniCard.setCourseNumLabel(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseNumber(oldValue);
                miniCard.setCourseNumLabel(oldValue);
            }
        });

        semesterField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setSemester(newValue);
                miniCard.setCourseSemLabel(newValue);
            } catch (NullPointerException e){
                currentCourse.setSemester(oldValue);
                miniCard.setCourseSemLabel(oldValue);
            }
        });

        current.getChildren().addAll(courseNumField, semesterField);

        return current;
    }

    private TextField createTitleField(){
        TextField current = new TextField();
        current.setPromptText("Title");
        current.setText(currentCourse.getCourseTitle());
        miniCard.setSummaryTitleLabel(currentCourse.getCourseTitle());

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
                miniCard.setSummaryTitleLabel(currentText);

            }
        });

        current.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseTitle(newValue);
                miniCard.setSummaryTitleLabel(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseTitle(oldValue);
                miniCard.setSummaryTitleLabel(oldValue);

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
        miniCard.setCourseGradeLabel(currentCourse.getLetterGrade());

        gradeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setLetterGrade(newValue);
                miniCard.setCourseGradeLabel(newValue);

            } catch (NullPointerException e){
                currentCourse.setLetterGrade(oldValue);
                miniCard.setCourseGradeLabel(oldValue);
            }
        });

        if(currentCourse.getType() != Course.CourseType.PRE && currentCourse.getType() != Course.CourseType.TRACK) {
            TextField transferField = new TextField();
            transferField.setPromptText("Transfer");
            transferField.setText(currentCourse.getTransfer());
            miniCard.setSummaryTranferWaiveLabel(currentCourse.getTransfer());


            current.getChildren().addAll(transferField);
            //current.getChildren().addAll(transferField, removeButton);

            transferField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    currentCourse.setTransfer(newValue);
                    miniCard.setSummaryTranferWaiveLabel(newValue);
                } catch (NullPointerException e){
                    currentCourse.setTransfer(oldValue);
                    miniCard.setSummaryTranferWaiveLabel(oldValue);
                }
            });

        } else {
            CheckBox waiverCheckBox = new CheckBox();
            waiverCheckBox.setText("Waiver");
            waiverCheckBox.setSelected(currentCourse.isWaived());
            if(waiverCheckBox.isSelected()) {
                miniCard.setSummaryTranferWaiveLabel(" ");
                miniCard.getSummaryTranferWaiveLabel().setStyle("-fx-font-weight: bold");
            }

            waiverCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                currentCourse.setWaived(waiverCheckBox.isSelected());
                if(waiverCheckBox.isSelected()) {
                    miniCard.setSummaryTranferWaiveLabel("Waived");
                    miniCard.getSummaryTranferWaiveLabel().setStyle("-fx-font-weight: bold");
                } else
                    miniCard.setSummaryTranferWaiveLabel(" ");

            });

            current.getChildren().addAll(waiverCheckBox);

        }

        gradeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setTransfer(newValue);
            } catch (NullPointerException e){
                currentCourse.setTransfer(oldValue);
            }
        });

        return current;
    }

    private void createCard(){

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
        card.getChildren().add(vAlign);

    }

    public Pane getCard() {
        return stackContainer;
    }

    public Double getCardWidth() {
        return stackContainer.getLayoutBounds().getWidth();
    }
}
