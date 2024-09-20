package com.example.store.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "parameter")
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String value;

    public Parameter(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public Parameter() {
    }

    public Parameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public boolean compareFilters(String key, String values) {
        String[] values2 = values.split(", ");
        for (String value : values2) {
            if (Objects.equals(this.key, key)) {
                if (Objects.equals(this.value, value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
