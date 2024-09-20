package com.example.store.dto.user;

import java.math.BigDecimal;
import java.util.Map;

public record TransactionCreateDTO(Long userId,
                                   BigDecimal price,
                                   Long orderId,
                                   Map<Long, BigDecimal> pricePerCompany) {

}
