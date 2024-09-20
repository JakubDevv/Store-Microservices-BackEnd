package com.example.identity.dto.user;

import java.time.LocalDateTime;

public record CompanyDTO(Long id,
                         String name,
                         LocalDateTime created,
                         LocalDateTime banned) {


}
