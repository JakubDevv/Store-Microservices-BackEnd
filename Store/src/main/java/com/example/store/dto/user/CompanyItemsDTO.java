package com.example.store.dto.user;

import com.example.store.dto.order.OrderItemAdminDTO2;
import com.example.store.dto.order.OrderStatusDTO;

import java.util.List;

public record CompanyItemsDTO(List<OrderStatusDTO> statuses,
                              String companyName,
                              List<OrderItemAdminDTO2> items) {

}
