package com.example.identity.controller;

import com.example.identity.dto.*;
import com.example.identity.dto.tokens.RefreshToken;
import com.example.identity.dto.tokens.Tokens;
import com.example.identity.dto.transaction.TransactionCreateDTO;
import com.example.identity.dto.transaction.TransactionDTO;
import com.example.identity.dto.user.*;
import com.example.identity.services.AuthServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<Tokens> createNewUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return new ResponseEntity<>(authServiceImpl.saveUser(userCreateDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Tokens> authenticate(@Valid @RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(authServiceImpl.authenticate(authRequest), HttpStatus.OK);
    }
    @GetMapping("/validate-token")
    public ResponseEntity<UserRolesDTO> validateToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(authServiceImpl.validateToken(token), HttpStatus.OK);
    }

    // todo
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUser(Principal principal) {
        return new ResponseEntity<>(authServiceImpl.getUserById(principal), HttpStatus.OK);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<UserShortDTO> getUserById(@Min(1) @PathVariable Long id) {
        return new ResponseEntity<>(authServiceImpl.getUserById(id), HttpStatus.OK);
    }

    // todo
    @GetMapping("/userName")
    public ResponseEntity<String> getUsername(@RequestParam Long userId) {
        return new ResponseEntity<>(authServiceImpl.getUsername(userId), HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getUserRoles(Principal principal) {
        return new ResponseEntity<>(authServiceImpl.getUserRoles(principal), HttpStatus.OK);
    }

    // todo
    @PostMapping("/company")
    public ResponseEntity<Void> createCompany(Principal principal, @RequestParam String companyName) {
        authServiceImpl.createCompany(principal, companyName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // todo
    @GetMapping("/company")
    public ResponseEntity<CompanyDTO> getCompany(@RequestParam Long companyId) {
        return new ResponseEntity<>(authServiceImpl.getCompany(companyId), HttpStatus.OK);
    }

    @PostMapping("/access-token")
    public ResponseEntity<Tokens> getNewAccessToken(@Valid @RequestBody RefreshToken refreshToken){
        return new ResponseEntity<>(authServiceImpl.getAccessToken(refreshToken), HttpStatus.CREATED);
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(Principal principal){
        return new ResponseEntity<>(authServiceImpl.getBalance(principal), HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(Principal principal, @RequestHeader("Authorization") String authToken){
        return new ResponseEntity<>(authServiceImpl.getTransactions(principal, authToken), HttpStatus.OK);
    }

    @GetMapping("/user-id")
    public ResponseEntity<Long> getUserId(@RequestParam Long companyId){
        return new ResponseEntity<>(authServiceImpl.getUserId(companyId), HttpStatus.OK);
    }

    @GetMapping("/company-id")
    public ResponseEntity<Long> getCompanyId(@RequestParam Long userId){
        return new ResponseEntity<>(authServiceImpl.getCompanyId(userId), HttpStatus.OK);
    }

    @PostMapping("/transaction")
    public ResponseEntity<Void> createTransaction(@Valid @RequestBody TransactionCreateDTO transactionCreateDTO){
        authServiceImpl.createTransaction(transactionCreateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<Integer> getAmountOfUsers(){
        return new ResponseEntity<>(authServiceImpl.getAmountOfUsers(), HttpStatus.OK);
    }
}
