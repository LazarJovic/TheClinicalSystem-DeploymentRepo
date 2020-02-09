package com.example.clinicalCenter.service;

import com.example.clinicalCenter.model.Authority;
import com.example.clinicalCenter.repository.AuthorityRepository;
import com.example.clinicalCenter.serviceInterface.AuthorityServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorityService implements AuthorityServiceInterface {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public List<Authority> findById(Long id) {
        Authority auth = this.authorityRepository.getOne(id);
        List<Authority> auths = new ArrayList<>();
        auths.add(auth);
        return auths;
    }

    @Override
    public List<Authority> findByType(String type) {
        Authority auth = this.authorityRepository.findByType(type);
        List<Authority> auths = new ArrayList<>();
        auths.add(auth);
        return auths;
    }
}
