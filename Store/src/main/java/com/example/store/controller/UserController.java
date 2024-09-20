package com.example.store.controller;

import com.example.store.dto.order.OrderCreateDTO;
import com.example.store.dto.order.OrderUserDTO;
import com.example.store.dto.product.ProductAvailability;
import com.example.store.dto.product.ProductReviewCreateDTO;
import com.example.store.dto.stats.UserSpendingStats;
import com.example.store.dto.user.UserDTO;
import com.example.store.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/order")
    public ResponseEntity<List<ProductAvailability>> createOrder(@RequestHeader("X-auth-user-id") Long userId, @RequestHeader("Authorization") String authToken, @Valid @RequestBody OrderCreateDTO order) {
        return new ResponseEntity<>(userService.createOrder(authToken, userId, order), HttpStatus.CREATED);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderUserDTO>> getOrders(@RequestHeader("X-auth-user-id") Long userId) {
        return new ResponseEntity<>(userService.getOrders(userId), HttpStatus.OK);
    }

    @PostMapping("/product/review")
    public ResponseEntity<Boolean> createProductReview(@RequestHeader("X-auth-user-id") Long userId, @Valid @RequestBody ProductReviewCreateDTO productReviewCreateDto) {
        userService.createProductReview(userId, productReviewCreateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/verify-purchase")
    public ResponseEntity<Boolean> checkIfUserBoughtProduct(@RequestHeader("X-auth-user-id") Long userId, @RequestParam Long productId) {
        return new ResponseEntity<>(userService.checkIfUserBoughtProduct(userId, productId), HttpStatus.OK);
    }

    @PutMapping("/photo")
    public ResponseEntity<Void> updateProfilePhoto(@RequestHeader("X-auth-user-id") Long userId, @RequestParam("file") MultipartFile file) {
        userService.updateProfilePhoto(userId, file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/photo")
    public ResponseEntity<?> getProfilePhoto(@RequestHeader("X-auth-user-id") Long userId) {
        return userService.getProfilePhoto(userId);
    }

    @GetMapping("/userImage")
    public ResponseEntity<?> getProfileImageByUserId(@RequestHeader("X-auth-user-id") Long userId, @RequestParam Long userId2) {
        return userService.getProfilePhoto(userId2);
    }

    @PutMapping("/order/{id}/complete")
    public ResponseEntity<Void> setOrderStatusCompleted(@RequestHeader("X-auth-user-id") Long userId, @PathVariable Long id) {
        userService.setOrderStatusCompleted(userId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/companies")
    public ResponseEntity<Set<Long>> getCompaniesByOrderId(@RequestParam Long orderId) {
        return new ResponseEntity<>(userService.getCompaniesByOrderId(orderId), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserSpendingStats> getStatsFromOrders(@RequestHeader("X-auth-user-id") Long userId) {
        return new ResponseEntity<>(userService.getStatsFromOrders(userId), HttpStatus.OK);
    }
    @GetMapping("/")
    public UserDTO getUser(@RequestHeader("Authorization") String authToken){
        return userService.getUser(authToken);
    }
}
