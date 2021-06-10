package com.learning.springbootwebapp.controllers;

import com.learning.springbootwebapp.model.Role;
import com.learning.springbootwebapp.model.User;
import com.learning.springbootwebapp.repository.UserRepository;
import com.learning.springbootwebapp.services.UserDtoService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UsersRestController {
    private final UserRepository userRepository;

    public UsersRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/role-list")
    public ResponseEntity<List<Role>> getRoleList() {
        return new ResponseEntity<>(Arrays.asList(new Role("ROLE_USER"), new Role("ROLE_ADMIN")), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).isPresent() ? userRepository.findById(id).get() : null;

        if(user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> update(@RequestBody User user) {
        User userUp = userRepository.save(user);
        return new ResponseEntity<>(userUp, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> delete(@RequestBody User user) {
        userRepository.delete(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
