package com.example.store.model;

import jakarta.persistence.*;

@Entity
@Table(name = "size")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sizevalue;

    private int quantity;

    public Size() {
    }

    public Size(Long id, String sizeValue, int quantity) {
        this.id = id;
        this.sizevalue = sizeValue;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSizevalue() {
        return sizevalue;
    }

    public void setSizevalue(String sizeValue) {
        this.sizevalue = sizeValue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
