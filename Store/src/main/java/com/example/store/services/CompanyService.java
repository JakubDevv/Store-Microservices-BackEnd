package com.example.store.services;

import com.example.store.dto.order.OrderStatusUpdateDTO;
import com.example.store.dto.stats.AmountDate;
import com.example.store.dto.order.OrderCompanyLongDTO;
import com.example.store.dto.order.OrderCompanyShortDTO;
import com.example.store.dto.product.ProductCompanyDTO;
import com.example.store.dto.product.ProductCreateDTO;
import com.example.store.dto.product.ProductDTO2;
import com.example.store.dto.product.ProductUpdateDTO;
import com.example.store.dto.stats.CompanyStats;
import com.example.store.dto.stats.CompanyProductStats;
import com.example.store.model.Status;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface CompanyService {

    ProductDTO2 getProductById(Long userId, Long productId);

    void updateProduct(Long userId, ProductUpdateDTO productUpdateDto);

    void createProduct(Long userId, ProductCreateDTO productDto);

    void addImagesToProduct(Long userId, Long productId, MultipartFile[] files);

    List<OrderCompanyShortDTO> getOrders(String authToken, Long userId);

    OrderCompanyLongDTO getOrderById(Long userId, Long orderId);

    void updateOrderStatus(Long userId, OrderStatusUpdateDTO orderStatusUpdateDTO);

    CompanyStats getCompanyStats(Long userId, LocalDateTime endDate);

    void retireProduct(Long userId, Long productId);

    Map<String, Integer> getSalesBySubCategories(Long userId, LocalDateTime startDate);

    List<AmountDate> getOrdersInTime(Long userId, LocalDateTime localDateTime);

    Map<LocalDate, BigDecimal> getIncomeInTime(Long userId, LocalDateTime localDateTime);

    List<ProductCompanyDTO> getProducts(Long userId);

    CompanyProductStats getProductStats(Long userId);

    Long getOrderOwnerUserIdByOrderId(Long orderId);
}
