package utd.dallas.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class IntroController {
    @FXML
    private Button createButton;

    @FXML
    private Button uploadButton;

    @FXML
    protected void onCreateButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("CreateScreen.fxml"));
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 600));
    }

    @FXML
    protected void onUploadButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("UploadScreen.fxml"));
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 600));
    }
}
