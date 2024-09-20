package com.example.store.services;

import com.example.store.dto.category.CategoryUserExpenseDTO;
import com.example.store.dto.user.TransactionCreateDTO;
import com.example.store.dto.order.OrderCreateDTO;
import com.example.store.dto.order.OrderItemCreateDTO;
import com.example.store.dto.order.OrderUserDTO;
import com.example.store.dto.product.ProductAvailability;
import com.example.store.dto.product.ProductReviewCreateDTO;
import com.example.store.dto.stats.UserSpendingStats;
import com.example.store.dto.user.UserDTO;
import com.example.store.exception.order.OrderNotFoundException;
import com.example.store.exception.product.ProductNotFoundException;
import com.example.store.exception.product.SizeNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.model.*;
import com.example.store.repository.*;
import com.example.store.s3.S3Service;
import com.example.store.webclient.WebClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SizesRepository sizesRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductReviewRepository productReviewRepository;
    private final S3Service s3Service;
    private final OrderMapper orderMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final WebClientService webClientService;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public UserDTO getUser(String authToken) {
        return webClientService.getUser(authToken);
    }

    @Override
    public List<ProductAvailability> createOrder(String authToken, Long userId, OrderCreateDTO order) {
        List<ProductAvailability> availabilities = new ArrayList<>();
        for (OrderItemCreateDTO productId : order.items()) {
            Size size = sizesRepository.findById(productId.sizeId()).orElseThrow(() -> new SizeNotFoundException(productId.sizeId()));
            if (size.getQuantity() < productId.quantity()) {
                availabilities.add(new ProductAvailability(productId.sizeId(), size.getQuantity()));
            }
        }
        if (availabilities.isEmpty()) {
            proceedOrder(authToken, userId, order);
        }
        return availabilities;
    }

    @Override
    public void createProductReview(Long userId, ProductReviewCreateDTO productReviewCreateDto) {
        ProductReview productReview = new ProductReview(productReviewCreateDto.message(), productReviewCreateDto.rating(), userId);
        productReviewRepository.save(productReview);
        Product product = productRepository.findById(productReviewCreateDto.productId()).orElseThrow(() -> new ProductNotFoundException(productReviewCreateDto.productId()));
        product.addReview(productReview);
        productRepository.save(product);
    }

    @Override
    public boolean checkIfUserBoughtProduct(Long userId, Long productId) {
        return orderItemRepository.findOrderItemsByProductCompanyid(userId).stream().anyMatch(item -> item.getProduct().getId() == productId);
    }

    @Override
    public ResponseEntity<?> getProfilePhoto(Long userId) {
//        try {
//            byte[] image = s3Service.getObject(
//                    "myapp2",
//                    "profilePhoto/" + userId
//            );
//
//            return ResponseEntity.status(HttpStatus.OK)
//                    .contentType(MediaType.valueOf("image/png"))
//                    .body(image);
//        } catch (IOException e) {
//            throw new IllegalArgumentException();
//        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public void updateProfilePhoto(Long userId, MultipartFile file) {
        try {
            s3Service.putObject(
                    "myapp2",
                    "profilePhoto/" + userId,
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<OrderUserDTO> getOrders(Long userId) {
        return orderRepository.findOrdersByUserid(userId)
                .stream()
                .map(orderMapper::mapOrderToOrderUserDTO)
                .sorted(Comparator.comparing(OrderUserDTO::sendTime).reversed())
                .toList();
    }

    @Transactional
    public void proceedOrder(String authToken, Long userId, OrderCreateDTO orderCreateDTO) {
        BigDecimal balance = webClientService.getBalance(authToken);
        if (balance.compareTo(calculateOrderPrice(orderCreateDTO)) >= 0) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            Order order = new Order(userId, orderCreateDTO.city(), orderCreateDTO.street(), orderCreateDTO.addressNumber(), orderCreateDTO.zip_code(), orderCreateDTO.phone());
            BigDecimal fullPrice = new BigDecimal(0);
            HashMap<Long, BigDecimal> map = new HashMap<>();
            for (OrderItemCreateDTO productId : orderCreateDTO.items()) {
                BigDecimal multiply;
                Size size = sizesRepository.findById(productId.sizeId()).orElseThrow(() -> new SizeNotFoundException(productId.sizeId()));
                Product product = productRepository.findById(productId.productId()).orElseThrow(() -> new ProductNotFoundException(productId.productId()));
                BigDecimal actualPrice = product.getDiscount_price() == null ? product.getPrice() : product.getDiscount_price();
                if (map.containsKey(product.getCompanyid())) {
                    BigDecimal bigDecimal = map.get(product.getCompanyid());
                    map.replace(product.getCompanyid(), bigDecimal.add(actualPrice.multiply(new BigDecimal(productId.quantity()))));
                } else {
                    map.put(product.getCompanyid(), actualPrice.multiply(new BigDecimal(productId.quantity())));
                }
                fullPrice = fullPrice.add(actualPrice.multiply(new BigDecimal(productId.quantity())));
                OrderItem orderItem = new OrderItem(productId.quantity(), userId);
                orderItem.setSize(size.getSizevalue());
                orderItem.setProduct(product);
                orderItem.setPrice(actualPrice);
                multiply = actualPrice.multiply(BigDecimal.valueOf(productId.quantity()));
                OrderStatus orderStatus = new OrderStatus(Status.IN_PROGRESS);
                orderStatusRepository.save(orderStatus);
                orderItem.setStatuses(List.of(orderStatus));
                orderItemRepository.save(orderItem);
                order.addOrderItem(orderItem);
                totalPrice = totalPrice.add(multiply);
                size.setQuantity(size.getQuantity() - productId.quantity());
                sizesRepository.save(size);
            }
            OrderStatus orderStatus = new OrderStatus(Status.IN_PROGRESS);
            orderStatusRepository.save(orderStatus);
            order.setStatuses(List.of(orderStatus));
            orderRepository.save(order);
            TransactionCreateDTO transaction = new TransactionCreateDTO(userId, fullPrice, order.getId(), map);
            webClientService.createTransaction(authToken, transaction);
        } else {
            throw new RuntimeException();
        }
    }

    private BigDecimal calculateOrderPrice(OrderCreateDTO orderCreateDTO) {
        return orderCreateDTO.items().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.productId()).orElseThrow(() -> new ProductNotFoundException(item.productId()));
                    BigDecimal actualPrice = product.getDiscount_price() == null ? product.getPrice() : product.getDiscount_price();
                    return actualPrice.multiply(new BigDecimal(item.quantity()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void setOrderStatusCompleted(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (userId == order.getUserid()) {

            order.getOrderItem().forEach(item -> {
                OrderStatus orderStatus = new OrderStatus(Status.COMPLETED);
                orderStatusRepository.save(orderStatus);
                item.getStatuses().add(orderStatus);
                orderItemRepository.save(item);
            });

            OrderStatus orderStatus = new OrderStatus(Status.COMPLETED);
            orderStatusRepository.save(orderStatus);
            order.getStatuses().add(orderStatus);
            orderRepository.save(order);
        }
    }

    @Override
    public Set<Long> getCompaniesByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId))
                .getOrderItem()
                .stream()
                .map(item -> item.getProduct().getCompanyid())
                .collect(Collectors.toSet());
    }

    @Override
    public UserSpendingStats getStatsFromOrders(Long userId) {
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        long between = ChronoUnit.MONTHS.between(LocalDate.of(2022, 1, 1), LocalDateTime.now());

        BigDecimal moneyTurnover = orderRepository.getMoneyTurnoverByUserId(userId);
        List<OrderItem> items = orderRepository.findOrdersByUserid(userId).stream().flatMap(order -> order.getOrderItem().stream()).toList();
        items.forEach(item -> {
            SubCategory subCategory = subCategoryRepository.getSubCategoryByProductsContaining(item.getProduct());
            expenseByCategory.compute(subCategory.getName(), (key, oldValue) ->
                    (oldValue == null ? BigDecimal.ZERO : oldValue).add(item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            );
        });

        List<Map.Entry<String, BigDecimal>> sortedEntries = expenseByCategory.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        List<Map.Entry<String, BigDecimal>> topThreeEntries = sortedEntries.subList(0, Math.min(3, sortedEntries.size()));

        List<CategoryUserExpenseDTO> list = topThreeEntries.stream().map(category -> new CategoryUserExpenseDTO(category.getKey(), category.getValue(), category.getValue().divide(moneyTurnover, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).intValue())).toList();

        return new UserSpendingStats(
                orderRepository.calculateMoneySpentByUserIdInDays(userId, LocalDateTime.now().minusMonths(6)),
                orderRepository.calculateAverageMoneySpentByUserIdAndMonths(userId, (int) between),
                orderRepository.calculateMoneySpentByUserId(userId, LocalDateTime.now().minusMonths(6)),
                orderRepository.calculateMoneySpentByUserId(userId, LocalDateTime.now().minusYears(1)),
                list
        );
    }

    private OrderItem createOrderItem(Long userId, OrderItemCreateDTO productId, Size size, Product product, BigDecimal actualPrice) {
        OrderItem orderItem = new OrderItem(productId.quantity(), userId);
        orderItem.setSize(size.getSizevalue());
        orderItem.setProduct(product);
        orderItem.setPrice(actualPrice);
        orderItemRepository.save(orderItem);
        return orderItem;
    }

    private void createAndSaveOrderStatus(OrderItem orderItem) {
        OrderStatus orderStatus = new OrderStatus(Status.IN_PROGRESS);
        orderStatusRepository.save(orderStatus);
        orderItem.setStatuses(List.of(orderStatus));
    }

    private void saveOrderAndTransaction(Order order, BigDecimal totalOrderPrice, String authToken, Long userId, Map<Long, BigDecimal> companyPriceMap) {
        OrderStatus orderStatus = new OrderStatus(Status.IN_PROGRESS);
        orderStatusRepository.save(orderStatus);
        order.setStatuses(List.of(orderStatus));

        orderRepository.save(order);

        TransactionCreateDTO transaction = new TransactionCreateDTO(userId, totalOrderPrice, order.getId(), companyPriceMap);
        webClientService.createTransaction(authToken, transaction);
    }
}
