package utd.dallas.frontend;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;


public class SummaryCard {
    private final StackPane summaryCard = new StackPane();
    private final Label courseNumLabel = new Label();
    private final Label titleLabel = new Label();
    private final Text semesterText = new Text("");
    private final Text gradeText = new Text("");
    private final Text transferText = new Text("");
    private final Text waiveText = new Text("");

    private static final int MAX_TITLE_LENGTH = 42;
    private Pane deleteButton = new Pane();
    BooleanBinding isEmptyCourse;

    SummaryCard(){
        // Initialize max width
        Text t = new Text("Design and Analysis of Computer Algorithms");
        t.setFont(Font.getDefault());
        double maxTextWidth = t.getLayoutBounds().getWidth() + 20;

        titleLabel.setMinWidth(maxTextWidth);
        titleLabel.setPrefWidth(maxTextWidth);
        titleLabel.setMaxWidth(maxTextWidth);
        titleLabel.setWrapText(true);

        TextFlow semesterFlow = createTextFlow("Semester: ", semesterText);
        TextFlow gradeFlow = createTextFlow("Grade: ", gradeText);
        TextFlow transferFlow = createTextFlow("Transfer: ", transferText);
        TextFlow waiveFlow = createTextFlow("", waiveText);

        VBox cardText = new VBox();
        cardText.getChildren().addAll(courseNumLabel, titleLabel);
        cardText = createSummaryCard(semesterFlow, gradeFlow, transferFlow, waiveFlow);
        cardText.setAlignment(Pos.TOP_LEFT);
        summaryCard.getChildren().addAll(cardText);


        isEmptyCourse =
                courseNumLabel.textProperty().isNotEmpty().or(
                titleLabel.textProperty().isNotEmpty().or(
                semesterText.textProperty().isNotEmpty().or(
                gradeText.textProperty().isNotEmpty().or(
                transferText.textProperty().isNotEmpty().or(
                waiveText.textProperty().isNotEmpty())))));

        deleteButton = createDeleteButton();
        deleteButton.visibleProperty().bind(summaryCard.hoverProperty().and(isEmptyCourse));

        summaryCard.getChildren().add(deleteButton);
        StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);
        deleteButton.setTranslateX(-10);
        deleteButton.setTranslateY(0);

        Label addCourseLabel = new Label("Add Course");
        addCourseLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        addCourseLabel.visibleProperty().bind(isEmptyCourse.not());

        summaryCard.getChildren().add(addCourseLabel);
    }
    public StackPane getSummaryCard() {
        return summaryCard;
    }

    private VBox createSummaryCard(TextFlow semester, TextFlow grade, TextFlow transfer, TextFlow waiveFlow){
        VBox currentSummary = new VBox();

        GridPane variables = new GridPane();
        variables.add(semester, 0,0);
        variables.add(grade, 1,0);
        variables.add(transfer, 0,1);
        variables.add(waiveFlow, 0,1);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100d / variables.getColumnCount());
        for (int i = 0; i < variables.getColumnCount(); i++) {
            variables.getColumnConstraints().add(cc);
        }

        currentSummary.getChildren().addAll(courseNumLabel, titleLabel, variables);
        currentSummary.setSpacing(3);
        return currentSummary;
    }


    private TextFlow createTextFlow(String initialText, Text value){
        TextFlow current_flow = new TextFlow();
        Text text = new Text(initialText);

        text.setStyle("-fx-font-size: 12");
        value.setStyle("-fx-font-weight: bold; -fx-font-size: 12");
        current_flow.getChildren().addAll(text, value);
        current_flow.setVisible(false);

        current_flow.visibleProperty().bind(value.textProperty().isNotEqualTo(""));
        return current_flow;
    }

    private Pane createDeleteButton(){
        Line leftDiag = new Line(-8,8,8,-8);
        Line rightDiag = new Line(-8,-8,8,8);

        leftDiag.setStrokeWidth(1.5);
        rightDiag.setStrokeWidth(1.5);

        StackPane cross = new StackPane(leftDiag, rightDiag);
        Pane container = new Pane(cross);

        double maxWidth = Math.abs(leftDiag.getEndX()-leftDiag.getStartX());

        container.setMaxWidth(maxWidth);
        container.setMaxHeight(maxWidth);

        cross.setPadding(new Insets(5));
        container.setPadding(new Insets(5));

        cross.setStyle("-fx-cursor: hand");

        cross.setOnMouseEntered(event -> {
            if(cross.isVisible()) {
                cross.setStyle("-fx-cursor: hand; -fx-background-color: gray; -fx-background-radius: " + String.valueOf(maxWidth/2-2) + "");
            }
        });
        cross.setOnMouseExited(event -> {
            if(cross.isVisible())
                cross.setStyle("-fx-cursor: hand");
        });
        return container;
    }

    public Pane getDeleteButton(){
        return deleteButton;
    }
    public void setNumText(String newNum) {
        courseNumLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        courseNumLabel.setText(newNum);
    }
    public void setTitleText(String newTitle) {
        titleLabel.setStyle("-fx-font-size: 13.8");
        titleLabel.setText(newTitle);
    }
    public void setSemesterText(String newSemester) {
        semesterText.setText(newSemester);
    }
    public void setGradeText(String newGrade) {
        gradeText.setText(newGrade);
    }
    public void setTransferText(String newTransfer) {
        transferText.setText(newTransfer);
    }
    public void setWaiveText(boolean isWaived){
        if(isWaived)
            waiveText.setText("Waived");
        else
            waiveText.setText("");
    }

}
