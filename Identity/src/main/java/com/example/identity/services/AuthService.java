package com.example.identity.services;

import com.example.identity.dto.*;
import com.example.identity.dto.tokens.RefreshToken;
import com.example.identity.dto.tokens.Tokens;
import com.example.identity.dto.transaction.TransactionCreateDTO;
import com.example.identity.dto.transaction.TransactionDTO;
import com.example.identity.dto.user.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface AuthService {

    Tokens saveUser(UserCreateDTO userCreateDTO);

    Tokens authenticate(AuthRequest authRequest);

    UserRolesDTO validateToken(String token);

    UserDTO getUserById(Principal principal);

    UserShortDTO getUserById(Long userId);

    String getUsername(Long userId);

    List<String> getUserRoles(Principal principal);

    void createCompany(Principal principal, String brandName);

    CompanyDTO getCompany(Long sellerId);

    Tokens getAccessToken(RefreshToken refreshToken);

    BigDecimal getBalance(Principal principal);

    List<TransactionDTO> getTransactions(Principal principal, String authToken);

    Long getUserId(Long companyId);

    Long getCompanyId(Long userId);

    void createTransaction(TransactionCreateDTO transactionCreateDTO);

    Integer getAmountOfUsers();
}
