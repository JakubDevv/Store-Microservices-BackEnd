package com.example.store.dto.product;

import com.example.store.dto.parameter.ParameterDTO;
import com.example.store.dto.size.SizeDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateDTO(@Min(1) Long id,
                               @NotBlank String title,
                               @NotBlank @Size(max = 255) String description,
                               @Positive BigDecimal price,
                               @Positive BigDecimal discountPrice,
                               List<ParameterDTO> parameters,
                               List<SizeDTO> sizes) {

}
