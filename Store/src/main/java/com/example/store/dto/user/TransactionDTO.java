package com.example.store.dto.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransactionDTO(int id,
                             BigDecimal amount,
                             int orderId,
                             LocalDateTime date,
                             String type,
                             List<String> names) {


}
