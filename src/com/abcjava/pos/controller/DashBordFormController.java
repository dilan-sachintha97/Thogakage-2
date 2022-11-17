package com.abcjava.pos.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashBordFormController {
    public Label lblDateAndTime;
    public AnchorPane dashBordContext;

    public void initialize(){
        setDateAndTime();
    }

    private void setDateAndTime(){
        //set date and time
        Timeline time = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss");
                    lblDateAndTime.setText(LocalDateTime.now().format(formatter));
                }), new KeyFrame(Duration.seconds(1)));
        time.setCycleCount(Animation.INDEFINITE);
        time.play();

    }

    public void btnCustomerManagementOnAction(ActionEvent actionEvent) throws IOException {
        setUI("CustomerManagementForm");
    }

    public void btnItemManagementOnAction(ActionEvent actionEvent) throws IOException {
        setUI("ItemManagementForm");
    }

    public void btnOrderManagementOnAction(ActionEvent actionEvent) throws IOException {
        setUI("OderDetailsForm");
    }

    public void btnPlaceOrderManagement(ActionEvent actionEvent) throws IOException {
        setUI("PlaceOrderForm");
    }

    private void setUI(String ui) throws IOException {
        Stage stage = (Stage) dashBordContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+ui + ".fxml"))));
        stage.centerOnScreen();
    }
}
