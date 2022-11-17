package com.abcjava.pos.controller;

import com.abcjava.pos.db.Database;
import com.abcjava.pos.modal.Customer;
import com.abcjava.pos.modal.Item;
import com.abcjava.pos.modal.ItemDetails;
import com.abcjava.pos.modal.Order;
import com.abcjava.pos.view.tm.CartTm;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static com.abcjava.pos.db.Database.ordersList;

public class PlaceOrderFormController {
    public TextField txtOrderId;
    public TextField txtOrderDate;
    public ComboBox<String> cmbCustomerId;
    public ComboBox<String> cmbItemId;
    public AnchorPane placeOrderFormContext;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQty;
    public TextField txtQtyOnHand;
    public TableView<CartTm> tblCart;
    public TableColumn<CartTm, String> colItemCode;
    public TableColumn<CartTm, String> colDescription;
    public TableColumn<CartTm, Double> colUnitPrice;
    public TableColumn<CartTm, Integer> colQty;
    public TableColumn<CartTm, Double> colTotal;
    public TableColumn<CartTm, Button> colOption;
    public Label lblTotal;

    public void initialize() {

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("button"));


        setOrderDate();
        loadAllCustomersIds();
        loadAllItemsCode();
        setOrderId();

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                setCustomerDetailsToTextFields();
            }
        });

        cmbItemId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                setItemDetailsToTextFields();
            }
        });

    }

    private void setOrderId() {
        if(Database.ordersList.isEmpty()){
            txtOrderId.setText("D-1");
            return;
        }
        String tempOrderId = Database.ordersList.get(Database.ordersList.size()-1).getOrderId(); //  D-5
        String[] numArray = tempOrderId.split("-"); //  [D,5]
        int numPart = Integer.parseInt(numArray[1]);  //   5
        int finalizeNumberOfOderId = numPart+1;
        txtOrderId.setText("D-" + finalizeNumberOfOderId);
    }

    private void setItemDetailsToTextFields() {
        for (Item item : Database.itemList) {
            if (item.getCode().equals(cmbItemId.getValue())) {
                txtDescription.setText(item.getDescription());
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));

            }
        }
    }

    private void setCustomerDetailsToTextFields() {
        for (Customer c : Database.customerList) {
            if (c.getId().equals(cmbCustomerId.getValue())) {
                txtName.setText(c.getName());
                txtAddress.setText(c.getAddress());
                txtSalary.setText(String.valueOf(c.getSalary()));
            }
        }
    }


    private void loadAllItemsCode() {
        for (Item item : Database.itemList) {
            cmbItemId.getItems().add(item.getCode());
        }
    }

    private void loadAllCustomersIds() {
        for (Customer c : Database.customerList) {
            cmbCustomerId.getItems().add(c.getId());
        }
    }

    private void setOrderDate() {
        /*Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String orderDate = df.format(date);
        txtOrderDate.setText(orderDate);*/

        txtOrderDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    public void btnBackToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) placeOrderFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashBordForm.fxml"))));
    }

    private boolean checkQty(String code, int qty){
        for(Item i: Database.itemList){
            if(code.equals(i.getCode())){
                if(i.getQtyOnHand()>= qty){
                    return true;
                }
            }
        }
        return false;
    }
    ObservableList<CartTm> obList = FXCollections.observableArrayList();
    public void btnAddToCartOnAction(ActionEvent actionEvent) {

        if(!checkQty(cmbItemId.getValue(), Integer.parseInt(txtQty.getText()))){
            new Alert(Alert.AlertType.WARNING,"Out of Stock !").show();
            return;
        }

        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double total = unitPrice * qty;
        Button button = new Button("Delete");

        int row = isAlreadyExit(cmbItemId.getValue());

        if (row == -1) {
            CartTm cartTm = new CartTm(cmbItemId.getValue(), txtDescription.getText(), unitPrice, qty, total, button);
            obList.add(cartTm);
            tblCart.setItems(obList);
        } else {
            int tempQty = obList.get(row).getQty() + qty;
            double tempTotal = unitPrice * tempQty;

            if(!checkQty(cmbItemId.getValue(), tempQty)){
                new Alert(Alert.AlertType.WARNING,"Out of Stock !").show();
                return;
            }

            obList.get(row).setQty(tempQty);
            obList.get(row).setTotal(tempTotal);
            tblCart.refresh();
        }
        calculateTotal();
//            clearFields();
        cmbItemId.requestFocus();

        button.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                for (CartTm tm : obList) {
                    obList.remove(tm);
                    calculateTotal();
                    tblCart.refresh();
                    return;
                }
            }
        });
    }

    private void clearFields() {
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtQty.clear();
    }

    private int isAlreadyExit(String code) {
        for (int i = 0; i < obList.size(); i++) {
            if (obList.get(i).getCode().equals(code)) {
                return i;  // row number
            }
        }
        return -1;
    }

    private void calculateTotal() {
        double total = 0;
        for (CartTm cartTm : obList) {
            total = total + cartTm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        if (obList.isEmpty()) return;  // obList ak empty nam JVM aka methanin ehata yanna apa
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        for (CartTm cartTm : obList) {
            ItemDetails itemDetails = new ItemDetails(cartTm.getCode(), cartTm.getUnitPrice(), cartTm.getQty());
            itemDetailsList.add(itemDetails);
        }

        Order order = new Order(txtOrderId.getText(),
                new Date(),
                Double.parseDouble(lblTotal.getText()),
                cmbItemId.getValue(),
                itemDetailsList);

        ordersList.add(order);
        manageQty();
        clearAll();
    }

    private void manageQty() {
        for(CartTm cartTm : obList){
            for(Item item : Database.itemList){
                if(cartTm.getCode().equals(item.getCode())){
                    item.setQtyOnHand(item.getQtyOnHand()-cartTm.getQty());
                    break;
                }
            }
        }
    }

    private void clearAll() {
        obList.clear();
        calculateTotal();

        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();

        //=======================
        cmbCustomerId.setValue(null);
        cmbItemId.setValue(null);
        //=======================

        clearFields();
        cmbCustomerId.requestFocus();
        setOrderId();
    }

}

