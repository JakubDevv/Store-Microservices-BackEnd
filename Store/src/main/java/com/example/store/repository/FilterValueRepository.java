package com.example.store.repository;

import com.example.store.model.FilterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterValueRepository extends JpaRepository<FilterValue, Long> {
}
