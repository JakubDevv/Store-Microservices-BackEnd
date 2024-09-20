package com.example.store.dto.order;

import java.math.BigDecimal;

public record OrderItemUserDTO(Long id,
                               String title,
                               String size,
                               BigDecimal price,
                               int quantity,
                               Long productId,
                               String companyName) {

}
