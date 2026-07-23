package com.vinay.futurevault.controller;
import com.vinay.futurevault.dto.RefreshTokenRequest;
import com.vinay.futurevault.dto.LoginRequest;
import com.vinay.futurevault.dto.RegisterRequest;
import com.vinay.futurevault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.vinay.futurevault.dto.AuthResponse;
import com.vinay.futurevault.dto.ForgotPasswordRequest;
import com.vinay.futurevault.dto.ResetPasswordRequest;
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
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return userService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return userService.resetPassword(request);
    }
}