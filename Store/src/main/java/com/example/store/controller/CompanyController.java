package com.example.store.controller;

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
import com.example.store.services.CompanyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductCompanyDTO>> getProducts(@RequestHeader("X-auth-user-id") Long userId) {
        return new ResponseEntity<>(companyService.getProducts(userId), HttpStatus.OK);
    }

    @GetMapping("/product")
    public ResponseEntity<ProductDTO2> getProductById(@RequestHeader("X-auth-user-id") Long userId, @Min(1) @RequestParam Long productId) {
        return new ResponseEntity<>(companyService.getProductById(userId, productId), HttpStatus.OK);
    }

    @PutMapping("/product")
    public ResponseEntity<Void> updateProduct(@RequestHeader("X-auth-user-id") Long userId, @Valid @RequestBody ProductUpdateDTO productUpdateDto) {
        companyService.updateProduct(userId, productUpdateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/product")
    public ResponseEntity<Void> createProduct(@RequestHeader("X-auth-user-id") Long userId, @Valid @RequestBody ProductCreateDTO productDto) {
        companyService.createProduct(userId, productDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/product/images")
    public ResponseEntity<Void> addImagesToProduct(@RequestHeader("X-auth-user-id") Long userId, @Min(1) @RequestParam("productId") Long productId, @RequestParam("file") MultipartFile[] files) {
        companyService.addImagesToProduct(userId, productId, files);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderCompanyShortDTO>> getOrders(@RequestHeader("X-auth-user-id") Long userId, @RequestHeader("Authorization") String authToken) {
        return new ResponseEntity<>(companyService.getOrders(authToken, userId), HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<OrderCompanyLongDTO> getOrderById(@RequestHeader("X-auth-user-id") Long userId, @Min(1) @RequestParam Long orderId) {
        return new ResponseEntity<>(companyService.getOrderById(userId, orderId), HttpStatus.OK);
    }

    @PutMapping("/order/status")
    public ResponseEntity<Void> updateOrderStatus(@RequestHeader("X-auth-user-id") Long userId, @Valid @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO) {
        companyService.updateOrderStatus(userId, orderStatusUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/stats")
    public ResponseEntity<CompanyStats> getCompanyStats(@RequestHeader("X-auth-user-id") Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getCompanyStats(userId, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/product/stats")
    public ResponseEntity<CompanyProductStats> getProductStats(@RequestHeader("X-auth-user-id") Long userId) {
        return new ResponseEntity<>(companyService.getProductStats(userId), HttpStatus.OK);
    }

    @PutMapping("/product/retire")
    public ResponseEntity<Void> retireProduct(@RequestHeader("X-auth-user-id") Long userId, @Min(1) @RequestParam Long productId) {
        companyService.retireProduct(userId, productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/sales/category")
    public ResponseEntity<Map<String, Integer>> getSalesBySubCategories(@RequestHeader("X-auth-user-id") Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getSalesBySubCategories(userId, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/orders/time")
    public ResponseEntity<List<AmountDate>> getOrdersInTime(@RequestHeader("X-auth-user-id") Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getOrdersInTime(userId, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/income/time")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getIncomeInTime(@RequestHeader("X-auth-user-id") Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getIncomeInTime(userId, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/userId")
    public ResponseEntity<Long> getOrderOwnerUserIdByOrderId(@RequestParam Long orderId) {
        return new ResponseEntity<>(companyService.getOrderOwnerUserIdByOrderId(orderId), HttpStatus.OK);
    }
}