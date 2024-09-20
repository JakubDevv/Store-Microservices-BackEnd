package com.example.store.repository;

import com.example.store.model.Product;
import com.example.store.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT r from Product p JOIN p.reviews r WHERE p.id = ?1 ORDER BY r.sendtime DESC ")
    Page<ProductReview> getProductReviewsByProductId(Long id, Pageable pageable);

    @Query("SELECT coalesce(count(p),0) from Product p WHERE p.created BETWEEN ?1 AND ?2")
    Integer getAmountOfNewProducts(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p from Product p WHERE p.id = ?1 AND p.companyid = ?2")
    Optional<Product> getProductByIdAndCompanyId(Long productId, Long companyId);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r where p.created >= ?1 and p.retired=null GROUP BY p.id")
    Page<Product> getPremieredProducts(LocalDateTime premiereTime, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r where p.discount_price != null and p.retired=null GROUP BY p.id")
    Page<Product> getDiscountedProducts(Pageable page);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r WHERE p.retired=null GROUP BY p.id ORDER BY coalesce(round(AVG(r.rating),0),0) desc")
    List<Product> getMostRatedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.parameters pa LEFT JOIN p.reviews r where p.name LIKE %?1% or p.description LIKE %?1% or pa.value LIKE %?1% and p.retired=null GROUP BY p.id")
    Page<Product> getProductsByQuery(String query, Pageable page);

    List<Product> findProductsByCompanyid(Long companyId);

    @Query("SELECT r FROM Product p RIGHT JOIN p.reviews r where p.companyid = ?1 and r.sendtime > ?2")
    List<ProductReview> findProductReviewsByCompanyIdAndTime(Long companyId, LocalDateTime dateTime);

    @Query("SELECT r FROM Product p RIGHT JOIN p.reviews r where p.companyid = ?1 and r.sendtime BETWEEN ?2 AND ?3")
    List<ProductReview> findProductReviewsByCompanyIdAndTime(Long companyId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET retired = CURRENT_TIMESTAMP WHERE companyid = :companyId AND retired IS NULL", nativeQuery = true)
    void retireProductsByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET retired = CURRENT_TIMESTAMP WHERE id = :productId AND retired IS NULL", nativeQuery = true)
    void retireProductById(@Param("productId") Long productId);

    @Query("SELECT count(p) FROM Product p")
    int getAmountOfProducts();

    @Query("select count(p) from Product p where p.retired = null and p.companyid = ?1")
    int getAmountOfActiveProducts(Long companyId);

    @Query("select count(p) from Product p where p.retired != null and p.companyid = ?1")
    int getAmountOfRetiredProducts(Long companyId);

    @Query("select avg(r.rating) from Product p Left join p.reviews r where p.companyid = ?1")
    double getRatingByCompanyId(Long companyId);

    @Query("select coalesce(avg(r.rating), 0) from Product p RIGHT join p.reviews r where p = ?1")
    Double getRatingByProduct(Product product);

    @Query("select coalesce(count(r.rating), 0) from Product p RIGHT join p.reviews r where p = ?1")
    Integer getRatingOfProduct(Product product);

    @Query("select coalesce(coalesce(p.discount_price, p.price) * sum(s.quantity), 0) from Product p RIGHT join p.sizes s where p = ?1 group by p.id")
    BigDecimal getValueOfStoredProduct(Product product);

    @Query("select coalesce(sum(s.quantity * coalesce(p.discount_price, p.price)), 0) from Product p right join p.sizes s where p.companyid = ?1")
    BigDecimal getValueOfStoredProductsByCompanyId(Long companyId);

}
