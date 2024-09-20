package com.example.store.dto.order;

import com.example.store.model.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCompanyLongDTO(Long id,
                                  Status status,
                                  String city,
                                  String street,
                                  int houseNumber,
                                  String zipCode,
                                  int phone,
                                  BigDecimal fullPrice,
                                  LocalDateTime sentDate,
                                  List<OrderItemDTO> items,
                                  LocalDateTime dateOfOrder,
                                  String firstName,
                                  String lastName,
                                  String username) {

}
