package utd.dallas.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class UploadController {
    @FXML
    private Button okButton;

    @FXML
    private Button backButton;

    @FXML
    protected void onBackButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("IntroScreen.fxml"));
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 600));
    }

    @FXML
    protected void onOkButtonClick() throws IOException {
        System.out.println("Ok button clicked");
    }
}
