package utd.dallas.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;


public class SummaryCard {
    private final Pane summaryCard = new Pane();
    private final Label courseNumLabel = new Label();
    private final Label titleLabel = new Label();
    private final Text semesterText = new Text("");
    private final Text gradeText = new Text("");
    private final Text transferText = new Text("");
    private final Text waiveText = new Text("");
    private static final int MAX_TITLE_LENGTH = 42;
    private final double maxTextWidth;
    SummaryCard(){
        // Initialize max width
        Text t = new Text("Design and Analysis of Computer Algorithms");
        t.setFont(Font.getDefault());
        maxTextWidth = t.getLayoutBounds().getWidth() + 20;

        titleLabel.setMaxWidth(maxTextWidth);
        titleLabel.setPrefWidth(maxTextWidth);
        titleLabel.setMinWidth(maxTextWidth);
        titleLabel.setWrapText(true);

        TextFlow semesterFlow = createTextFlow("Semester: ", semesterText, TextAlignment.LEFT);
        TextFlow gradeFlow = createTextFlow("Grade: ", gradeText, TextAlignment.LEFT);
        TextFlow transferFlow = createTextFlow("Transfer: ", transferText, TextAlignment.LEFT);
        TextFlow waiveFlow = createTextFlow("", waiveText, TextAlignment.LEFT);

        VBox summaryContainer = createSummaryCard(semesterFlow, gradeFlow, transferFlow, waiveFlow);
        summaryContainer.setAlignment(Pos.CENTER_LEFT);
        summaryContainer.setFillWidth(true);
        HBox alignH = new HBox(summaryContainer);
        alignH.setFillHeight(true);
        alignH.setAlignment(Pos.CENTER);
        summaryCard.getChildren().add(alignH);
    }
    public Pane getSummaryCard() {
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
        currentSummary.setPadding(new Insets(10,0,0,10));

        return currentSummary;
    }


    private TextFlow createTextFlow(String initialText, Text value, TextAlignment alignment){
        TextFlow current_flow = new TextFlow();
        Text text = new Text(initialText);

        text.setStyle("-fx-font-size: 12");
        value.setStyle("-fx-font-weight: bold; -fx-font-size: 12");
        current_flow.getChildren().addAll(text, value);

        current_flow.setTextAlignment(alignment);
        current_flow.setVisible(false);

        value.textProperty().addListener((observable, oldValue, newValue) -> {
            current_flow.setVisible(!value.getText().equals(""));
        });

        return current_flow;
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
