package com.abcjava.pos.controller;

import com.abcjava.pos.db.DBConnection;
import com.abcjava.pos.db.Database;
import com.abcjava.pos.modal.Customer;
import com.abcjava.pos.view.tm.CustomerTm;
import com.jfoenix.controls.JFXButton;
import com.mysql.cj.jdbc.Driver;
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

public class CustomerManagementFormController {
    public AnchorPane customerManagementContext;
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public TableView<CustomerTm> tblCustomerDetails;
    public TableColumn<CustomerTm, String> colId;
    public TableColumn<CustomerTm, String> colName;
    public TableColumn<CustomerTm, String> colAddress;
    public TableColumn<CustomerTm, Double> colSalary;
    public TableColumn<CustomerTm, Button> colOptions;
    public JFXButton btnSaveCustomer;
    public TextField txtSearchCustomer;

    private String searchText="";

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("button"));

        tblCustomerDetails.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue != null){  //null != newValue  <- more speed
                        setData(newValue);
                    }

        });

        txtSearchCustomer.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                searchText=newValue;
            searchCustomers(searchText);
        });

        searchCustomers(searchText);
    }

    private void setData(CustomerTm tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getName());
        txtAddress.setText(tm.getAddress());
        txtSalary.setText(String.valueOf(tm.getSalary()));
        btnSaveCustomer.setText("Update Customer");
    }

    private void searchCustomers(String text) {
        String searchText = "%"+text+"%";

        // set value to table
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        try {
            String sql = "SELECT * FROM Customer WHERE name LIKE ? || address LIKE ?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,searchText);
            statement.setString(2,searchText);
            ResultSet set = statement.executeQuery();



            while (set.next()){
                Button button = new Button("Delete");
                CustomerTm customerTm = new CustomerTm(
                        set.getString(1),
                        set.getString(2),
                        set.getString(3),
                        set.getDouble(4),
                        button
                );
                tmList.add(customerTm);
                button.setOnAction(event -> {
                    // System.out.println(c.getName());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure  want to delete this customer ?",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {

                        try{
                            String sql1 = "DELETE FROM Customer WHERE id=?";
                            PreparedStatement statement1 = DBConnection.getInstance().getConnection().prepareStatement(sql1);
                            statement1.setString(1,customerTm.getId());

                            if(statement1.executeUpdate() > 0){
                                searchCustomers(searchText);
                                new Alert(Alert.AlertType.INFORMATION, "Customer Deleted").show();
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
        tblCustomerDetails.setItems(tmList);
    }

    private void clearField() {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
    }

    public void btnSaveCustomerOnAction(ActionEvent actionEvent) {

        Customer customer = new Customer(
                txtId.getText(), txtName.getText(), txtAddress.getText(), Double.parseDouble(txtSalary.getText())
        );

        if (btnSaveCustomer.getText().equalsIgnoreCase("Save Customer")) {

            //Save customer in database - mySQl

            try {
                String sql = "INSERT INTO Customer VALUES (?,?,?,?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1,customer.getId());
                statement.setString(2,customer.getName());
                statement.setString(3,customer.getAddress());
                statement.setDouble(4,customer.getSalary());

                //step 05 -> execute statement
                int isSaved = statement.executeUpdate();

                if (isSaved > 0) {
                    searchCustomers(searchText);
                    clearField();
                    new Alert(Alert.AlertType.INFORMATION, "Customer Saved").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
                }

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }



        } else {
            //update customer

            try{
                String sql = "UPDATE Customer SET name =?, address=?, salary=? WHERE id=?";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1,txtName.getText());
                statement.setString(2,txtAddress.getText());
                statement.setDouble(3, Double.parseDouble(txtSalary.getText()));
                statement.setString(4,txtId.getText());
                int isUpdated = statement.executeUpdate();
                if(isUpdated > 0){
                    searchCustomers(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Customer Updated").show();
                    clearField();
                }else {
                    new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }

        }
    }

    public void btnAddNewCustomer(ActionEvent actionEvent) {
        btnSaveCustomer.setText("Save Customer");
    }

    public void btnBackToHome(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) customerManagementContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBordForm.fxml"))));
    }
}

