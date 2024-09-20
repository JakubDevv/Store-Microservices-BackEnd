package com.example.store.dto.category;

import com.example.store.dto.filters.FilterDTO;

import java.time.LocalDateTime;
import java.util.List;

public record SubCategoryAdminDTO(Long id,
                                  String name,
                                  int products,
                                  int sales,
                                  LocalDateTime deleted,
                                  List<FilterDTO> filters) {

}
