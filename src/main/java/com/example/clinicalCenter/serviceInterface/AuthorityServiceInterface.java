package com.example.clinicalCenter.serviceInterface;

import com.example.clinicalCenter.model.Authority;

import java.util.List;

public interface AuthorityServiceInterface {
    List<Authority> findById(Long id);

    List<Authority> findByType(String type);
}
