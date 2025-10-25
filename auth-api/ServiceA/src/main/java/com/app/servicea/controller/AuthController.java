package com.app.servicea.controller;

import com.app.servicea.dto.AuthRequest;
import com.app.servicea.dto.AuthResponse;
import com.app.servicea.entity.Users;
import com.app.servicea.security.JwtUtil;
import com.app.servicea.services.UsersServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersServices usersServices;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsersServices usersServices, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.usersServices = usersServices;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AuthRequest request) {
        // Перевірка на вже існуючого користувача має бути у UserService
        Users user = usersServices.registerNewUser(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(HttpStatus.CREATED);// Повертаємо 201
    }

    // 2. POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Users user = usersServices.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Валідація хешованого пароля
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Генерація JWT
        String token = jwtUtil.generateToken(user.getId());

        return ResponseEntity.ok(new AuthResponse(token));
    }

}
