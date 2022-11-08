package utd.dallas.frontend;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import utd.dallas.backend.Course;
import utd.dallas.backend.StudentCourse;

import java.util.ArrayList;
import java.util.List;

public class GridObject extends CreateController{
    public GridPane grid;
    private List<StudentCourse> gridCourse;
    private Course.CourseType type;
    private List<ColumnConstraints> col = new ArrayList<>();
    private List<RowConstraints> row = new ArrayList<>();

    GridObject(Course.CourseType type){
        grid = new GridPane();
        this.type = type;
        gridCourse = new ArrayList<>();

        addConstraints();

        // Create grid labels
        grid.add(new Label("Course Title"), 0, 0);
        grid.add(new Label("Course Number"), 1, 0);
        grid.add(new Label("Semester"), 2, 0);
        grid.add(new Label("Transfer"), 3, 0);
        grid.add(new Label("Grade"), 4, 0);
    }

    private void addConstraints() {
        double[] widths = {257.0, 88.0, 61.0, 51.0, 43.0};
        for (int i = 0; i < widths.length; i++) {
            ColumnConstraints column = new ColumnConstraints(widths[i]);
            column.setHalignment(HPos.valueOf("CENTER"));
            column.setHgrow(Priority.valueOf("SOMETIMES"));
            column.setMinWidth(widths[i]);
            column.setMaxWidth(widths[i]);
            grid.getColumnConstraints().add(column);
        }
    }


    public void addRow(StudentCourse currentCourse){
        TextField title = new TextField(currentCourse.getCourseTitle());
        TextField number = new TextField(currentCourse.getCourseNumber());
        TextField semester = new TextField(currentCourse.getSemester());
        TextField transfer = new TextField(currentCourse.getTransfer());
        TextField grade = new TextField(currentCourse.getLetterGrade());

        int numRows = grid.getRowCount();

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseTitle(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseTitle(oldValue);
            }
        });

        number.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setCourseTitle(newValue);
            } catch (NullPointerException e){
                currentCourse.setCourseTitle(oldValue);
            }
        });

        semester.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setSemester(newValue);
            } catch (NullPointerException e){
                currentCourse.setSemester(oldValue);
            }
        });

        transfer.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setTransfer(newValue);
            } catch (NullPointerException e){
                currentCourse.setTransfer(oldValue);
            }
        });

        grade.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                currentCourse.setLetterGrade(newValue);
            } catch (NullPointerException e){
                currentCourse.setLetterGrade(oldValue);
            }
        });

        grid.add(title, 0, numRows);
        grid.add(number, 1, numRows);
        grid.add(semester, 2, numRows);
        grid.add(transfer, 3, numRows);
        grid.add(grade, 4, numRows);
    }

    public void removeRow(){
        int numRows = grid.getRowCount();
        grid.getChildren().removeIf(
                node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) != 0 && GridPane.getRowIndex(node) == numRows-1);
    }

    public GridPane getGrid() {
        return grid;
    }
}
