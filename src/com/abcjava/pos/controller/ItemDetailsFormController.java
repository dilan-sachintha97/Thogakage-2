package com.abcjava.pos.controller;

import com.abcjava.pos.db.Database;
import com.abcjava.pos.modal.ItemDetails;
import com.abcjava.pos.modal.Order;
import com.abcjava.pos.view.tm.ItemDetailsTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ItemDetailsFormController {
    public TableView<ItemDetailsTm> tblItemDetails;
    public TableColumn<ItemDetailsTm,String> colItemCode;
    public TableColumn<ItemDetailsTm,Double> colUnitPrice;
    public TableColumn<ItemDetailsTm,Integer> colQty;
    public TableColumn<ItemDetailsTm,Double> colTotal;
    public AnchorPane itemDetailsContext;

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    public void loadItemDetails(String oderId){
        for(Order order : Database.ordersList){
            if(order.getOrderId().equals(oderId)){
                ObservableList<ItemDetailsTm> obsItemList = FXCollections.observableArrayList();

                for(ItemDetails item : order.getItemDetails()){
                    double tempUnitPrice = item.getUnitPrice();
                    int tempQtyOnHand = item.getQty();
                    double tempTotal = tempQtyOnHand * tempUnitPrice;
                    ItemDetailsTm itemDetailsTm = new ItemDetailsTm(item.getCode(),item.getUnitPrice(),item.getQty(),tempTotal);

                    obsItemList.add(itemDetailsTm);
                }
                tblItemDetails.setItems(obsItemList);
                return;
            }
        }
    }

    public void btnBackToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemDetailsContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBordForm.fxml"))));
    }

}
