package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.service.AuthService;

import jakarta.validation.Valid;

@Controller
public class LoginController {
	
	 private final AuthService authService;

	    public LoginController(AuthService authService) {
	        this.authService = authService;
	    }


    @GetMapping("/")
    public String login() {
        // show login page

        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        // show login page

        return "login";
    }
    
    @GetMapping("/home")
    public String homePage() {
        // show home page

        return "home";
    }
   
    
    @GetMapping("/register")
    public String registerPage() {
        // show register page

        return "register";
    }
}