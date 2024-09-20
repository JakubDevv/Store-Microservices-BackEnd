package com.example.store.model;

import jakarta.persistence.*;

@Entity
@Table(name = "filtervalue")
public class FilterValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String value;

    public FilterValue() {
    }

    public FilterValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
