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
        System.out.println("LOGIN CONTROLLER CALLED");

        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        System.out.println("LOGIN CONTROLLER CALLED | login");

        return "login";
    }
    
    @GetMapping("/home")
    public String homePage() {
        System.out.println("LOGIN CONTROLLER CALLED | home");

        return "home";
    }
   
    
    @GetMapping("/register")
    public String registerPage() {
        System.out.println("LOGIN CONTROLLER CALLED | register");

        return "register";
    }
}