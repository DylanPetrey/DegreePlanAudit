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
import java.util.List;

public class FlowObject {
    FlowPane flowPane;
    ObservableList<Node> flowOrder;
    ObservableList<CourseCard> observableCard;
    Course.CourseType type;
    StackPane addCard;


    FlowObject(Course.CourseType type){
        this.type = type;
        flowPane = new FlowPane();
        flowPane.setHgap(0);
        flowPane.setAlignment(Pos.CENTER);

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
        flowOrder = FXCollections.observableArrayList();
        observableCard.forEach(CourseCard ->{
            flowOrder.add(CourseCard.getCard());
        });

        if(addCard != null){
            flowOrder.add(addCard);
        }

        flowPane.getChildren().addAll(flowOrder);
    }

    public boolean hasAddCourse(){
        return observableCard.get(observableCard.size()-1).getCurrentCourse().isEmpty();
    }
    public CourseCard addCard(StudentCourse currentCourse){
        currentCourse.setType(type);
        CourseCard currentCard = new CourseCard(currentCourse);
        observableCard.add(currentCard);
        return currentCard;
    }

    public List<CourseCard> removeEmpty(){
        List<CourseCard> removeList = new ArrayList<>();
        for(int i = 0; i < observableCard.size()-1; i++)
            if(observableCard.get(i).getCurrentCourse().isEmpty())
                    removeList.add(observableCard.get(i));

        return removeList;
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
}


