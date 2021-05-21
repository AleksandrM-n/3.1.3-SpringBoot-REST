package com.learning.springbootwebapp.controllers;

import com.learning.springbootwebapp.model.Role;
import com.learning.springbootwebapp.model.User;
import com.learning.springbootwebapp.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;


@Controller
public class UsersController {
    private final UserRepository userRepository;

    public UsersController (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String asAdminStart(UsernamePasswordAuthenticationToken principal, ModelMap model) {
        List<User> users = userRepository.findAll();
        String roles = principal.getAuthorities().toString().replaceAll("[]\\[]", "");
        model.addAttribute("roles", roles);
        model.addAttribute("email", principal.getName());
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        model.addAttribute("roleSet",
                new HashSet<>(Arrays.asList(new Role("ROLE_USER"), new Role("ROLE_ADMIN"))));
        return "admin";
    }

    @GetMapping("/admin/users")
    public String printUsers(ModelMap model) {

        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/user/{id}")
    public String printUserById(@PathVariable ("id") long id, ModelMap model) {
        User user = userRepository.getOne(id);
        model.addAttribute("user", user.toString());
        return "user";
    }

    @GetMapping("/admin/new")
    public String newUser(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("roleSet", new HashSet<>(Arrays.asList(new Role("ROLE_USER"), new Role("ROLE_ADMIN"))));
        return "newForm";
    }

    @PostMapping("/admin/new")
    public String addUser(@ModelAttribute ("user") User user) {
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("admin/update/{id}")
    public String updateUser(ModelMap model, @PathVariable("id") long id) {
        model.addAttribute("user", userRepository.getOne(id));
        model.addAttribute("roleSet", new HashSet<>(Arrays.asList(new Role("ROLE_USER"), new Role("ROLE_ADMIN"))));
        return "update";
    }

    @PostMapping("admin/update/{id}")
    public String update(@ModelAttribute("user") User user) {
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("admin/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return "redirect:/admin";
    }
}

