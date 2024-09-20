package com.example.identity.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class WebClientService {

    @Value("${products.server}")
    private String val;

    public List<Long> getCompaniesIdsByOrderId(String authToken, Long orderId) {
        WebClient client = WebClient.builder()
                .baseUrl("http://" + val + ":8080")
                .build();

        return client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/user/companies").queryParam("orderId", orderId).build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToFlux(Long.class)
                .collectList()
                .block();
    }

    public Long getOrderOwnerUserIdByOrderId(String authToken, Long orderId) {
        WebClient client = WebClient.builder()
                .baseUrl("http://" + val + ":8080")
                .build();

        return client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/company/userId").queryParam("orderId", orderId).build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public void retireProductsByUserId(String authToken, Long userId) {
        WebClient client = WebClient.builder()
                .baseUrl("http://" + val + ":8080")
                .build();

        client
            .put()
            .uri(uriBuilder -> uriBuilder.path("/admin/products/retire").queryParam("userId", userId).build())
            .header("Authorization", authToken)
            .retrieve()
             .bodyToMono(Void.class)
             .block();
    }
}
