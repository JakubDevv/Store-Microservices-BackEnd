package com.example.identity.repository;

import com.example.identity.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r from Role r WHERE r.name = ?1")
    Role findRoleByName(String name);
}
