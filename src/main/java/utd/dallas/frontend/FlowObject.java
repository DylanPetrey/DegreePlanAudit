package utd.dallas.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import utd.dallas.backend.Course;
import utd.dallas.backend.StudentCourse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FlowObject {
    FlowPane flowPane;
    ObservableList<CourseCard> observableCard;


    FlowObject(Course.CourseType type){
        flowPane = newFlowPane();
        flowPane.setHgap(0);

        observableCard = FXCollections.observableArrayList(new ArrayList<CourseCard>());

        observableCard.addListener(new ListChangeListener<CourseCard>() {
            @Override
            public void onChanged(Change<? extends CourseCard> change) {
                while (change.next()) {
                    for (CourseCard removedCard : change.getRemoved()) {
                        flowPane.getChildren().remove(removedCard.getCard());
                    }
                    for (CourseCard addedCard : change.getAddedSubList()) {
                        flowPane.getChildren().add(addedCard.getCard());
                    }
                }
            }
        });
    }

    public void addCard(StudentCourse currentCourse){
        CourseCard currentCard = new CourseCard(currentCourse);
        observableCard.add(currentCard);
    }
    public FlowPane getFlowPane() {
        return flowPane;
    }
    public ObservableList<CourseCard> getObservableCard() {
        return observableCard;
    }

    private FlowPane newFlowPane(){
        return new FlowPane() {
            {
                setAlignment(Pos.TOP_CENTER);
                setOrientation(Orientation.HORIZONTAL);
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();

                final LinkedHashMap<Double, List<Node>> rows = new LinkedHashMap<>();
                getChildren().forEach(node -> {
                    final double layoutY = node.getLayoutY();
                    List<Node> row = rows.get(node.getLayoutY());
                    if (row == null) {
                        row = new ArrayList<>();
                        rows.put(layoutY, row);
                    }

                    row.add(node);
                });

                try {
                    final Object[] keys = rows.keySet().toArray();
                    final List<Node> firstRow = rows.get(keys[0]);
                    final List<Node> lastRow = rows.get(keys[keys.length - 1]);
                    for (int i = 0; i < lastRow.size(); i++) {
                        lastRow.get(i).setLayoutX(firstRow.get(i).getLayoutX());
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    return;
                }
            }
        };
    }
}


