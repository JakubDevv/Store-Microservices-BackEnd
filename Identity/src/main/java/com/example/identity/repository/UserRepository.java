package com.example.identity.repository;

import com.example.identity.dto.user.UserDTO;
import com.example.identity.dto.user.UserShortDTO;
import com.example.identity.models.Transaction;
import com.example.identity.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.user_name = ?1")
    Optional<User> findUserByUser_name(String username);

    @Query("SELECT u FROM User u RIGHT JOIN u.company c WHERE c.id=?1")
    Optional<User> findUserByCompanyId(Long companyId);

    @Query("SELECT new com.example.identity.dto.user.UserDTO(u.id, u.first_name, u.last_name, u.user_name, coalesce(c.name, null), u.created, u.banned, u.balance, u.photo) FROM User u LEFT JOIN u.company c")
    List<UserDTO> findAll2();

    @Query("SELECT new com.example.identity.dto.user.UserDTO(u.id, u.first_name, u.last_name, u.user_name, coalesce(c.name, null), u.created, u.banned, u.balance, u.photo) FROM User u LEFT JOIN u.company c WHERE u.id=?1")
    Optional<UserDTO> findUserById(Long id);

    @Query("SELECT new com.example.identity.dto.user.UserShortDTO(u.id, u.first_name, u.last_name, u.user_name) FROM User u WHERE u.id=?1")
    UserShortDTO findUserById2(Long id);

    @Query("SELECT new com.example.identity.dto.user.UserDTO(u.id, u.first_name, u.last_name, u.user_name, coalesce(c.name, null), u.created, u.banned, u.balance, u.photo) FROM User u LEFT JOIN u.company c WHERE u.user_name=?1")
    UserDTO findUserByName(String name);

    @Query("SELECT t FROM User u right JOIN u.transactions t WHERE u.user_name = ?1 order by t.date DESC")
    List<Transaction> findTransactionsByUserName(String userName);

    @Query("SELECT c.name FROM User u RIGHT JOIN u.company c WHERE c.id = ?1")
    String getCompanyNameByCompanyId(Long userId);

    @Query("SELECT u.user_name FROM User u WHERE u.id = ?1")
    String getUserNameByCompanyId(Long userId);

    @Query("SELECT count(u) FROM User u")
    Integer countUsers();
}

