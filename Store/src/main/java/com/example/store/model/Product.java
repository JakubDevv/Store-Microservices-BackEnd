package com.example.store.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discount_price;

    private int sales;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_image_product")
    private List<Image> images;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_size_product")
    private List<Size> sizes;

    private Long companyid;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_review_product")
    private List<ProductReview> reviews;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_parameter_product")
    private List<Parameter> parameters;

    private LocalDateTime created;

    private LocalDateTime retired;

    public Product(String name, String description, BigDecimal price, List<Size> sizes, Long companyid, List<Parameter> parameters) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sizes = sizes;
        this.companyid = companyid;
        this.parameters = parameters;
        this.created = LocalDateTime.now();
    }

    public Product() {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int solds) {
        this.sales = solds;
    }

    public List<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ProductReview> reviews) {
        this.reviews = reviews;
    }

    public BigDecimal getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(BigDecimal discountPrice) {
        this.discount_price = discountPrice;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }

    public Long getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Long companyid) {
        this.companyid = companyid;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addReview(ProductReview productReview) {
        this.reviews.add(productReview);
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        this.images.add(image);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getRetired() {
        return retired;
    }

    public void setRetired(LocalDateTime retired) {
        this.retired = retired;
    }
}
