package utd.dallas.frontend;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class DragDropHandler {
    List<Timeline> activeTimelines = new ArrayList<>();
    private static DataFormat cardFormat = new DataFormat("CourseCard");;
    private double scrollVelocity = 0;
    private CourseCard draggingCard;

    DragDropHandler(){
        if (cardFormat == null)
            cardFormat = new DataFormat("CourseCard");
    }

    public void createDragListener(CourseCard source) {
        StackPane sourcePane = source.summary;
        sourcePane.setOnDragDetected(e -> {
            if(!source.summaryCard.isEmptyCourse().get() || !source.summary.isVisible()){
                e.consume();
                return;
            }

            Dragboard db = sourcePane.startDragAndDrop(TransferMode.MOVE);

            // Create draggable image
            db.setDragView(sourcePane.snapshot(new SnapshotParameters(), null));
            db.setDragViewOffsetX(e.getSceneX() - sourcePane.localToScene(sourcePane.getBoundsInLocal()).getMinX());
            db.setDragViewOffsetY(e.getSceneY() - sourcePane.localToScene(sourcePane.getBoundsInLocal()).getMinY());

            // Add to clipboard
            ClipboardContent cc = new ClipboardContent();
            cc.put(cardFormat, "sourceCard");
            db.setContent(cc);

            draggingCard = source;
        });

        sourcePane.setOnDragDone(e -> {
            draggingCard = null;
            activeTimelines.forEach(Timeline::stop);
        });
    }

    public void createDropListener(FlowObject targetFlow){
        VBox target = targetFlow.getParent();
        target.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(cardFormat)
                    && draggingCard != null
                    && draggingCard.getCard().getParent().getParent() != target) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            target.setStyle("-fx-border-color: black; -fx-border-radius: 5");
        });

        target.setOnDragExited(e -> target.setStyle(""));

        target.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(cardFormat)) {
                targetFlow.removeCard(draggingCard);
                targetFlow.addCard(draggingCard);
                e.setDropCompleted(true);
            }
        });
    }


    public void setupScrolling(ScrollPane currentPane) {
        Timeline scrollTimeline = new Timeline();
        activeTimelines.add(scrollTimeline);
        int speed = 100;

        scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        scrollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), (ActionEvent) -> { dragScroll(currentPane);}));

        currentPane.getParent().setOnDragExited((DragEvent e) -> {
            boolean xOffset = e.getSceneX() > currentPane.localToScene(currentPane.getBoundsInLocal()).getMinX()
                    && e.getSceneX() < currentPane.localToScene(currentPane.getBoundsInLocal()).getMaxX();

            if(!xOffset) {
                scrollTimeline.stop();
                return;
            }

            double topDelta = Math.abs(currentPane.localToScreen(currentPane.getBoundsInLocal()).getMinY() - e.getScreenY());
            double botDelta = Math.abs(e.getScreenY() - currentPane.localToScreen(currentPane.getBoundsInLocal()).getMaxY());

            if (topDelta < botDelta) {
                scrollVelocity = -1.0 / speed;
            }
            else if (botDelta < topDelta) {
                scrollVelocity = 1.0 / speed;
            }

            Dragboard db = e.getDragboard();
            if (db.hasContent(cardFormat)) {
                scrollTimeline.play();
            }
        });

        currentPane.getParent().setOnDragEntered((DragEvent e) -> {
            scrollTimeline.stop();
        });

        currentPane.getParent().setOnDragDone((DragEvent e) -> {
            scrollTimeline.stop();
        });
    }
    public void dragScroll(ScrollPane currentPane) {
        ScrollBar sb = getVerticalScrollbar(currentPane);
        if (sb != null) {
            if(scrollVelocity > 0)
                sb.increment();
            else sb.decrement();
        }
    }

    public ScrollBar getVerticalScrollbar(ScrollPane currentPane) {
        ScrollBar result = null;
        for (Node n : currentPane.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }
}
