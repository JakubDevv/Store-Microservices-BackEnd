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
import com.example.store.exception.order.OrderNotFoundException;
import com.example.store.exception.product.ProductNotFoundException;
import com.example.store.exception.subcategory.SubcategoryNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.mapper.ProductMapper;
import com.example.store.model.*;
import com.example.store.repository.*;
import com.example.store.webclient.WebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ParametersRepository parametersRepository;
    private final SizesRepository sizesRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ImageService imageService;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final ImageRepository imageRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final WebClientService webClientService;

    @Override
    public ProductDTO2 getProductById(Long userId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.mapProductToProductDTO2(product);
    }

    @Override
    public void updateProduct(Long userId, ProductUpdateDTO productUpdateDto) {
        Product product = productRepository.getProductByIdAndCompanyId(productUpdateDto.id(), userId).orElseThrow(() -> new ProductNotFoundException(productUpdateDto.id()));
        List<Size> newSizes = productUpdateDto.sizes().stream().map(productMapper::mapSizeDTOToSize).collect(Collectors.toList());
        List<Parameter> newParameters = productUpdateDto.parameters().stream().map(productMapper::mapParameterDTOToParameter).collect(Collectors.toList());
        parametersRepository.saveAll(newParameters);
        sizesRepository.saveAll(newSizes);
        product.setSizes(newSizes);
        product.setParameters(newParameters);
        product.setName(productUpdateDto.title());
        product.setDescription(productUpdateDto.description());
        product.setPrice(productUpdateDto.price());
        product.setDiscount_price(productUpdateDto.discountPrice());
        productRepository.save(product);
    }

    @Override
    public void createProduct(Long userId, ProductCreateDTO productDto) {
        SubCategory subCategory = subCategoryRepository.findById(productDto.subcategoryId()).orElseThrow(() -> new SubcategoryNotFoundException(productDto.subcategoryId()));
        List<Size> sizes = productDto.sizes().stream().map(productMapper::mapSizeDTOToSize).toList();
        List<Parameter> parameters = productDto.parameters().stream().map(productMapper::mapParameterDTOToParameter).toList();
        sizesRepository.saveAll(sizes);
        parametersRepository.saveAll(parameters);
        Image image = new Image(UUID.randomUUID().toString());
        imageRepository.save(image);
        Product product = new Product(productDto.title(), productDto.description(), productDto.price(), sizes, userId, parameters);
        product.setImages(List.of(image));
        subCategory.addProduct(product);
        subCategoryRepository.save(subCategory);
    }

    @Override
    public void addImagesToProduct(Long userId, Long productId, MultipartFile[] files) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        imageService.deleteImagesByProduct(product);
        imageService.addMultipleFiles(files, product);
    }

    @Override
    public List<OrderCompanyShortDTO> getOrders(String authToken, Long userId) {
        List<Order> orders = orderRepository.getOrdersByCompanyId(webClientService.findCompanyIdByUserId(userId));
        return orders.stream().map(order -> orderMapper.mapOrderToOrderCompanyShortDTO(order, userId, authToken)).toList();
    }

    @Override
    public OrderCompanyLongDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.mapOrderToOrderCompanyLongDTO(order, userId);
    }

    @Override
    public void updateOrderStatus(Long userId, OrderStatusUpdateDTO orderStatusUpdateDTO) {
        Order order = orderRepository.findById(orderStatusUpdateDTO.orderId()).orElseThrow(() -> new OrderNotFoundException(orderStatusUpdateDTO.orderId()));
        order.getOrderItem().stream()
                .filter(item -> webClientService.getUserId(item.getProduct().getCompanyid()) == userId)
                .forEach(item -> {
                    OrderStatus orderStatus = new OrderStatus(orderStatusUpdateDTO.status());
                    orderStatusRepository.save(orderStatus);
                    item.getStatuses().add(orderStatus);
                });
        if (order.getOrderItem().stream().allMatch(item -> item.getStatuses().stream()
                .anyMatch(status -> status.getStatus().equals(Status.SENT)))) {
            OrderStatus orderStatus = new OrderStatus(Status.SENT);
            orderStatusRepository.save(orderStatus);
            order.getStatuses().add(orderStatus);
        }
        orderItemRepository.saveAll(order.getOrderItem());
        orderRepository.save(order);
    }


    @Override
    public CompanyStats getCompanyStats(Long userId, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        long betweenDays = ChronoUnit.DAYS.between(now, endDate);
        LocalDateTime secondDate = endDate.plusDays(betweenDays);

        List<OrderItem> items = orderRepository.getOrdersByCompanyIdAndEndDate(webClientService.findCompanyIdByUserId(userId), endDate).stream().flatMap(order -> order.getOrderItem().stream()).filter(item -> item.getProduct().getCompanyid() == webClientService.findCompanyIdByUserId(userId)).toList();
        List<OrderItem> items2 = orderRepository.getOrdersByCompanyIdAndStartDateAndEndDate(webClientService.findCompanyIdByUserId(userId), endDate, secondDate).stream().flatMap(order -> order.getOrderItem().stream()).filter(item -> item.getProduct().getCompanyid() == webClientService.findCompanyIdByUserId(userId)).toList();

        BigDecimal totalAmount = items.stream().map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount2 = items2.stream().map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        double multiply = totalAmount2.equals(BigDecimal.ZERO) ? 0 : ((totalAmount.doubleValue() - totalAmount2.doubleValue()) / totalAmount2.doubleValue()) * 100;

        int totalQuantity = items.stream().mapToInt(OrderItem::getQuantity).sum();
        int totalQuantity2 = items2.stream().mapToInt(OrderItem::getQuantity).sum();

        List<ProductReview> reviewCount = productRepository.findProductReviewsByCompanyIdAndTime(webClientService.findCompanyIdByUserId(userId), endDate);
        List<ProductReview> reviewCount2 = productRepository.findProductReviewsByCompanyIdAndTime(webClientService.findCompanyIdByUserId(userId), secondDate, endDate);

        int orderCount = orderRepository.getOrdersByCompanyIdAndEndDate(webClientService.findCompanyIdByUserId(userId), endDate).size();
        int orderCount2 = orderRepository.getOrdersByCompanyIdAndStartDateAndEndDate(webClientService.findCompanyIdByUserId(userId), endDate, secondDate).size();

        return new CompanyStats(orderCount,
                totalAmount,
                totalQuantity,
                reviewCount.size(),
                reviewCount.size() - reviewCount2.size(),
                totalQuantity - totalQuantity2,
                multiply,
                orderCount - orderCount2);
    }

    @Override
    public void retireProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getCompanyid() == webClientService.findCompanyIdByUserId(userId)) {
            productRepository.retireProductById(productId);
        }
    }

    @Override
    public Map<String, Integer> getSalesBySubCategories(Long userId, LocalDateTime startDate) {
        List<Order> orders = orderRepository.getOrdersByCompanyIdAndEndDate(webClientService.findCompanyIdByUserId(userId), startDate);

        return orders.stream()
                .flatMap(order -> order.getOrderItem().stream())
                .collect(Collectors.toMap(
                        item -> subCategoryRepository.findSubCategoryByProductsContaining(item.getProduct()).orElseThrow(SubcategoryNotFoundException::new).getName(),
                        OrderItem::getQuantity,
                        Integer::sum));
    }

    @Override
    public List<AmountDate> getOrdersInTime(Long userId, LocalDateTime localDateTime) {
        List<Order> ordersBySellerId3 = orderRepository.getOrdersByCompanyIdAndEndDate(webClientService.findCompanyIdByUserId(userId), localDateTime);
        return ordersBySellerId3.stream().map(order -> new AmountDate(1, order.getStatuses().stream().filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).toList().get(0).getTime().toLocalDate())).toList();
    }

    @Override
    public Map<LocalDate, BigDecimal> getIncomeInTime(Long userId, LocalDateTime localDateTime) {
        return orderRepository.getOrdersByCompanyIdAndEndDate(webClientService.findCompanyIdByUserId(userId), localDateTime)
                .stream()
                .flatMap(order -> order.getOrderItem().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getStatuses()
                                .stream()
                                .filter(status -> status.getStatus().equals(Status.IN_PROGRESS))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No IN_PROGRESS status found"))
                                .getTime()
                                .toLocalDate(),
                        Collectors.mapping(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    @Override
    public List<ProductCompanyDTO> getProducts(Long userId) {
        Long companyId = webClientService.findCompanyIdByUserId(userId);
        List<Product> products = productRepository.findProductsByCompanyid(companyId);
        return products.stream().map(product -> {
            SubCategory subCategory = subCategoryRepository.getSubCategoryByProductsContaining(product);
            List<OrderItem> items = orderItemRepository.findOrderItemsByProduct(product);
            int sales = items.stream().mapToInt(OrderItem::getQuantity).sum();
            BigDecimal income = items.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            int sum = product.getSizes().stream().mapToInt(Size::getQuantity).sum();
            return new ProductCompanyDTO(product.getId(), product.getCreated(), product.getName(), subCategory.getName(), subCategory.getDeleted() != null, sales, sum, product.getPrice(), product.getDiscount_price(), income, product.getRetired() == null);
        }).toList();
    }

    @Override
    public CompanyProductStats getProductStats(Long userId) {
        Long companyId = webClientService.findCompanyIdByUserId(userId);
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByProductCompanyid(userId);
        BigDecimal overallSpend = orderItems.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Order> orders = orderRepository.getOrdersByCompanyId(companyId);
        Set<Long> clients = orders.stream()
                .map(Order::getUserid)
                .collect(Collectors.toSet());

        return new CompanyProductStats(overallSpend,
                productRepository.getAmountOfActiveProducts(companyId),
                productRepository.getAmountOfRetiredProducts(companyId),
                clients.size(),
                productRepository.getRatingByCompanyId(companyId),
                productRepository.getValueOfStoredProductsByCompanyId(companyId),
                subCategoryRepository.getValueOfStoredProductsByCompanyId(companyId));
    }

    @Override
    public Long getOrderOwnerUserIdByOrderId(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId)).getUserid();
    }
}
