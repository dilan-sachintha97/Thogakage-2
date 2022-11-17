package com.abcjava.pos.view.tm;

import javafx.scene.control.Button;

import java.util.Date;

public class OderTm {
    private String oderId;
    private String customerName;
    private Date oderDate;
    private double total;
    private Button button;

    public OderTm() {
    }

    public OderTm(String oderId, String customerName, Date oderDate, double total, Button button) {
        this.oderId = oderId;
        this.customerName = customerName;
        this.oderDate = oderDate;
        this.total = total;
        this.button = button;
    }

    public String getOderId() {
        return oderId;
    }

    public void setOderId(String oderId) {
        this.oderId = oderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getOderDate() {
        return oderDate;
    }

    public void setOderDate(Date oderDate) {
        this.oderDate = oderDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
