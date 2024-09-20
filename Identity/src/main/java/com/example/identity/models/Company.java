package com.example.identity.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime created;

    private LocalDateTime banned;

    public Company() {
    }

    public Company(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String companyName) {
        this.name = companyName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime creation_date) {
        this.created = creation_date;
    }

    public LocalDateTime getBanned() {
        return banned;
    }

    public void setBanned(LocalDateTime banned) {
        this.banned = banned;
    }
}
