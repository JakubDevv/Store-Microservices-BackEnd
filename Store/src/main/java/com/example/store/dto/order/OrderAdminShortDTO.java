package com.example.store.dto.order;

import com.example.store.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderAdminShortDTO(Long id,
                                 String username,
                                 BigDecimal price,
                                 LocalDateTime sendtime,
                                 Status status,
                                 List<String> companies,
                                 String address,
                                 int items) {

}
