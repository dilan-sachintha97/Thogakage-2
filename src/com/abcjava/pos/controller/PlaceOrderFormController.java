package com.abcjava.pos.controller;

import com.abcjava.pos.db.DBConnection;
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
import java.sql.*;
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
        try{
            String sql = "SELECT orderId FROM `Order` ORDER BY orderId DESC LIMIT 1";  //10 not working (UNSIGN use)
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set = statement.executeQuery();

            if(set.next()){
                String tempOrderId = set.getString(1);
                String[] numArray = tempOrderId.split("-"); //  [D,5]
                int numPart = Integer.parseInt(numArray[1]);  //   5
                int finalizeNumberOfOderId = numPart+1;
                txtOrderId.setText("D-" + finalizeNumberOfOderId);

            }else{
                txtOrderId.setText("D-1");
                return;
            }

        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    private void setItemDetailsToTextFields() {
        try{
            String sql = "SELECT * FROM Item WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,cmbItemId.getValue());
            ResultSet set = statement.executeQuery();

            if(set.next()){
                txtDescription.setText(set.getString(2));
                txtUnitPrice.setText(String.valueOf(set.getDouble(3)));
                txtQtyOnHand.setText(String.valueOf(set.getInt(4)));
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();}
    }

    private void setCustomerDetailsToTextFields() {
        try{
            String sql = "SELECT * FROM Customer WHERE id=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,cmbCustomerId.getValue());
            ResultSet set = statement.executeQuery();

            if(set.next()){
                txtName.setText(set.getString(2));
                txtAddress.setText(set.getString(3));
                txtSalary.setText(String.valueOf(set.getDouble(4)));
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();}

    }


    private void loadAllItemsCode() {
        try{
            String sql = "SELECT code FROM Item";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set = statement.executeQuery();

            ArrayList<String> codeList = new ArrayList<>();
            while (set.next()){
                codeList.add(set.getString(1));
            }
            ObservableList<String> obListOfCode = FXCollections.observableArrayList(codeList);
            cmbItemId.setItems(obListOfCode);

        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void loadAllCustomersIds() {
       try{
           String sql = "SELECT id FROM Customer";
           PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
           ResultSet set = statement.executeQuery();

           ArrayList<String> customerIdList = new ArrayList<>();
           while (set.next()){
               customerIdList.add(set.getString(1));
           }
           ObservableList<String> obListOfCustomerId = FXCollections.observableArrayList(customerIdList);
           cmbCustomerId.setItems(obListOfCustomerId);

       }catch (ClassNotFoundException | SQLException e){
           e.printStackTrace();
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
       try{
           String sql = "SELECT qtyOnhand FROM Item WHERE code=?";
           PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
           statement.setString(1,code);
           ResultSet set = statement.executeQuery();

           if(set.next()){
               int tempQty = set.getInt(1);
               if(tempQty>=qty){
                   return true;
               }
           }

       }catch (Exception e){
           e.printStackTrace();
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

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) throws SQLException {
        if (obList.isEmpty()) return;  // obList ak empty nam JVM aka methanin ehata yanna apa
        ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        for (CartTm cartTm : obList) {
            ItemDetails itemDetails = new ItemDetails(cartTm.getCode(), cartTm.getUnitPrice(), cartTm.getQty());
            itemDetailsList.add(itemDetails);
        }

        Order order = new Order(txtOrderId.getText(),
                new Date(),
                Double.parseDouble(lblTotal.getText()),
                cmbCustomerId.getValue(),
                itemDetailsList);


        //place order
        Connection con = null;
        try{

            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            String sql = "INSERT INTO `Order` VALUES (?,?,?,?)";

           // PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setString(1, order.getOrderId());
            statement.setString(2, txtOrderDate.getText());
            statement.setDouble(3, order.getTotalCost());
            statement.setString(4, order.getCustomer());

            boolean isOderSaved = statement.executeUpdate() > 0;

            if(isOderSaved){
                boolean isAllUpdated = manageQty(itemDetailsList);
                if(isAllUpdated){
                    con.commit();
                    new Alert(Alert.AlertType.CONFIRMATION,"Order Placed !").show();
                    clearAll();
                }else {
                    con.setAutoCommit(true);
                    con.rollback();
                    new Alert(Alert.AlertType.WARNING,"Try Again !").show();
                }

            }else {
                con.setAutoCommit(true);
                con.rollback();
                new Alert(Alert.AlertType.WARNING,"Try Again !").show();
            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            con.setAutoCommit(true);
        }

    }

    private boolean manageQty(ArrayList<ItemDetails> itemDetailsList) {

        try{
            for(ItemDetails d : itemDetailsList){
                String sql = "INSERT INTO `Order Details` VALUES (?,?,?,?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, d.getCode());
                statement.setString(2, txtOrderId.getText());
                statement.setDouble(3, d.getUnitPrice());
                statement.setInt(4, d.getQty());

                boolean isSavedOrderDetails = statement.executeUpdate() > 0;

                if(isSavedOrderDetails){
                    boolean isUpdated = update(d);
                    if(!isUpdated){
                        return false;
                    }
                }else {
                    return false;
                }
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private boolean update(ItemDetails d) {
        try {
            String sql = "UPDATE Item SET qtyOnHand =(qtyOnHand - ?) WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setInt(1, d.getQty());
            statement.setString(2, d.getCode());
            return statement.executeUpdate() > 0;  // true

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
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

