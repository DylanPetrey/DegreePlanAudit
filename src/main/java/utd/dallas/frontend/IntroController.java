package utd.dallas.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class IntroController {
    @FXML private AnchorPane basePane;
    @FXML private Button createButton;
    @FXML private Button uploadButton;


    /**
     * Button functionality to create a blank audit form
     */
    @FXML
    protected void onCreateButtonClick() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CreateScreen.fxml")));
        Stage stage = (Stage) createButton.getScene().getWindow();
        Scene currentStage = basePane.getScene();
        stage.setScene(new Scene(root, currentStage.getWidth(), currentStage.getHeight()));
    }

    /**
     * Button functionality to upload a transcript
     */
    @FXML
    protected void onUploadButtonClick() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("UploadScreen.fxml")));
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        Scene currentStage = basePane.getScene();
        stage.setScene(new Scene(root, currentStage.getWidth(), currentStage.getHeight()));
    }
}
