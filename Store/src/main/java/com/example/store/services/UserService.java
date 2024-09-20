package com.example.store.services;

import com.example.store.dto.order.OrderCreateDTO;
import com.example.store.dto.order.OrderUserDTO;
import com.example.store.dto.product.ProductAvailability;
import com.example.store.dto.product.ProductReviewCreateDTO;
import com.example.store.dto.stats.UserSpendingStats;
import com.example.store.dto.user.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface UserService {

    UserDTO getUser(String authToken);

    List<ProductAvailability> createOrder(String authToken, Long userId, OrderCreateDTO order);

    void createProductReview(Long userId, ProductReviewCreateDTO productReviewCreateDto);

    boolean checkIfUserBoughtProduct(Long userId, Long productId);

    ResponseEntity<?> getProfilePhoto(Long userId);

    void updateProfilePhoto(Long userId, MultipartFile file);

    List<OrderUserDTO> getOrders(Long userId);

    void setOrderStatusCompleted(Long userId, Long orderId);

    Set<Long> getCompaniesByOrderId(Long orderId);

    UserSpendingStats getStatsFromOrders(Long userId);
}
