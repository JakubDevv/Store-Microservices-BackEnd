package com.example.store.dto.product;

import com.example.store.dto.size.SizeDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDTO3(Long id,
                          String title,
                          String companyName,
                          List<SizeDTO> sizeList,
                          BigDecimal price,
                          BigDecimal discountPrice,
                          double starsRate,
                          @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime premiere,
                          int sales,
                          int amountOfImages,
                          String category) implements Serializable {

}
