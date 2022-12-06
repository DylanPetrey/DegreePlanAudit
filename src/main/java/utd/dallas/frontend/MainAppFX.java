package utd.dallas.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class MainAppFX extends Application {

    /**
     * Creates the stages and with initial objects
     */
    public void start(Stage stage) throws IOException {
        Mediator.getInstance();

        Text t = new Text("Design and Analysis of Computer Algorithms");
        t.setFont((Font.getDefault()));
        double minWidth = t.getLayoutBounds().getWidth() + 20;
        minWidth *= 2.5;

        FXMLLoader fxmlLoader = new FXMLLoader(MainAppFX.class.getResource("IntroScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), minWidth, 700);
        stage.setTitle("Select Track");
        stage.setScene(scene);


        stage.setMinWidth(minWidth);

        stage.setMinHeight(600);
        stage.show();
    }

    /**
     * Launches the program
     */
    public static void main(String[] args) {
        launch();
    }

}