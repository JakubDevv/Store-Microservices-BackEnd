package com.example.store.dto.order;

import jakarta.validation.constraints.Min;

public record OrderItemCreateDTO(@Min(1) Long productId,
                                 @Min(1) Long sizeId,
                                 @Min(1) int quantity) {

}
