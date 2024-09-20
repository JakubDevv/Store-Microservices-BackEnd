package com.example.identity.controller;

import com.example.identity.dto.user.UserDTO;
import com.example.identity.services.AdminServiceImpl;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-identity")
public class AdminController {

    private final AdminServiceImpl adminService;

    public AdminController(AdminServiceImpl adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return new ResponseEntity<>(adminService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/usersInTime")
    public ResponseEntity<Map<LocalDate, Integer>> getUsersInTime(){
        return new ResponseEntity<>(adminService.getUsersInTime(), HttpStatus.OK);
    }

    @GetMapping("/companies")
    public ResponseEntity<Map<String, List<Long>>> getCompanies(){
        return new ResponseEntity<>(adminService.getCompanies(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@Min(1) @PathVariable Long id){
        return new ResponseEntity<>(adminService.getUserById(id), HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> banUserById(@Min(1) @PathVariable Long id, @RequestHeader("Authorization") String authToken){
        adminService.banUserById(authToken, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/company/{id}")
    public ResponseEntity<Void> banCompany(@Min(1) @PathVariable Long id, @RequestHeader("Authorization") String authToken){
        adminService.banCompanyById(authToken, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
