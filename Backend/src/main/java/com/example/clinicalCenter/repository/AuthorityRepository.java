package com.example.clinicalCenter.repository;

import com.example.clinicalCenter.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByType(String type);
}
