package com.example.identity.dto.transaction;

import java.math.BigDecimal;
import java.util.Map;

public record TransactionCreateDTO(Long userId,
                                   BigDecimal price,
                                   Long orderId,
                                   Map<Long, BigDecimal> pricePerCompany) {


}
