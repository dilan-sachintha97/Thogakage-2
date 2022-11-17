package com.abcjava.pos.controller;

import com.abcjava.pos.db.Database;
import com.abcjava.pos.modal.Customer;
import com.abcjava.pos.view.tm.CustomerTm;
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
        // set value to table
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        for (Customer c : Database.customerList) {

            if(c.getName().contains(text) || c.getAddress().contains(text)){
                Button button = new Button("Delete");
                CustomerTm customerTm = new CustomerTm(
                        c.getId(), c.getName(), c.getAddress(), c.getSalary(), button
                );
                tmList.add(customerTm);

                button.setOnAction(event -> {
                    // System.out.println(c.getName());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure  want to delete this customer ?",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get() == ButtonType.YES) {
                        boolean isDeleted = Database.customerList.remove(c);
                        if (isDeleted) {
                            searchCustomers(searchText);
                            new Alert(Alert.AlertType.INFORMATION, "Customer Deleted").show();
                        } else {
                            new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
                        }
                    }

                });
            }


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
            // save customer in arrayList
            boolean isSaved = Database.customerList.add(customer);
            if (isSaved) {
                searchCustomers(searchText);
                clearField();
                new Alert(Alert.AlertType.INFORMATION, "Customer Saved").show();
            } else {
                new Alert(Alert.AlertType.WARNING, "Something Wrong!").show();
            }
        } else {
            //update customer
            for (int i = 0; i < Database.customerList.size(); i++) {
                if (txtId.getText().equalsIgnoreCase(Database.customerList.get(i).getId())) {
                    Database.customerList.get(i).setName(txtName.getText());
                    Database.customerList.get(i).setAddress(txtAddress.getText());
                    Database.customerList.get(i).setSalary(Double.parseDouble(txtSalary.getText()));

                    searchCustomers(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Customer Updated").show();
                    clearField();
                }
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

