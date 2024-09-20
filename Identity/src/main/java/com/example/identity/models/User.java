package com.example.identity.models;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_name;

    private String first_name;

    private String last_name;

    private String password;

    private LocalDateTime created;

    private LocalDateTime banned;

    private BigDecimal balance;

    private boolean photo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "fk_user_company")
    private Company company;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_transaction_users")
    private List<Transaction> transactions;

    public User(String user_name, String first_name, String last_name, String password) {
        this.user_name = user_name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.created = LocalDateTime.now();
        this.balance = BigDecimal.valueOf(1000);
        this.transactions = new ArrayList<>();
        this.photo = false;
    }

    public String getPassword() {
        return password;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String userName) {
        this.user_name = userName;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String firstName) {
        this.first_name = firstName;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String lastName) {
        this.last_name = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime creationTime) {
        this.created = creationTime;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getBanned() {
        return banned;
    }

    public void setBanned(LocalDateTime banned) {
        this.banned = banned;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isPhoto() {
        return photo;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }
}
