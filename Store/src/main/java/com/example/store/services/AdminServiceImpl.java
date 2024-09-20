package com.example.store.services;

import com.example.store.dto.filters.FilterCreateDTO;
import com.example.store.dto.filters.FilterDTO;
import com.example.store.dto.filters.FilterValueCreateDTO;
import com.example.store.dto.filters.FilterValueDTO;
import com.example.store.dto.category.MainCategoryAdminDTO;
import com.example.store.dto.category.SubCategoryCreateDTO;
import com.example.store.dto.order.OrderAdminLongDTO;
import com.example.store.dto.order.OrderAdminShortDTO;
import com.example.store.dto.product.ProductAdminDTO;
import com.example.store.dto.product.ProductAdminStatsDTO;
import com.example.store.dto.stats.AdminStats;
import com.example.store.dto.stats.AmountDate;
import com.example.store.dto.stats.CategoryProductsQuantity;
import com.example.store.dto.user.*;
import com.example.store.exception.filter.FilterNotFoundException;
import com.example.store.exception.filtervalue.FilterValueNotFoundException;
import com.example.store.exception.maincategory.MainCategoryNotFoundException;
import com.example.store.exception.order.OrderNotFoundException;
import com.example.store.exception.product.ProductNotFoundException;
import com.example.store.exception.subcategory.SubcategoryNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.mapper.ProductMapper;
import com.example.store.model.*;
import com.example.store.repository.*;
import com.example.store.webclient.WebClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MainCategoryRepository mainCategoryRepository;
    private final FilterRepository filterRepository;
    private final FilterValueRepository filterValueRepository;
    private final ProductMapper productMapper;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WebClientService webClientService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<MainCategoryAdminDTO> getCategories() {
        return mainCategoryRepository.findAll().stream().map(productMapper::mapMainCategoryToMainCategoryAdminDTO).toList();
    }

    @Override
    public void createCategory(String category) {
        mainCategoryRepository.save(new MainCategory(category));
    }

    @Override
    public void createSubCategory(SubCategoryCreateDTO subCategoryCreateDTO) {
        MainCategory mainCategory = mainCategoryRepository.findById(subCategoryCreateDTO.mainCategoryId()).orElseThrow(() -> new MainCategoryNotFoundException(subCategoryCreateDTO.mainCategoryId()));
        SubCategory subCategory1 = new SubCategory(subCategoryCreateDTO.name());
        subCategoryRepository.save(subCategory1);
        mainCategory.getSubCategories().add(subCategory1);
        mainCategoryRepository.save(mainCategory);
    }

    @Override
    public void deleteFilter(Long id) {
        filterRepository.deleteById(id);
    }

    @Override
    public void deleteFilterValue(Long id) {
        FilterValue filterValue = filterValueRepository.findById(id).orElseThrow(() -> new FilterValueNotFoundException(id));
        filterValueRepository.delete(filterValue);
    }

    @Override
    public FilterValueDTO createFilterValue(FilterValueCreateDTO filterValueCreateDTO) {
        Filter filter = filterRepository.findById(filterValueCreateDTO.filterId()).orElseThrow(() -> new FilterNotFoundException(filterValueCreateDTO.filterId()));
        FilterValue filterValue1 = new FilterValue(filterValueCreateDTO.name());
        filterValueRepository.save(filterValue1);

        filter.getValues().add(filterValue1);
        filterRepository.save(filter);

        return productMapper.mapFilterValueToFilterValueDTO(filterValue1);
    }

    @Override
    public FilterDTO createFilter(FilterCreateDTO filterCreateDTO) {
        SubCategory subCategory = subCategoryRepository.findById(filterCreateDTO.subCategoryId()).orElseThrow(() -> new SubcategoryNotFoundException(filterCreateDTO.subCategoryId()));
        Filter filter = new Filter(filterCreateDTO.name());
        filterRepository.save(filter);

        subCategory.getFilters().add(filter);
        subCategoryRepository.save(subCategory);

        return productMapper.mapFilterToFilterDTO(filter);
    }

    @Override
    public void deleteCategoryById(Long id) {
        MainCategory category = mainCategoryRepository.findById(id).orElseThrow(() -> new MainCategoryNotFoundException(id));
        category.setDeleted(LocalDateTime.now());
        category.getSubCategories().forEach(subCategory -> deleteSubCategoryById(subCategory.getId()));
        mainCategoryRepository.save(category);
    }

    @Override
    public void deleteSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(() -> new SubcategoryNotFoundException(id));
        subCategory.setDeleted(LocalDateTime.now());
        List<Product> products = subCategory.getProducts();
        products.forEach(product -> product.setRetired(LocalDateTime.now()));
        productRepository.saveAll(products);
        subCategoryRepository.save(subCategory);
    }

    @Override
    public List<ProductAdminDTO> getProducts() {
        return productRepository.findAll().stream().map(productMapper::mapProductToProductAdminDTO).toList();
    }

    @Override
    public List<CategoryProductsQuantity> getAmountOfProductsByCategory() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories.stream().map(subCategory -> new CategoryProductsQuantity(subCategory.getName(), subCategory.getProducts().stream().filter(product -> product.getRetired() == null).toList().size())).toList();
    }

    @Override
    public List<CategoryProductsQuantity> getAmountOfProducts() {
        return mainCategoryRepository.findMainCategoriesByDeletedIsNull().stream().map(category -> new CategoryProductsQuantity(category.getName(), category.getSubCategories().stream().filter(subCategory -> subCategory.getDeleted() == null).mapToInt(subcategory -> subcategory.getProducts().stream().filter(product -> product.getRetired() == null).toList().size()).sum())).toList();
    }

    @Override
    public List<CategoryProductsQuantity> getSalesBySubCategory() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories.stream().map(subCategory -> new CategoryProductsQuantity(subCategory.getName(), subCategory.getProducts().stream().mapToInt(Product::getSales).sum())).toList();
    }

    @Override
    public List<AmountDate> getProductsInTime() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> product.getCreated().toLocalDate())
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AmountDate(Integer.parseInt(String.valueOf(entry.getValue())), entry.getKey()))
                .toList();
    }

    @Override
    public List<AmountDate> getOrdersInTime() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(product -> product.getStatuses().stream().filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime())
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AmountDate(Integer.parseInt(String.valueOf(entry.getValue())), entry.getKey().toLocalDate()))
                .toList();
    }

    @Override
    public List<OrderAdminShortDTO> getOrders() {
        return orderRepository.findAll().stream().map(orderMapper::mapOrderToOrderAdminShortDTO).toList();
    }

    @Override
    public OrderAdminLongDTO getOrderById(Long id) {
        return orderMapper.mapOrderToOrderAdminLongDTO(orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @Override
    public AdminStats getStats(LocalDate startDate, LocalDate endDate) {
        int amountOfNewProducts = productRepository.getAmountOfNewProducts(startDate.atStartOfDay(), endDate.atStartOfDay());
        int amountOfNewOrders = orderRepository.getQuantityOfUncompletedOrders(startDate.atStartOfDay(), endDate.atStartOfDay());
        BigDecimal moneyTurnoverInTime = orderRepository.getMoneyTurnoverInTime(startDate.atStartOfDay(), endDate.atStartOfDay());
        int amountOfSoldProducts = orderRepository.getAmountOfSoldProducts(startDate.atStartOfDay(), endDate.atStartOfDay());
        return new AdminStats(
                amountOfNewProducts,
                amountOfNewOrders,
                moneyTurnoverInTime,
                amountOfSoldProducts
        );
    }

    @Override
    public ProductAdminStatsDTO getProductStats(Long productId) {
        return productMapper.mapProductToProductAdminStatsDTO(productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId)));
    }

    @Override
    public List<CompanyLongDTO> getCompanies(String authToken) {
        return webClientService.findCompanies(authToken).stream().map(orderMapper::mapCompanyIdToCompanyLongDTO).toList();
    }

    @Override
    public UserAdminStatsDTO getUser(String authToken, Long userId) {
        List<Order> orders = orderRepository.findOrdersByUserid(userId);
        UserDTO user = webClientService.getUser(authToken, userId);

        return new UserAdminStatsDTO(
                userId,
                user.firstName(),
                user.lastName(),
                user.username(),
                user.created(),
                user.banned(),
                orders.stream().map(orderMapper::mapOrderToOrderUserAdminDTO).toList(),
                orders.stream()
                        .map(this::getLatestInProgressStatusTime)
                        .max(LocalDateTime::compareTo)
                        .orElse(null),
                orderRepository.getOrdersByUserIdAndStatus(userId, "COMPLETED").stream().flatMap(order -> order.getOrderItem().stream()).map(orderMapper::mapOrderItemToOrderItemAdminDTO2).toList(),
                user.photo()
        );
    }

    @Override
    public List<UserAdminDTO> getUsers(String authToken) {
        return webClientService.getUsers(authToken).stream().map(orderMapper::mapUserDtoToUserAdminDTO).toList();
    }

    @Override
    public List<AmountDate> getSalesInTime() {
        List<OrderItem> orderItems = orderItemRepository.findAll();

        return orderItems.stream()
                .map(orderItem -> {
                    LocalDateTime time = orderItem.getStatuses().stream().filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().get().getTime();
                    int quantity = orderItem.getQuantity();
                    return new AmountDate(quantity, time.toLocalDate());
                })
                .collect(Collectors.groupingBy(AmountDate::date, Collectors.summingInt(AmountDate::amount)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AmountDate(Integer.parseInt(String.valueOf(entry.getValue())), entry.getKey()))
                .toList();
    }

    @Override
    public void retireProductsByUserId(Long userId) {
        productRepository.retireProductsByCompanyId(userId);
    }

    @Override
    public void retireProductById(Long productId) {
        productRepository.retireProductById(productId);
    }

    @Override
    public void banCompanyById(String authToken, Long companyId) {
        webClientService.banCompany(authToken, companyId);
    }

    @Override
    public void banUserById(String authToken, Long userId) {
        webClientService.banUser(authToken, userId);
    }

    @Override
    public Map<LocalDate, Integer> getUsersInTime(String authToken) {
        return webClientService.getUsersInTime(authToken);
    }

    public LocalDateTime getLatestInProgressStatusTime(Order order) {
        Optional<LocalDateTime> latestInProgressTime = order.getStatuses().stream()
                .filter(orderStatus -> Status.IN_PROGRESS.equals(orderStatus.getStatus()))
                .map(OrderStatus::getTime)
                .max(LocalDateTime::compareTo);

        return latestInProgressTime.orElse(null);
    }


}
