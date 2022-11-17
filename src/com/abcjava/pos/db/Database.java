package com.abcjava.pos.db;

import com.abcjava.pos.modal.Customer;
import com.abcjava.pos.modal.Item;
import com.abcjava.pos.modal.Order;

import java.util.ArrayList;

public class Database {
    public static ArrayList<Customer> customerList = new ArrayList<Customer>();

    public static ArrayList<Item> itemList = new ArrayList<Item>();

    public static ArrayList<Order> ordersList = new ArrayList<>();

    static {
        customerList.add(new Customer("C001","Nimal","Matara",25000));
        customerList.add(new Customer("C002","Sirimal","Colombo",85000));
        customerList.add(new Customer("C003","Nuwam","Matara",45000));
        customerList.add(new Customer("C004","Malith","Galle",50000));
        customerList.add(new Customer("C005","Dilan","Kottawa",70000));

        itemList.add(new Item("I-001","Description 1",25,20));
        itemList.add(new Item("I-002","Description 2",60,8));
        itemList.add(new Item("I-003","Description 3",80,5));
        itemList.add(new Item("I-004","Description 4",100,2));
        itemList.add(new Item("I-005","Description 5",350,6));
    }
}
