package com.abcjava.pos.controller;

import com.abcjava.pos.db.Database;
import com.abcjava.pos.modal.Item;
import com.abcjava.pos.view.tm.ItemTm;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ItemManagementFormController {
    public TextField txtCode;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public JFXButton btnSaveItem;
    public TextField txtSearchItem;
    public TableView<ItemTm> tblItemDetails;
    public TableColumn<ItemTm,String> colCode;
    public TableColumn<ItemTm,String>  colDescription;
    public TableColumn <ItemTm,Double> colUnitPrice;
    public TableColumn<ItemTm,Button>  colOptions;
    public AnchorPane itemManagementContext;
    public TableColumn<ItemTm,Integer>  colQtyOnHand;
    private String text = "";

    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("button"));

        txtSearchItem.textProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println(newValue);
            setDataToTable(newValue);
        });

        tblItemDetails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println( newValue.getDescription());
            if(null!= newValue){
                selectedTableRowToTextField(newValue);
            }

        });

        setDataToTable(text);
    }

    private void selectedTableRowToTextField(ItemTm itemTm) {
        txtCode.setText(itemTm.getCode());
        txtDescription.setText(itemTm.getDescription());
        txtUnitPrice.setText(String.valueOf(itemTm.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(itemTm.getCode()));
        btnSaveItem.setText("Update Item");
    }


    public void btnBackToHome(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemManagementContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBordForm.fxml"))));
    }

    public void btnAddNewItem(ActionEvent actionEvent) {
        btnSaveItem.setText("save Item");
        clearField();

    }

    public void btnSaveItemOnAction(ActionEvent actionEvent) {
        if(btnSaveItem.getText().equalsIgnoreCase("Save Item")){ //save item
            setDataToDatabase();
            setDataToTable(text);
            clearField();
        }else{
            for(int i=0; i<Database.itemList.size(); i++){
                if(txtCode.getText().equalsIgnoreCase(Database.itemList.get(i).getCode())){
                    Database.itemList.get(i).setDescription(txtDescription.getText());
                    Database.itemList.get(i).setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
                    Database.itemList.get(i).setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
                }
            }
            setDataToTable(text);
            clearField();
        }

    }

    private void clearField() {
        txtCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
    }

    private void setDataToTable(String text) {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();
        for (Item c:Database.itemList) {

            //search text
            if(c.getDescription().contains(text)){
                Button button = new Button("Delete");
                ItemTm itemTm = new ItemTm(c.getCode(), c.getDescription(), c.getUnitPrice(), c.getQtyOnHand(),button);
                tmList.add(itemTm);
                button.setOnAction(event -> {
//                    System.out.println(c.getDescription());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this item ?",
                            ButtonType.YES,ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if(buttonType.get().equals(ButtonType.YES)){
                        boolean isDeleted = Database.itemList.remove(c);
                        if(isDeleted){
                            setDataToTable(text);
                            new Alert(Alert.AlertType.INFORMATION, "Deleted Item !").show();
                        }else {
                            new Alert(Alert.AlertType.INFORMATION, "something wrong !").show();
                        }
                    }
                });
            }

        }

        tblItemDetails.setItems(tmList);
    }

    private void setDataToDatabase() {
        Item item = new Item(txtCode.getText(), txtDescription.getText(), Double.parseDouble(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText()));
        boolean isSaved = Database.itemList.add(item);
        if(isSaved){
            new Alert(Alert.AlertType.INFORMATION, "item Saved").show();
        }else{
            new Alert(Alert.AlertType.INFORMATION, "Something wrong !");
        }

    }
}
