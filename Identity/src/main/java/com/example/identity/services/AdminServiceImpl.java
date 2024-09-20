package com.example.identity.services;

import com.example.identity.dto.user.UserDTO;
import com.example.identity.exceptions.CompanyNotFoundException;
import com.example.identity.exceptions.UserNotFoundException;
import com.example.identity.models.Company;
import com.example.identity.models.User;
import com.example.identity.repository.CompanyRepository;
import com.example.identity.repository.UserRepository;
import com.example.identity.webclient.WebClientService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    private final WebClientService webClientService;

    public AdminServiceImpl(UserRepository userRepository, CompanyRepository companyRepository, WebClientService webClientService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.webClientService = webClientService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll2().stream().filter(user -> user.username() != "Admin").toList();
    }

    @Override
    public Map<LocalDate, Integer> getUsersInTime() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> user.getCreated().toLocalDate())
                .collect(Collectors.groupingBy(date -> date, Collectors.summingInt(e -> 1)));
    }

    @Override
    public Map<String, List<Long>> getCompanies(){
        List<Company> all = companyRepository.findAll();

        Map<String, List<Long>> map = new HashMap<>();

        map.put("id", all.stream().map(Company::getId).toList());

        return map;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public void banCompanyById(String authToken, Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new CompanyNotFoundException(companyId));
        company.setBanned(LocalDateTime.now());
        webClientService.retireProductsByUserId(authToken, companyId);
        companyRepository.save(company);
    }

    @Override
    public void banUserById(String authToken, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if(user.getCompany() != null){
            banCompanyById(authToken, user.getCompany().getId());
        }
        user.setBanned(LocalDateTime.now());
        userRepository.save(user);
    }

}
