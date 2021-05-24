package com.learning.springbootwebapp.controllers;

import com.learning.springbootwebapp.model.Role;
import com.learning.springbootwebapp.model.User;
import com.learning.springbootwebapp.services.UserDtoService;
import com.learning.springbootwebapp.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
public class UsersController {
    private final UserRepository userRepository;
    private final UserDtoService userDtoService;

    public UsersController (UserRepository userRepository, UserDtoService userDtoService) {
        this.userRepository = userRepository;
        this.userDtoService = userDtoService;
    }

    @GetMapping("/")
    public String asAdminStart(UsernamePasswordAuthenticationToken principal, ModelMap model) {
        List<User> users = userRepository.findAll();
        userDtoService.setUsers(users);
        model.addAttribute("authUser", userRepository.findUserByEmail(principal.getName()));
        model.addAttribute("packedUsers", userDtoService);
        model.addAttribute("user", new User());
        model.addAttribute("roleSet",
                new HashSet<>(Arrays.asList(new Role("ROLE_USER"), new Role("ROLE_ADMIN"))));
        return "admin";
    }

    @PostMapping("/admin/new")
    public String addUser(@ModelAttribute ("user") User user) {
        userRepository.save(user);
        return "redirect:/";
    }

    @PostMapping("/update/{id}")
    public String update(@ModelAttribute("packedUsers")  UserDtoService packedUsers) {
        List<User> users = packedUsers.getUsers().stream().filter
                (user -> (user.getEmail() != null) || (user.getFirstName() != null)).collect(Collectors.toList());
        userRepository.saveAll(users);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return "redirect:/";
    }
}

