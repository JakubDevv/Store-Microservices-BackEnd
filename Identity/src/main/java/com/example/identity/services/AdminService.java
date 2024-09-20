package com.example.identity.services;

import com.example.identity.dto.user.UserDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {

    List<UserDTO> getAllUsers();

    Map<LocalDate, Integer> getUsersInTime();

    Map<String, List<Long>> getCompanies();

    UserDTO getUserById(Long userId);

    void banCompanyById(String authToken, Long companyId);

    void banUserById(String authToken, Long userId);

}
