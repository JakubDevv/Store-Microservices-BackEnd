package com.example.store.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orderitem")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_order_status_orderitem")
    private List<OrderStatus> statuses;

    private BigDecimal price;

    private String size;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "fk_product_orderitem")
    private Product product;

    public OrderItem() {
    }

    public OrderItem(int quantity, Long userid) {
        this.quantity = quantity;
//        this.userid = userid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

//    public Long getUserid() {
//        return userid;
//    }

//    public void setUserid(Long userid) {
//        this.userid = userid;
//    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<OrderStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<OrderStatus> statuses) {
        this.statuses = statuses;
    }
}
