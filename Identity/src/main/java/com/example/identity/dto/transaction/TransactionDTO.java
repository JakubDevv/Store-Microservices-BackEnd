package com.example.identity.dto.transaction;

import com.example.identity.models.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransactionDTO(Long id,
                            BigDecimal amount,
                            Long orderId,
                            LocalDateTime date,
                            Type type,
                            List<String> names) {


}
