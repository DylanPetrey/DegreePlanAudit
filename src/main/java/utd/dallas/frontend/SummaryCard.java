package utd.dallas.frontend;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import utd.dallas.backend.Course;


public class SummaryCard {
    private final StackPane summaryCard = new StackPane();
    private final Text numText = new Text();
    private final Label titleLabel = new Label();
    private final Text semesterText = new Text("");
    private final Text gradeText = new Text("");
    private final Text transferText = new Text("");
    private final Text hoursText = new Text("");

    private final Text waiveText = new Text("");

    private static final int MAX_TITLE_LENGTH = 42;
    private Pane deleteButton = new Pane();
    private final BooleanProperty isOptionalType = new SimpleBooleanProperty(false);
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

        TextFlow titleFlow = createTitleFlow(numText);
        TextFlow hourFlow = createTextFlow("Credit Hours: ", hoursText);
        TextFlow semesterFlow = createTextFlow("Semester: ", semesterText);
        TextFlow gradeFlow = createTextFlow("Grade: ", gradeText);
        TextFlow transferFlow = createTextFlow("Transfer: ", transferText);
        TextFlow waiveFlow = createTextFlow("", waiveText);

        VBox cardText = new VBox();
        cardText.getChildren().addAll(titleFlow, titleLabel);
        cardText = createSummaryCard(hourFlow, semesterFlow, gradeFlow, transferFlow);
        cardText.setAlignment(Pos.TOP_LEFT);
        summaryCard.getChildren().addAll(cardText);

        isEmptyCourse =
                numText.textProperty().isNotEmpty().or(
                titleLabel.textProperty().isNotEmpty().or(
                hoursText.textProperty().isNotEmpty().or(
                semesterText.textProperty().isNotEmpty().or(
                gradeText.textProperty().isNotEmpty().or(
                transferText.textProperty().isNotEmpty().or(
                waiveText.textProperty().isNotEmpty()))))));

        deleteButton = createDeleteButton();
        deleteButton.visibleProperty().bind(summaryCard.hoverProperty().and(isEmptyCourse).and(isOptionalType.not()));

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

    private VBox createSummaryCard(TextFlow hourFlow, TextFlow semester, TextFlow grade, TextFlow transfer){
        VBox currentSummary = new VBox();

        GridPane variables = new GridPane();
        variables.add(hourFlow, 0,0);
        variables.add(transfer, 1,1);
        variables.add(semester, 0,1);
        variables.add(grade, 1,0);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100d / variables.getColumnCount());
        for (int i = 0; i < variables.getColumnCount(); i++) {
            variables.getColumnConstraints().add(cc);
        }

        currentSummary.getChildren().addAll(createTitleFlow(numText), titleLabel, variables);
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

    private TextFlow createTitleFlow(Text v1){
        TextFlow current_flow = new TextFlow();

        v1.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        waiveText.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        current_flow.getChildren().addAll(v1, waiveText);
        current_flow.setVisible(false);

        current_flow.visibleProperty().bind(v1.textProperty().isNotEqualTo(""));

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

    public Text getGradeText() {
        return gradeText;
    }

    public Text getSemesterText() {
        return semesterText;
    }

    public Pane getDeleteButton(){
        return deleteButton;
    }
    public void setNumText(String newNum) {
        numText.setStyle("-fx-font-weight: bold; -fx-font-size: 15");
        numText.setText(newNum);
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
    public void setHoursText(String newHour) { hoursText.setText(newHour); }
    public void setTransferText(String newTransfer) {
        transferText.setText(newTransfer);
    }

    public void setWaiveText(boolean isWaived){
        if(isWaived)
            waiveText.setText(" - Waived");
        else
            waiveText.setText("");
    }

    public void setCourseType(Course.CourseType type){
        isOptionalType.setValue(type == Course.CourseType.OPTIONAL);
    }

    public BooleanBinding isEmptyCourse() {
        return isEmptyCourse;
    }
}
