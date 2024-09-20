package com.example.store.dto.product;

import com.example.store.dto.parameter.ParameterDTO2;
import com.example.store.dto.size.SizeDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductDTO(Long id,
                         String name,
                         String description,
                         BigDecimal price,
                         BigDecimal discountPrice,
                         String companyName,
                         List<SizeDTO> sizes,
                         List<ParameterDTO2> parameters,
                         int images) {

}
