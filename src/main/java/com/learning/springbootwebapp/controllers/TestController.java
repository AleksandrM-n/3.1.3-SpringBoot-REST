package com.learning.springbootwebapp.controllers;

import com.learning.springbootwebapp.model.Role;
import com.learning.springbootwebapp.model.User;
import com.learning.springbootwebapp.repository.UserRepository;
import com.learning.springbootwebapp.services.UserDtoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    private final UserRepository userRepository;

    public TestController (UserRepository userRepository, UserDtoService userDtoService) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String asAdminStart(UsernamePasswordAuthenticationToken principal, ModelMap model) {
        model.addAttribute("authUser", userRepository.findUserByEmail(principal.getName()));
        return "learningPage";
    }
}
