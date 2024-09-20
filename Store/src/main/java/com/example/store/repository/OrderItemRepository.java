package com.example.store.repository;

import com.example.store.model.OrderItem;
import com.example.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findOrderItemsByProductCompanyid(Long userId);

    List<OrderItem> findOrderItemsByProduct(Product product);

    @Query("select oi from OrderItem oi LEFT JOIN oi.product p where p.id = ?1")
    List<OrderItem> findOrderItemsByProductId(Long productId);


}
