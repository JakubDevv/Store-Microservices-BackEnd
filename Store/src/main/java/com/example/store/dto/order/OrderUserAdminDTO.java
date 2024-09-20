package com.example.store.dto.order;

import com.example.store.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderUserAdminDTO(Long id,
                                BigDecimal price,
                                String street,
                                String zip_code,
                                int apartment_number,
                                String city,
                                LocalDateTime created,
                                Status status) {

}
