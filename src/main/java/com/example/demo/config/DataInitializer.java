package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByEmail("admin@example.com")) {
				User admin = new User();
				admin.setName("Admin");
				admin.setEmail("admin@example.com");
				admin.setPassword(passwordEncoder.encode("adminpass"));
				admin.setRole("ADMIN");
				userRepository.save(admin);
			}

			if (!userRepository.existsByEmail("user@example.com")) {
				User user = new User();
				user.setName("User");
				user.setEmail("user@example.com");
				user.setPassword(passwordEncoder.encode("userpass"));
				user.setRole("USER");
				userRepository.save(user);
			}
		};
	}
}
