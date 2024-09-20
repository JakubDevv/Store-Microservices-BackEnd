package com.example.store.services;

import com.example.store.dto.category.MainCategoryDTO;
import com.example.store.dto.category.SubCategoryDTO;
import com.example.store.dto.filters.FilterDTO;
import com.example.store.dto.product.*;
import com.example.store.dto.stats.HomeStats;
import com.example.store.model.Filter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface ProductsService {

    ResponseEntity<?> getProductPhoto(Long productId, int photoNumber);

    ResponseEntity<?> findProductImageByKey(Long productId, String key);

    List<SubCategoryDTO> getSubCategories();

    List<ProductDTO3> getProductsSortedByRating();

    ProductRatingDTO calculateProductRating(Long productId);

    ProductDTO getProductById(Long productId);

    Page<ProductDTO3> getFormattedProductsByQuery(String query, int page);

    List<MainCategoryDTO> getCategories();

    List<FilterDTO> getFiltersForSubcategory(String category);

    Page<ProductDTO3> getNewProducts(int page);

    Page<ProductDTO3> getProductsByCategory(String category, Integer page);

    Page<ProductDTO3> getFilteredProducts(String category, Map<String, String> allFilters, BigDecimal from, BigDecimal to);

    Page<ProductReviewDTO> getProductReviews(Long id, int pageNum);

    List<ProductDTO4> getProductsSortedBySales();

    Page<ProductDTO3> getDiscountedProducts(Integer pageNum);

    HomeStats getStats();
}
