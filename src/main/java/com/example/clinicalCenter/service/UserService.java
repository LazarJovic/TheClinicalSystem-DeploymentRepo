package com.example.clinicalCenter.service;

import com.example.clinicalCenter.dto.SimpleUserDTO;
import com.example.clinicalCenter.dto.UserDTO;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.repository.UserRepository;
import com.example.clinicalCenter.serviceInterface.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@ComponentScan
@Service
public class UserService implements ServiceInterface<UserDTO> {

    @Autowired
    private UserRepository userRepository;

    public UserService() {

    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElseGet(null);
    }

    public User findByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    @Override
    public List<UserDTO> findAll() {
        return null;
    }

    @Override
    public UserDTO findOne(Long id) {
        return null;
    }

    @Override
    public UserDTO create(UserDTO dto) throws Exception {
        return null;
    }

    @Override
    public UserDTO update(UserDTO dto) throws Exception {
        return null;
    }

    public User update(User u) {
        return this.userRepository.save(u);
    }

    @Override
    public UserDTO delete(Long id) {
        return null;
    }

    public List<User> getAll(String type) {
        return this.userRepository.findByType(type);
    }

    public SimpleUserDTO getLoggedIn() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByEmail(email);
        if (u == null)
            return null;
        return new SimpleUserDTO(u.getEmail(), u.getName(), u.getSurname());
    }
}
