package com.example.identity.mapper;

import com.example.identity.dto.transaction.TransactionDTO;
import com.example.identity.models.Transaction;
import com.example.identity.models.Type;
import com.example.identity.repository.UserRepository;
import com.example.identity.webclient.WebClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
public class TransactionMapper {

    private final WebClientService webClientService;

    private final UserRepository userRepository;

    public TransactionDTO mapTransactionToTransactionDto(String authToken, Transaction transaction) {
        List<String> companies = new ArrayList<>();

        if (transaction.getOrder_id() != 0) {
            List<Long> companiesIdsByOrderId;

            if (transaction.getType().equals(Type.SENT)) {
                companiesIdsByOrderId = webClientService.getCompaniesIdsByOrderId(authToken, transaction.getOrder_id());
                companies = companiesIdsByOrderId.stream().map(userRepository::getCompanyNameByCompanyId).toList();
            } else if (transaction.getType().equals(Type.RECEIVED)) {
                companiesIdsByOrderId = List.of(webClientService.getOrderOwnerUserIdByOrderId(authToken, transaction.getOrder_id()));
                companies = companiesIdsByOrderId.stream().map(userRepository::getUserNameByCompanyId).toList();
            }
        } else {
            companies.add("Store");
        }

        return new TransactionDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getOrder_id(),
                transaction.getDate(),
                transaction.getType(),
                companies
        );
    }

}

