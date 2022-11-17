package com.abcjava.pos.controller;

import com.abcjava.pos.db.Database;
import com.abcjava.pos.modal.Order;
import com.abcjava.pos.view.tm.OderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Date;

public class OderDetailsFormController {

    public TableView<OderTm> tblOderDetails;
    public TableColumn<OderTm,String> colOderId;
    public TableColumn<OderTm,String>  colCustomer;
    public TableColumn<OderTm, Date>  colDate;
    public TableColumn<OderTm,Double>  colTotal;
    public TableColumn<OderTm,Button>  colOptions;
    public AnchorPane oderDetailsFormContext;

    public void initialize(){
        colOderId.setCellValueFactory(new PropertyValueFactory<>("oderId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("oderDate"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("button"));

        loadOrders();

    }

    private void loadOrders() {
        ObservableList<OderTm> obsOderList = FXCollections.observableArrayList();
        for(Order order : Database.ordersList){
            Button btn = new Button("View more");
            OderTm oderTm = new OderTm(order.getOrderId(), order.getCustomer(), order.getDate(), order.getTotalCost(),btn);
            obsOderList.add(oderTm);

            btn.setOnAction(event -> {
                try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ItemDetailsForm.fxml"));
                    Parent parent = loader.load();
                    ItemDetailsFormController controller = loader.getController();
                    controller.loadItemDetails(oderTm.getOderId());
                    Stage stage = new Stage();
                    stage.setScene(new Scene(parent));
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        tblOderDetails.setItems(obsOderList);
    }

    public void btnBackToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) oderDetailsFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBordForm.fxml"))));
    }
}

// 2.12 video 22