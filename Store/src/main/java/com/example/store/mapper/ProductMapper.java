package com.example.store.mapper;

import com.example.store.dto.filters.FilterDTO;
import com.example.store.dto.filters.FilterValueDTO;
import com.example.store.dto.category.MainCategoryAdminDTO;
import com.example.store.dto.category.SubCategoryAdminDTO;
import com.example.store.dto.order.OrderItemAdminDTO;
import com.example.store.dto.parameter.ParameterDTO;
import com.example.store.dto.parameter.ParameterDTO2;
import com.example.store.dto.product.*;
import com.example.store.dto.size.SizeDTO;
import com.example.store.exception.subcategory.SubcategoryNotFoundException;
import com.example.store.model.*;
import com.example.store.repository.OrderItemRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.repository.SubCategoryRepository;
import com.example.store.webclient.WebClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductMapper {

    private final WebClientService webClientService;

    private final SubCategoryRepository subCategoryRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderMapper orderMapper;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public ProductDTO mapProductToProductDTO(Product product) {
        String companyName = webClientService.findCompanyName(product.getCompanyid()).name();

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscount_price())
                .companyName(companyName)
                .sizes(product.getSizes().stream().map(this::mapSizeToSizeDTO).toList())
                .parameters(product.getParameters().stream().map(this::mapParameterToParameterDTO2).toList())
                .images(product.getImages().size())
                .build();
    }

    public ProductDTO4 mapProductToProductDTO4(Product product) {

        return new ProductDTO4(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount_price(),
                (int) product.getReviews().stream().mapToInt(ProductReview::getRating).average().orElse(0.0),
                product.getImages().size()
        );
    }

    public ProductDTO2 mapProductToProductDTO2(Product product) {
        Set<Long> users = new HashSet<>();
        if(product.getSales() > 0){
            users = orderRepository.getOrdersByProduct(product).stream()
                    .map(Order::getUserid)
                    .collect(Collectors.toSet());
        }
        String category = subCategoryRepository.findSubCategoryByProductsContaining(product).orElseThrow(() -> new SubcategoryNotFoundException(Long.valueOf(0))).getName();

        return new ProductDTO2(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount_price(),
                product.getCreated(),
                product.getSales(),
                product.getParameters().stream().map(this::mapParameterToParameterDTO).toList(),
                product.getSizes().stream().map(this::mapSizeToSizeDTO).toList(),
                product.getImages().size(),
                product.getRetired(),
                users.size(),
                productRepository.getRatingByProduct(product),
                productRepository.getRatingOfProduct(product),
                productRepository.getValueOfStoredProduct(product),
                category
        );
    }

    public Size mapSizeDTOToSize(SizeDTO sizeDTO) {

        return new Size(
                sizeDTO.id(),
                sizeDTO.sizeValue(),
                sizeDTO.quantity()
        );
    }

    public Parameter mapParameterDTOToParameter(ParameterDTO parameterDTO) {

        return new Parameter(
                parameterDTO.id(),
                parameterDTO.key(),
                parameterDTO.value()
        );
    }

    public SizeDTO mapSizeToSizeDTO(Size size) {

        return SizeDTO.builder()
                .id(size.getId())
                .quantity(size.getQuantity())
                .sizeValue(size.getSizevalue())
                .build();
    }

    public ParameterDTO2 mapParameterToParameterDTO2(Parameter parameter) {

        return ParameterDTO2.builder()
                .key(parameter.getKey())
                .value(parameter.getValue())
                .build();
    }

    public ParameterDTO mapParameterToParameterDTO(Parameter parameter) {

        return new ParameterDTO(
                parameter.getId(),
                parameter.getKey(),
                parameter.getValue()
        );
    }

    public ProductDTO3 mapProductToProductDTO3(Product product) {

        return new ProductDTO3(
                product.getId(),
                product.getName(),
                webClientService.findCompanyName(product.getCompanyid()).name(),
                product.getSizes().stream().map(this::mapSizeToSizeDTO).toList(),
                product.getPrice(),
                product.getDiscount_price(),
                (int) product.getReviews().stream().mapToInt(ProductReview::getRating).average().orElse(0.0),
                product.getCreated(),
                product.getSales(),
                product.getImages().size(),
                subCategoryRepository.findSubCategoryByProductsContaining(product).orElseThrow(() -> new SubcategoryNotFoundException(Long.valueOf(0))).getName()
        );
    }


    public ProductReviewDTO mapProductReviewToProductReviewDTO(ProductReview productReview) {

        return new ProductReviewDTO(
                productReview.getId(),
                productReview.getMessage(),
                productReview.getRating(),
                webClientService.findUserName(productReview.getUserid()),
                productReview.getSendtime()
        );
    }


    public FilterValueDTO mapFilterValueToFilterValueDTO(FilterValue filterValue) {

        return new FilterValueDTO(
                filterValue.getId(),
                filterValue.getValue()
        );
    }

    public ProductAdminDTO mapProductToProductAdminDTO(Product product) {

        return new ProductAdminDTO(
                product.getId(),
                product.getName(),
                product.getDiscount_price() == null ? product.getPrice() : product.getDiscount_price(),
                product.getSales(),
                webClientService.findCompanyName(product.getCompanyid()).name(),
                product.getCreated(),
                product.getRetired(),
                subCategoryRepository.getSubCategoryByProductsContaining(product).getName(),
                orderItemRepository.findOrderItemsByProductId(product.getId()).stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }

    public ProductAdminStatsDTO mapProductToProductAdminStatsDTO(Product product){
        List<OrderItem> list =  orderItemRepository.findOrderItemsByProduct(product);
        BigDecimal totalPrice = list.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProductAdminStatsDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                subCategoryRepository.getSubCategoryByProductsContaining(product).getName(),
                product.getPrice(),
                product.getDiscount_price(),
                webClientService.findCompanyName(product.getCompanyid()).name(),
                product.getCreated(),
                totalPrice,
                product.getSales(),
                product.getRetired(),
                list.stream().map(orderMapper::mapOrderItemToOrderItemAdminDTO).sorted(Comparator.comparing(OrderItemAdminDTO::sendTime)).toList(),
                product.getImages().stream().map(Image::getName).toList(),
                product.getSizes().stream().map(this::mapSizeToSizeDTO).toList(),
                product.getParameters().stream().map(Parameter::getValue).toList()
        );
    }

    public MainCategoryAdminDTO mapMainCategoryToMainCategoryAdminDTO(MainCategory mainCategory){
        return new MainCategoryAdminDTO(
                mainCategory.getId(),
                mainCategory.getName(),
                mainCategory.getSubCategories().stream().mapToInt(subCategory -> subCategory.getProducts().size()).sum(),
                mainCategory.getSubCategories().stream().flatMap(subCategory -> subCategory.getProducts().stream()).mapToInt(Product::getSales).sum(),
                mainCategory.getDeleted(),
                mainCategory.getSubCategories().stream().map(this::mapSubCategoryToSubCategoryAdminDTO).toList()
        );
    }

    public SubCategoryAdminDTO mapSubCategoryToSubCategoryAdminDTO(SubCategory subCategory){
        return new SubCategoryAdminDTO(
                subCategory.getId(),
                subCategory.getName(),
                (int) subCategory.getProducts().stream().map(product -> product.getRetired() == null).count(),
                subCategory.getProducts().stream().mapToInt(Product::getSales).sum(),
                subCategory.getDeleted(),
                subCategory.getFilters().stream().map(this::mapFilterToFilterDTO).toList()
                );
    }

    public FilterDTO mapFilterToFilterDTO(Filter filter) {
        List<FilterValueDTO> filterValues = filter.getValues().stream().map(this::mapFilterValueToFilterValueDTO).toList();

        return new FilterDTO(
                filter.getId(),
                filter.getKey(),
                filterValues
        );
    }

}
