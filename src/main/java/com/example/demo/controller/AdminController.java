package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {

        // admin dashboard view

        return "admin/admin-dashboard";
    }
}