package com.vinay.futurevault.controller;
import com.vinay.futurevault.dto.RefreshTokenRequest;
import com.vinay.futurevault.dto.LoginRequest;
import com.vinay.futurevault.dto.RegisterRequest;
import com.vinay.futurevault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.vinay.futurevault.dto.AuthResponse;
@RestController

@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        System.out.println("LOGIN API CALLED");
        return userService.login(request);
    }
    @PostMapping("/refresh")
    public AuthResponse refreshToken(
            @RequestBody RefreshTokenRequest request) {

        return userService.refreshToken(request);
    }
}