package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class HelloController {
    @FXML
    private ChoiceBox<String> trackBox;
    @FXML
    private Button printButton;
    @FXML
    private TextField studentName;
    @FXML
    private TextField studentID;
    @FXML
    private TextField studentSemAdmitted;
    @FXML
    private CheckBox fastTrack;
    @FXML
    private CheckBox thesis;
    @FXML
    private TextField anticipatedGrad;

    @FXML
    protected void initialize() {

    }

    @FXML
    protected void onSWEButtonClick() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Software Engineering");

        trackBox.getItems().clear();
        trackBox.getItems().addAll(list);
    }

    @FXML
    protected void onCSButtonClick() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Traditional Computer Science");
        list.add("Network Telecommunications");
        list.add("Intelligent Systems");
        list.add("Interactive Computing");
        list.add("Systems");
        list.add("Data Science");
        list.add("Cyber Security");

        trackBox.getItems().clear();
        trackBox.getItems().addAll(list);
    }

    @FXML
    protected void onPrintButtonClick() {
        System.out.println("trackBox.getValue() == " + trackBox.getValue());
        System.out.println("student name: " + studentName.getText());
        System.out.println("student id: " + studentID.getText());
        System.out.println("semester admitted: " + studentSemAdmitted.getText());
        System.out.println("Full time? " + fastTrack.isSelected());
        System.out.println("Thesis? " + thesis.isSelected());
        System.out.println("Anticipated Graduation: " + anticipatedGrad.getText());
    }
}