package utd.dallas.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utd.dallas.backend.Student;
import utd.dallas.backend.TranscriptParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UploadController {
    @FXML private Button backButton;
    @FXML private Button continueButton;
    @FXML private Group fileWindow;
    @FXML private Label continueErrorLabel;
    @FXML private Label filenameLabel;
    @FXML private Rectangle uploadFileBox;
    @FXML private Group uploadFileButton;
    @FXML private Rectangle fileButtonBackground;

    File inputFile;
    TranscriptParser parser;


    @FXML
    protected void initialize() {
        // Extensions that are valid to be drag-n-dropped
        List<String> validExtensions = Arrays.asList("pdf");
        fileWindow.setOnDragOver(event -> {
            // On drag over if the DragBoard has files
            if (event.getGestureSource() != fileWindow && event.getDragboard().hasFiles()) {
                // All files on the dragboard must have an accepted extension
                if (!validExtensions.containsAll(
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
                    continueErrorLabel.setVisible(false);
                    uploadFileBox.setFill(Paint.valueOf("#f5f5f5"));
                    inputFile = event.getDragboard().getFiles().get(0);
                    filenameLabel.setText(inputFile.getName());
                    parser = new TranscriptParser(inputFile);
                } catch (Exception e) {
                    parser = null;
                    e.printStackTrace();
                    uploadFileBox.setFill(Paint.valueOf("#ff8080"));
                    filenameLabel.setText("Not a valid transcript");
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });


        uploadFileButton.setOnMouseEntered(event -> fileButtonBackground.setFill(Paint.valueOf("#c6c6c6")));
        uploadFileButton.setOnMouseExited(event -> fileButtonBackground.setFill(Paint.valueOf("#ffffff")));
    }


    
    @FXML
    protected void onFileButtonClick() {
        try {
            continueErrorLabel.setVisible(false);
            uploadFileBox.setFill(Paint.valueOf("#f5f5f5"));
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDFs", "*.pdf"));
            fileChooser.setInitialDirectory(Mediator.getInstance().getDefaultDirectory());
            Stage stage = (Stage) uploadFileButton.getScene().getWindow();
            inputFile = fileChooser.showOpenDialog(stage);
            filenameLabel.setText(inputFile.getName());
            parser = new TranscriptParser(inputFile);
            Mediator.getInstance().setDefaultDirectory(inputFile.getParent());
        } catch (Exception e) {
            parser = null;
            e.printStackTrace();
            uploadFileBox.setFill(Paint.valueOf("#ff8080"));
            filenameLabel.setText("Not a valid transcript");
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
            continueErrorLabel.setVisible(true);
            return;
        }

        Mediator.getInstance().setStudent(parser.getStudent());

        Parent root = FXMLLoader.load(getClass().getResource("CreateScreen.fxml"));
        Stage stage = (Stage) continueButton.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 600));
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("IntroScreen.fxml"));
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 600));
    }


}

