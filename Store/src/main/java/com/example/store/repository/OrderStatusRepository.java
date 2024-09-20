package com.example.store.repository;

import com.example.store.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {

    @Query(value = "SELECT COALESCE(time, null) FROM order_status LEFT JOIN orderitem ON orderitem.id = order_status.fk_order_status_orderitem LEFT JOIN product ON product.id = orderitem.fk_product_orderitem WHERE product.companyid = ?1 AND order_status.status = 'SENT' ORDER BY order_status.time DESC LIMIT 1", nativeQuery = true)
    LocalDateTime gets(Long userId);
}
