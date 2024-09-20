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
import com.example.store.dto.user.CompanyLongDTO;
import com.example.store.dto.user.UserAdminDTO;
import com.example.store.dto.user.UserAdminStatsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {

    List<MainCategoryAdminDTO> getCategories();

    void createCategory(String category);

    void createSubCategory(SubCategoryCreateDTO subCategoryCreateDTO);

    void deleteFilter(Long id);

    void deleteFilterValue(Long id);

    FilterValueDTO createFilterValue(FilterValueCreateDTO filterValueCreateDTO);

    FilterDTO createFilter(FilterCreateDTO filterCreateDTO);

    void deleteCategoryById(Long id);

    void deleteSubCategoryById(Long id);

    List<ProductAdminDTO> getProducts();

    List<CategoryProductsQuantity> getAmountOfProductsByCategory();

    List<CategoryProductsQuantity> getAmountOfProducts();

    List<CategoryProductsQuantity> getSalesBySubCategory();

    List<AmountDate> getProductsInTime();

    List<AmountDate> getOrdersInTime();

    List<OrderAdminShortDTO> getOrders();

    OrderAdminLongDTO getOrderById(Long id);

    AdminStats getStats(LocalDate startDate, LocalDate endDate);

    ProductAdminStatsDTO getProductStats(Long productId);

    List<CompanyLongDTO> getCompanies(String authToken);

    UserAdminStatsDTO getUser(String authToken, Long userId);

    List<UserAdminDTO> getUsers(String authToken);

    List<AmountDate> getSalesInTime();

    void retireProductsByUserId(Long userId);

    void retireProductById(Long productId);

    void banCompanyById(String authToken, Long companyId);

    void banUserById(String authToken, Long userId);

    Map<LocalDate, Integer> getUsersInTime(String authToken);

}
