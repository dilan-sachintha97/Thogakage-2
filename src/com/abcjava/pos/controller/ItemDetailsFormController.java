package com.abcjava.pos.controller;

import com.abcjava.pos.db.DBConnection;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        try{
            String sql = "SELECT o.orderid, d.itemCode, d.orderId, d.unitprice, d.qty"+
                    " FROM `Order` o INNER JOIN  `Order Details` d ON o.orderId=d.orderId AND o.orderId=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,oderId);
            ResultSet set = statement.executeQuery();

            ObservableList<ItemDetailsTm> obsItemList = FXCollections.observableArrayList();
            while (set.next()){
                double tempUnitPrice = set.getDouble(4);
                int tempQtyOnHand = set.getInt(5);
                double tempTotal = tempQtyOnHand * tempUnitPrice;

                ItemDetailsTm itemDetailsTm = new ItemDetailsTm(set.getString(2),tempUnitPrice,tempQtyOnHand,tempTotal);
                obsItemList.add(itemDetailsTm);
            }
            tblItemDetails.setItems(obsItemList);


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void btnBackToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemDetailsContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBordForm.fxml"))));
    }

}
