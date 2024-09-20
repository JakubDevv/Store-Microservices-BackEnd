package com.example.store.dto.order;

import com.example.store.model.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCompanyShortDTO(Long id,
                                   int quantity,
                                   @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime date,
                                   BigDecimal price,
                                   Status status,
                                   String firstName,
                                   String lastName,
                                   String city,
                                   String street,
                                   int apartment_num,
                                   int items,
                                   Long userId
) {

}
