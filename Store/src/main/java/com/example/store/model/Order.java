package com.example.store.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_orderitem_orders_table")
    private List<OrderItem> orderItem;

    private Long userid;

    private String city;

    private String street;

    private int house_number;

    private String zipcode;

    private int phone;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_order_status_order")
    private List<OrderStatus> statuses;

    public Order() {
    }

    public Order(Long userid, String city, String street, int house_number, String zipcode, int phone) {
        this.userid = userid;
        this.orderItem = new ArrayList<>();
        this.city = city;
        this.street = street;
        this.house_number = house_number;
        this.zipcode = zipcode;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItem.add(item);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouse_number() {
        return house_number;
    }

    public void setHouse_number(int number) {
        this.house_number = number;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public List<OrderStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<OrderStatus> statuses) {
        this.statuses = statuses;
    }
}
