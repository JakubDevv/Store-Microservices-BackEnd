package com.example.store.webclient;

import com.example.store.dto.stats.AmountDate;
import com.example.store.dto.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WebClientService {

    @Value("${identity.server}")
    private String val;

    public CompanyShortDTO findCompanyName(Long companyId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/company").queryParam("companyId", companyId).build())
                .retrieve()
                .bodyToMono(CompanyShortDTO.class)
                .block();
    }

    public Long findCompanyIdByUserId(Long userId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/company-id").queryParam("userId", userId).build())
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public Long getUserId(Long companyId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/user-id").queryParam("companyId", companyId).build())
                .retrieve()
                .bodyToMono(Long.class)
                .block();
    }

    public String findUserName(Long userId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/userName").queryParam("userId", userId).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public UserDTO findUser(Long userId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri("/auth/user/{id}", userId)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }

    public List<Long> findCompanies(String authToken) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/admin-identity/companies").build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<Long>>>() {
                })
                .block().get("id");
    }

    public BigDecimal getBalance(String authToken) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/balance").build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(BigDecimal.class)
                .block();
    }

    public UserDTO getUser(String authToken, Long userId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri("/admin-identity/user/{id}", userId)
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }

    public List<UserDTO> getUsers(String authToken) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/admin-identity/users").build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToFlux(UserDTO.class)
                .collectList()
                .block();
    }

    public void createTransaction(String authToken, TransactionCreateDTO transactionCreateDTO) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/auth/transaction").build())
                .header("Authorization", authToken)
                .body(BodyInserters.fromValue(transactionCreateDTO))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void banUser(String authToken, Long userId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        webClient
                .delete()
                .uri("/admin-identity/user/{id}", userId)
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    public void banCompany(String authToken, Long companyId) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        webClient
                .delete()
                .uri("/admin-identity/company/{id}", companyId)
                .header("Authorization", authToken)
                .retrieve()
                .bodyToFlux(AmountDate.class)
                .collectList()
                .block();
    }

    public Map<LocalDate, Integer> getUsersInTime(String authToken) {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/admin-identity/usersInTime").build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<LocalDate, Integer>>() {})
                .block();
    }

    public Integer getAmountOfUsers() {
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/users").build())
                .retrieve()
                .bodyToMono(Integer.class)
                .block();
    }

    public List<TransactionDTO> getTransactions(String authToken){
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/transactions").build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToFlux(TransactionDTO.class)
                .collectList()
                .block();
    }

    public UserDTO getUser(String authToken){
        WebClient webClient = WebClient.builder().baseUrl("http://" + val + ":8080").build();

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/auth/user").build())
                .header("Authorization", authToken)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }
}
