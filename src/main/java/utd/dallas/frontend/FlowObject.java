package utd.dallas.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import utd.dallas.backend.Course;
import utd.dallas.backend.StudentCourse;
import java.util.ArrayList;

public class FlowObject {
    VBox parent;
    FlowPane flowPane;
    ObservableList<CourseCard> observableCard;
    Course.CourseType type;

    FlowObject(Course.CourseType type, VBox parent){
        this.type = type;
        this.parent = parent;
        flowPane = new FlowPane();
        flowPane.setHgap(0);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setPrefHeight(100);


        observableCard = FXCollections.observableArrayList(new ArrayList<CourseCard>());

        observableCard.addListener(new ListChangeListener<CourseCard>() {
            @Override
            public void onChanged(Change<? extends CourseCard> change) {
                updateFlowPane();
            }
        });
    }

    public void updateFlowPane(){
        flowPane.getChildren().clear();
        ObservableList<Node> flowOrder = FXCollections.observableArrayList();

        observableCard.forEach(CourseCard ->{
            if(!flowOrder.contains(CourseCard.getCard()))
                flowOrder.add(CourseCard.getCard());
        });

        flowPane.getChildren().addAll(flowOrder);

        if(getObservableCard().size() == 0) {
            flowPane.setMinHeight(150);
            flowPane.setPrefHeight(150);
        }else {
            flowPane.setMinHeight(Region.USE_COMPUTED_SIZE);
            flowPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        }
    }

    public CourseCard addCard(StudentCourse currentCourse){
        currentCourse.setType(type);
        CourseCard currentCard = new CourseCard(currentCourse);
        currentCard.setParent(this);
        currentCard.summaryCard.setCourseType(currentCourse.getType());
        observableCard.add(currentCard);
        return currentCard;
    }

    public void addCard(CourseCard newCard){
        newCard.getParent().removeCard(newCard);
        newCard.setParent(this);
        newCard.getCurrentCourse().setType(type);
        newCard.summaryCard.setCourseType(type);

        if(type == Course.CourseType.OPTIONAL || observableCard.size() < 1)
            observableCard.add(observableCard.size(), newCard);
        else
            observableCard.add(observableCard.size()-1, newCard);
    }

    public void removeCard(CourseCard target){
        observableCard.remove(target);
    }

    public FlowPane getFlowPane() {
        return flowPane;
    }
    public ObservableList<CourseCard> getObservableCard() {
        return observableCard;
    }

    public Course.CourseType getType() {
        return type;
    }

    public VBox getParent(){
        return parent;
    }
}


