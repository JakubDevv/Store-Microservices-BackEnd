package com.example.store.dto.order;

import com.example.store.model.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDTO(@Min(1) Long orderId,
                                   @NotNull Status status) {
}
