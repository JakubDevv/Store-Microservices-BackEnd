package com.example.store.repository;

import com.example.store.dto.category.CategoryUserExpenseDTO;
import com.example.store.dto.category.SubCategoryDTO;
import com.example.store.model.Product;
import com.example.store.model.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long>, PagingAndSortingRepository<SubCategory, Long> {

    @Query("SELECT p FROM SubCategory s JOIN s.products p LEFT JOIN p.reviews r where s.name=?1 GROUP BY p.id")
    Page<Product> getProductsBySubCategory(String category, Pageable pageable);

    @Query("SELECT new com.example.store.dto.category.SubCategoryDTO(s.id, s.name) FROM SubCategory s ORDER BY s.name asc")
    List<SubCategoryDTO> getSubCategories();

    @Query("SELECT NEW com.example.store.dto.category.CategoryUserExpenseDTO(s.name, " +
            "SUM(COALESCE(p.discount_price * si.quantity, p.price * si.quantity)), " +
            "CAST(ROUND(100.0 * SUM(COALESCE(p.discount_price * si.quantity, p.price * si.quantity)) / " +
            "(SELECT SUM(COALESCE(p2.discount_price * si2.quantity, p2.price * si2.quantity)) " +
            " FROM SubCategory s2 JOIN s2.products p2 JOIN p2.sizes si2 WHERE p2.companyid = ?1), 2) as double)) " +
            "FROM SubCategory s " +
            "RIGHT JOIN s.products p " +
            "RIGHT JOIN p.sizes si " +
            "WHERE p.companyid = ?1 " +
            "GROUP BY s.name " +
            "ORDER BY 2 DESC " +
            "LIMIT 3")
    List<CategoryUserExpenseDTO> getValueOfStoredProductsByCompanyId(Long companyId);

    Optional<SubCategory> findSubCategoryByProductsContaining(Product product);

    SubCategory getSubCategoryByProductsContaining(Product product);

    SubCategory getSubCategoryByName(String name);
}
