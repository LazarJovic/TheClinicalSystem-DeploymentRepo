package com.example.clinicalCenter.serviceInterface;

import com.example.clinicalCenter.dto.UserDTO;
import com.example.clinicalCenter.model.User;

import java.util.List;

public interface UserServiceInterface {
    User findById(Long id);

    User findByUsername(String username);

    List<User> findAll();

    UserDTO save(UserDTO userRequest) throws Exception;
}
