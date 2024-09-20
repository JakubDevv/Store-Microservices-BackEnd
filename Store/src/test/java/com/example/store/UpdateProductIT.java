//package com.example.products;
//
//import com.example.store.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//public class UpdateProductIT extends BaseIT {
//
//    @Autowired
//    private ProductRepository productRepository;
//
////    @Test
////    @Order(1)
////    void addProduct_() throws Exception {
////        ProductCreateDTO addProduct = createTestAddProduct();
////        String jsonRequest = objectMapper.writeValueAsString(addProduct);
////        mockMvc.perform(post("/seller/addProduct")
////                        .header("X-auth-user-id",1)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(jsonRequest))
////                .andExpect(status().isOk());
////    }
////
////    @Test
////    @Order(2)
////    void updateProductById_changeEveryValue_whenValuesChangedTrue() throws Exception {
////
////        ProductUpdateDto product = createTestProductDto();
////
////        String jsonRequest = objectMapper.writeValueAsString(product);
////        mockMvc.perform(put("/seller/updateProduct")
////                        .header("X-auth-user-id",1)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(jsonRequest))
////                .andExpect(status().isOk());
////
////        mockMvc.perform(get("/products/product/1"))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.name").value(createTestProductDto().title()))
////                .andExpect(jsonPath("$.description").value(createTestProductDto().description()))
////                .andExpect(jsonPath("$.price").value(createTestProductDto().price()))
////                .andExpect(jsonPath("$.discountPrice").value(createTestProductDto().discountPrice()))
////                .andExpect(jsonPath("$.sellerId").value(1))
////                .andExpect(jsonPath("$.sizes").value(createTestProductDto().sizes()))
////                .andExpect(jsonPath("$.parameters").value(createTestProductDto().parameters()));
////    }
//
//
////    private ProductCreateDTO createTestAddProduct(){
////        ParameterDto parameterDto = new ParameterDto("Condition", "New");
////        SizeDto size1 = new SizeDto("S",5);
////        SizeDto size2 = new SizeDto("M",15);
////        return new ProductCreateDTO("Product title", "Product description", BigDecimal.valueOf(123.12), List.of(parameterDto), List.of(size1, size2),1);
////    }
////
////    private ProductUpdateDto createTestProductDto(){
////        ParameterDto parameterDto = new ParameterDto("Condition1", "New1");
////        SizeDto size1 = new SizeDto("S1",6);
////        SizeDto size2 = new SizeDto("M1",7);
////        return new ProductUpdateDto(1, "title1", "description1", BigDecimal.valueOf(13), null, List.of(parameterDto), List.of(size1, size2));
////    }
//}
