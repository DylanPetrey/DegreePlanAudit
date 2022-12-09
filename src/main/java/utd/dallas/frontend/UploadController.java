package utd.dallas.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utd.dallas.backend.TranscriptParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UploadController {
    @FXML private AnchorPane basePane;
    @FXML private Button backButton;
    @FXML private Button continueButton;
    @FXML private Group fileWindow;
    @FXML private Rectangle uploadFileBox;
    @FXML private Label uploadButtonLabel;

    @FXML private Text filenameText;

    File inputFile;
    TranscriptParser parser;


    @FXML
    protected void initialize() {
        Platform.runLater(() -> {
            double maxWidth = continueButton.getLayoutBounds().getWidth() + 10;
            double maxHeight = continueButton.getLayoutBounds().getHeight() + 10;
            continueButton.setPrefWidth(maxWidth);
            continueButton.setPrefHeight(maxHeight);
            backButton.setPrefWidth(maxWidth);
            backButton.setPrefHeight(maxHeight);
        });

        initalizeDragDrop();

        // Mouse Over colors
        uploadButtonLabel.setOnMouseEntered(event -> uploadButtonLabel.setStyle("-fx-background-color: #c6c6c6; -fx-text-fill: black; -fx-background-radius: 5; -fx-border-color: black; -fx-border-radius: 5"));
        uploadButtonLabel.setOnMouseExited(event -> uploadButtonLabel.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black; -fx-background-radius: 5; -fx-border-color: black; -fx-border-radius: 5"));
        continueButton.setOnMouseEntered(event -> continueButton.setStyle("-fx-background-color: #c6c6c6"));
        continueButton.setOnMouseExited(event -> continueButton.setStyle("-fx-background-color: #ffffff"));
        backButton.setOnMouseEntered(event -> backButton.setStyle("-fx-background-color: #c6c6c6"));
        backButton.setOnMouseExited(event -> backButton.setStyle("-fx-background-color: #ffffff"));
    }

    private void initalizeDragDrop(){
        // Extensions that are valid to be drag-n-dropped
        List<String> validExtensions = Arrays.asList("pdf");

        uploadButtonLabel.heightProperty().addListener((observable, oldValue, newValue) -> {
            try {
                uploadButtonLabel.setAlignment(Pos.CENTER);
                double diff = uploadFileBox.getY()+(uploadFileBox.getHeight() - newValue.doubleValue()) / 2;
                uploadButtonLabel.setLayoutY(diff);
                filenameText.setLayoutX(uploadButtonLabel.getWidth()+5);
                filenameText.setLayoutY(uploadButtonLabel.getLayoutY());
                filenameText.setWrappingWidth(uploadFileBox.getWidth()-uploadButtonLabel.getWidth()-10);

            } catch (Exception e){
                uploadButtonLabel.setLayoutY(oldValue.doubleValue());
            }
        });

        filenameText.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                double boxHeight = uploadFileBox.getLayoutBounds().getHeight()/2;
                filenameText.setLayoutY(boxHeight);
            }catch (Exception ignored){
            }
        });

        fileWindow.setOnDragOver(event -> {
            // On drag over if the DragBoard has files
            if (event.getGestureSource() != fileWindow && event.getDragboard().hasFiles()) {
                // All files on the dragboard must have an accepted extension
                if (!new HashSet<>(validExtensions).containsAll(
                        event.getDragboard().getFiles().stream()
                                .map(file -> getExtension(file.getName()))
                                .collect(Collectors.toList()))) {

                    event.consume();
                    return;
                }

                // Allow for both copying and moving
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        fileWindow.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getGestureSource() != fileWindow && event.getDragboard().hasFiles()) {
                try {
                    uploadFileBox.setFill(Paint.valueOf("#f5f5f5"));
                    inputFile = event.getDragboard().getFiles().get(0);
                    filenameText.setText(inputFile.getName());
                    parser = new TranscriptParser(inputFile);
                    Mediator.getInstance().setDefaultDirectory(inputFile.getParent());
                } catch (Exception e) {
                    parser = null;
                    uploadFileBox.setFill(Paint.valueOf("#ff8080"));
                    filenameText.setText("Not a valid transcript");
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }


    
    @FXML
    protected void onFileButtonClick() {
        try {
            uploadFileBox.setFill(Paint.valueOf("#f5f5f5"));
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDFs", "*.pdf"));
            fileChooser.setInitialDirectory(Mediator.getInstance().getDefaultDirectory());
            Stage stage = (Stage) uploadButtonLabel.getScene().getWindow();
            inputFile = fileChooser.showOpenDialog(stage);
            filenameText.setText(inputFile.getName());
            parser = new TranscriptParser(inputFile);
            Mediator.getInstance().setDefaultDirectory(inputFile.getParent());
        } catch (Exception e) {
            parser = null;
            uploadFileBox.setFill(Paint.valueOf("#ff8080"));
            filenameText.setText("Not a valid transcript");
        }
    }



    // Method to get extension of a file
    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) //if the name is not empty
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    @FXML
    protected void onOkButtonClick() throws Exception {
        if(parser == null) {
            uploadFileBox.setFill(Paint.valueOf("#ff8080"));
            filenameText.setText("Upload a valid transcript");
            return;
        }

        Mediator.getInstance().setStudent(parser.getStudent());

        Parent root = FXMLLoader.load(getClass().getResource("CreateScreen.fxml"));
        Stage stage = (Stage) continueButton.getScene().getWindow();
        Scene currentStage = basePane.getScene();
        stage.setScene(new Scene(root, currentStage.getWidth(), currentStage.getHeight()));
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("IntroScreen.fxml"));
        Stage stage = (Stage) backButton.getScene().getWindow();
        Scene currentStage = basePane.getScene();
        stage.setScene(new Scene(root, currentStage.getWidth(), currentStage.getHeight()));
    }


}

