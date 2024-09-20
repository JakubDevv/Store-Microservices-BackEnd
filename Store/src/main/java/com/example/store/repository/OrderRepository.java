package com.example.store.repository;

import com.example.store.dto.stats.AmountDate2;
import com.example.store.model.Order;
import com.example.store.model.OrderItem;
import com.example.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN o.orderItem ord WHERE ord.product.companyid = ?1 GROUP BY o.id")
    List<Order> getOrdersByCompanyId(Long companyId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderItem ord " +
            "JOIN ord.product pr " +
            "JOIN ord.statuses st " +
            "WHERE st.status = 'IN_PROGRESS' AND st.time > ?2 AND pr.companyid = ?1")
    List<Order> getOrdersByCompanyIdAndEndDate(Long companyId, LocalDateTime endDate);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderItem ord " +
            "JOIN ord.product pr " +
            "JOIN ord.statuses st " +
            "WHERE st.status = 'IN_PROGRESS' AND st.time BETWEEN ?3 AND ?2 AND pr.companyid = ?1")
    List<Order> getOrdersByCompanyIdAndStartDateAndEndDate(Long companyId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o RIGHT JOIN o.orderItem ord RIGHT JOIN ord.statuses st WHERE ord.product.companyid = ?1 AND CAST(st.status AS String) = ?2")
    List<Order> getOrdersByCompanyIdAndStatus(Long companyId, String status);

    @Query("SELECT o FROM Order o LEFT JOIN o.statuses st WHERE o.userid = ?1 AND CAST(st.status AS String) = 'SENT'")
    List<Order> getSentOrdersByUserid(Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN o.statuses st WHERE o.userid = ?1")
    List<Order> getOrdersByUserid(Long userId);

    @Query("SELECT coalesce(SUM(ord.price * ord.quantity),0) FROM Order o RIGHT JOIN o.orderItem ord WHERE ord.product.companyid = ?1")
    BigDecimal getMoneyTurnoverByCompanyId(Long sellerId);

    @Query("SELECT coalesce(SUM(it.price * it.quantity),0) from Order o LEFT JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND s.time BETWEEN ?1 AND ?2 ")
    BigDecimal getMoneyTurnoverInTime(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT coalesce(SUM(it.price * it.quantity),0) from Order o LEFT JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND o.userid = ?1")
    BigDecimal getMoneyTurnoverByUserId(Long userId);

    @Query("SELECT coalesce(SUM(it.price * it.quantity),0) from Order o LEFT JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND o.userid = ?1 AND s.time > ?2")
    BigDecimal calculateMoneySpentByUserId(Long userId, LocalDateTime date);

    @Query("SELECT ROUND(coalesce(SUM(it.price * it.quantity),0) / ?2, 2) from Order o LEFT JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND o.userid = ?1")
    BigDecimal calculateAverageMoneySpentByUserIdAndMonths(Long userId, int months);

    @Query("SELECT new com.example.store.dto.stats.AmountDate2(s.time, SUM(it.price * it.quantity)) " +
            "FROM Order o " +
            "LEFT JOIN o.statuses s " +
            "LEFT JOIN o.orderItem it " +
            "WHERE s.status = 'IN_PROGRESS' AND o.userid = ?1 AND s.time > ?2 " +
            "GROUP BY s.time")
    List<AmountDate2> calculateMoneySpentByUserIdInDays(Long userId, LocalDateTime date);

    @Query("SELECT coalesce(count(o), 0) from Order o LEFT JOIN o.statuses s WHERE s.status='IN_PROGRESS' AND s.time BETWEEN ?1 AND ?2")
    Integer getQuantityOfUncompletedOrders(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(sum(oi.quantity), 0) from Order o join o.orderItem oi LEFT JOIN o.statuses s WHERE s.status='IN_PROGRESS' AND s.time BETWEEN ?1 AND ?2 ")
    Integer getAmountOfSoldProducts(LocalDateTime startDate, LocalDateTime endDate);

    List<Order> findOrdersByUserid(Long userId);

    @Query("SELECT o FROM Order o left JOIN o.statuses st WHERE o.userid=?1 AND CAST(st.status AS String) = ?2")
    List<Order> getOrdersByUserIdAndStatus(Long userId, String status);

    @Query("SELECT o FROM Order o RIGHT JOIN o.orderItem or RIGHT join or.product p where p = ?1")
    List<Order> getOrdersByProduct(Product product);

    Order findOrderByOrderItemContaining(OrderItem orderItem);

    @Query("SELECT count(o) FROM Order o")
    int getAmountOfOrders();
}
