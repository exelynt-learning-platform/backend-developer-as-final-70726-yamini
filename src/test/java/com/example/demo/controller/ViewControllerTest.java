package com.example.demo.controller;

import com.example.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.config.SecurityConfig;
import com.example.demo.config.ViewResolverConfig;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest({LoginController.class, AdminController.class})
@Import({ViewResolverConfig.class, SecurityConfig.class})
public class ViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private com.example.demo.security.JwtService jwtService;

    @MockBean
    private com.example.demo.security.CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    void root_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    void loginPage_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    void homePage_ReturnsHomeView() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @WithMockUser
    void registerPage_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDashboard_ReturnsAdminView() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-dashboard"));
    }

    @Test
    void directControllerMethodsTest() {
        LoginController loginController = new LoginController(authService);
        assertThat(loginController.login()).isEqualTo("login");
        assertThat(loginController.loginPage()).isEqualTo("login");
        assertThat(loginController.homePage()).isEqualTo("home");
        assertThat(loginController.registerPage()).isEqualTo("register");

        AdminController adminController = new AdminController();
        assertThat(adminController.adminDashboard()).isEqualTo("admin/admin-dashboard");
    }
}
