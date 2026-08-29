package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminDashboard() {

        System.out.println("ADMIN DASHBOARD CALLED");

        return "admin/admin-dashboard";
    }
}