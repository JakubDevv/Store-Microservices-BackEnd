package com.example.store.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "filter")
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(referencedColumnName = "id", name = "fk_filtervalue_filter")
    private List<FilterValue> values;

    public Filter() {
    }

    public Filter(String key) {
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<FilterValue> getValues() {
        return values;
    }

    public void setValues(List<FilterValue> values) {
        this.values = values;
    }
}
