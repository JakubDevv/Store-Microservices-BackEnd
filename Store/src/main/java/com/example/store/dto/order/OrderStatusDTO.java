package com.example.store.dto.order;

import com.example.store.model.Status;

import java.time.LocalDateTime;

public record OrderStatusDTO(Status status,
                             LocalDateTime time) {

}
