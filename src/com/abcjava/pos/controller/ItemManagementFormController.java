package com.abcjava.pos.controller;

import com.abcjava.pos.db.DBConnection;
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
import java.sql.*;
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
        txtQtyOnHand.setText(String.valueOf(itemTm.getQtyOnHand()));
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
            try {
                String sql = "UPDATE Item SET description =?, unitPrice=?, qtyOnHand=? WHERE code=?";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);

                statement.setString(1, txtDescription.getText());
                statement.setDouble(2, Double.parseDouble(txtUnitPrice.getText()));
                statement.setInt(3, Integer.parseInt(txtQtyOnHand.getText()));
                statement.setString(4, txtCode.getText());
                int isUpdated = statement.executeUpdate();

                if (isUpdated > 0) {
                    clearField();
                    new Alert(Alert.AlertType.INFORMATION, "Item Updated").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
                }

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
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
        //search
        String searchText = "%"+text+"%";
        try{
            String sql = "SELECT * FROM Item WHERE description LIKE ?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,searchText);
            ResultSet set = statement.executeQuery();

            while (set.next()){
                Button button = new Button("Delete");
                ItemTm itemTm = new ItemTm(
                        set.getString(1),
                        set.getString(2),
                        set.getDouble(3),
                        set.getInt(4),
                        button);
                tmList.add(itemTm);

                button.setOnAction(event -> {
                    // System.out.println(c.getName());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure  want to delete this customer ?",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {

                        try{
                            String sql1 = "DELETE FROM Item WHERE code=?";
                            PreparedStatement statement1 = DBConnection.getInstance().getConnection().prepareStatement(sql1);
                            statement1.setString(1,itemTm.getCode());

                            if(statement1.executeUpdate() > 0){
                                 setDataToTable(text);
                                new Alert(Alert.AlertType.INFORMATION, "Item Deleted").show();
                            }else {
                                new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
                            }

                        }catch (ClassNotFoundException | SQLException e){
                            e.printStackTrace();
                        }

                    }

                });
            }


        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        tblItemDetails.setItems(tmList);
        }



    private void setDataToDatabase() {
        Item item = new Item(txtCode.getText(), txtDescription.getText(), Double.parseDouble(txtUnitPrice.getText()), Integer.parseInt(txtQtyOnHand.getText()));
        try {
            String sql = "INSERT INTO Item VALUES (?,?,?,?)";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, item.getCode());
            statement.setString(2, item.getDescription());
            statement.setDouble(3, item.getUnitPrice());
            statement.setInt(4, item.getQtyOnHand());
            int isSaved = statement.executeUpdate();

            if (isSaved > 0) {
                clearField();
                new Alert(Alert.AlertType.INFORMATION, "Item Saved").show();
            } else {
                new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
            }


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}
