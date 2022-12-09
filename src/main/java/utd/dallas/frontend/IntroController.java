package utd.dallas.frontend;

import javafx.application.Platform;
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

    @FXML
    protected void initialize() {
        createButton.setOnMouseEntered(event -> createButton.setStyle("-fx-background-color: #c6c6c6"));
        createButton.setOnMouseExited(event -> createButton.setStyle("-fx-background-color: #ffffff"));
        uploadButton.setOnMouseEntered(event -> uploadButton.setStyle("-fx-background-color: #c6c6c6"));
        uploadButton.setOnMouseExited(event -> uploadButton.setStyle("-fx-background-color: #ffffff"));

        Platform.runLater(() -> {
            double maxWidth = Math.max(createButton.getLayoutBounds().getWidth() + 10, uploadButton.getLayoutBounds().getWidth() + 10);
            double maxHeight = Math.max(createButton.getLayoutBounds().getHeight() + 10, uploadButton.getLayoutBounds().getHeight() + 10);
            createButton.setPrefWidth(maxWidth);
            createButton.setPrefHeight(maxHeight);
            uploadButton.setPrefWidth(maxWidth);
            uploadButton.setPrefHeight(maxHeight);
        });

    }

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
