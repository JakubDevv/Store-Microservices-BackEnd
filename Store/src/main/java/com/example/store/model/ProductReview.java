package com.example.store.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "productreview")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private int rating;

    private LocalDateTime sendtime;

    private Long userid;

    public ProductReview() {
    }

    public ProductReview(String message, int rating, Long userid) {
        this.message = message;
        this.rating = rating;
        this.userid = userid;
        this.sendtime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getSendtime() {
        return sendtime;
    }

    public void setSendtime(LocalDateTime sendTime) {
        this.sendtime = sendTime;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userId) {
        this.userid = userId;
    }
}
