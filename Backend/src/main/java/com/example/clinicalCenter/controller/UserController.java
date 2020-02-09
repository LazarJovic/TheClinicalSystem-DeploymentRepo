package com.example.clinicalCenter.controller;

import com.example.clinicalCenter.dto.SimpleUserDTO;
import com.example.clinicalCenter.model.User;
import com.example.clinicalCenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{type}")
    public List<User> getAll(@PathVariable String type) {
        return this.userService.getAll(type);
    }

    @GetMapping(value = "/logged-in")
    public ResponseEntity<?> getUsernameOfLoggedIn() {
        SimpleUserDTO dto = this.userService.getLoggedIn();
        if (dto == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
