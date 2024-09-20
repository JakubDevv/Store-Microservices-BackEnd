package com.example.store.dto.user;

import com.example.store.dto.order.OrderUserAdminDTO;
import com.example.store.dto.order.OrderItemAdminDTO2;

import java.time.LocalDateTime;
import java.util.List;

public record UserAdminStatsDTO(Long id,
                                String firstName,
                                String lastName,
                                String userName,
                                LocalDateTime created,
                                LocalDateTime banned,
                                List<OrderUserAdminDTO> orders,
                                LocalDateTime lastOrder,
                                List<OrderItemAdminDTO2> products,
                                boolean photo) {


}
