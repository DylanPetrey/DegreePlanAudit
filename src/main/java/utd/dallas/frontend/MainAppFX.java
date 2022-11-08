package utd.dallas.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utd.dallas.backend.Student;

import java.io.IOException;

public class MainAppFX extends Application {
    private Stage primaryStage;

    public void start(Stage stage) throws IOException {
        Mediator.getInstance().setStage(primaryStage);

        FXMLLoader fxmlLoader = new FXMLLoader(MainAppFX.class.getResource("IntroScreen.fxml"));
        this.primaryStage = stage;
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        this.primaryStage.setTitle("Select Track");
        this.primaryStage.setScene(scene);
        this.primaryStage.setMinWidth(600);
        this.primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}