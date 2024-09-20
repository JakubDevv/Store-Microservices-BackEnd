package com.example.identity.services;

import com.example.identity.dto.*;
import com.example.identity.dto.tokens.RefreshToken;
import com.example.identity.dto.tokens.Tokens;
import com.example.identity.dto.transaction.TransactionCreateDTO;
import com.example.identity.dto.transaction.TransactionDTO;
import com.example.identity.dto.user.*;
import com.example.identity.exceptions.CompanyNotFoundException;
import com.example.identity.exceptions.UserNotFoundException;
import com.example.identity.jwt.JwtService;
import com.example.identity.mapper.TransactionMapper;
import com.example.identity.models.*;
import com.example.identity.repository.CompanyRepository;
import com.example.identity.repository.RoleRepository;
import com.example.identity.repository.TransactionRepository;
import com.example.identity.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(JwtService jwtService, UserRepository userRepository, AuthenticationManager authenticationManager, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, TransactionMapper transactionMapper, TransactionRepository transactionRepository, RoleRepository roleRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Tokens saveUser(UserCreateDTO userCreateDTO) {
        User user = new User(userCreateDTO.userName(), userCreateDTO.firstName(), userCreateDTO.lastName(), passwordEncoder.encode(userCreateDTO.password()));
        Transaction transaction = new Transaction(BigDecimal.valueOf(1000), 0L, Type.RECEIVED);
        transactionRepository.save(transaction);
        user.setTransactions(List.of(transaction));
        User user1 = userRepository.save(user);
        return new Tokens(jwtService.generateRefreshToken(user1.getUser_name()),  jwtService.generateAccessToken(user1.getUser_name()));
    }

    @Override
    public Tokens authenticate(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        if (authenticate.isAuthenticated()) {
            return new Tokens(jwtService.generateRefreshToken(authRequest.username()), jwtService.generateAccessToken(authRequest.username()));
        } else {
            throw new RuntimeException("Wrong username or password");
        }
    }

    @Override
    public UserRolesDTO validateToken(String token) {
        User user = userRepository.findUserByUser_name(jwtService.getSubjectAccessToken(token)).orElseThrow(() -> new UserNotFoundException(jwtService.getSubjectAccessToken(token)));
        return new UserRolesDTO(user.getId(), user.getRoles().stream().map(Role::getName).toList());
    }

    @Override
    public UserDTO getUserById(Principal principal) {
        return userRepository.findUserByName(principal.getName());
    }

    @Override
    public UserShortDTO getUserById(Long userId) {
        return userRepository.findUserById2(userId);
    }

    @Override
    public String getUsername(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return user.getUser_name();
    }

    @Override
    public List<String> getUserRoles(Principal principal) {
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return user.getRoles().stream().map(Role::getName).toList();
    }

    @Override
    public void createCompany(Principal principal, String brandName) {
        Company company = new Company(brandName);
        companyRepository.save(company);
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        user.setCompany(company);
        Role role = roleRepository.findRoleByName("company");
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public CompanyDTO getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new CompanyNotFoundException(companyId));
        return new CompanyDTO(company.getId(), company.getName(), company.getCreated(), company.getBanned());
    }

    @Override
    public Tokens getAccessToken(RefreshToken refreshToken) {
        if(jwtService.validateRefreshToken(refreshToken.value())){
            return new Tokens(jwtService.generateRefreshToken(jwtService.getSubjectRefreshToken(refreshToken.value())), jwtService.generateAccessToken(jwtService.getSubjectRefreshToken(refreshToken.value())));
        }
        else {
            throw new IllegalArgumentException("Wrong refresh token");
        }
    }

    @Override
    public BigDecimal getBalance(Principal principal) {
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return user.getBalance();
    }

    @Override
    public List<TransactionDTO> getTransactions(Principal principal, String authToken) {
        List<Transaction> transactions = userRepository.findTransactionsByUserName(principal.getName());
        return transactions.stream().map(transaction -> transactionMapper.mapTransactionToTransactionDto(authToken, transaction)).toList();
    }

    @Override
    public Long getUserId(Long companyId) {
        return userRepository.findUserByCompanyId(companyId).orElseThrow(() -> new CompanyNotFoundException(companyId)).getId();
    }

    @Override
    public Long getCompanyId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)).getCompany().getId();
    }

    @Override
    public void createTransaction(TransactionCreateDTO transactionCreateDTO) {
        User user = userRepository.findById(transactionCreateDTO.userId()).orElseThrow(() -> new UserNotFoundException(transactionCreateDTO.userId()));
        if(user.getBalance().compareTo(transactionCreateDTO.price()) >= 0) {
            user.setBalance(user.getBalance().subtract(transactionCreateDTO.price()));
            Transaction userTransaction = new Transaction(transactionCreateDTO.price(), transactionCreateDTO.orderId(), Type.SENT);
            transactionRepository.save(userTransaction);
            user.getTransactions().add(userTransaction);
            userRepository.save(user);
            transactionCreateDTO.pricePerCompany().forEach((key, val) -> {
                Transaction companyTransaction = new Transaction(val, transactionCreateDTO.orderId(), Type.RECEIVED);
                User user1 = userRepository.findUserByCompanyId(key).orElseThrow(() -> new CompanyNotFoundException(key));
                transactionRepository.save(companyTransaction);
                user1.setBalance(user1.getBalance().add(val));
                user1.getTransactions().add(companyTransaction);
                userRepository.save(user1);
            });
        }
    }

    @Override
    public Integer getAmountOfUsers() {
        return userRepository.countUsers();
    }
}
