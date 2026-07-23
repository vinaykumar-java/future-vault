package com.vinay.futurevault.controller;
import com.vinay.futurevault.dto.*;
import com.vinay.futurevault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<String> register(
            @Valid @RequestBody RegisterRequest request) {

        return new ApiResponse<>(
                true,
                "User registered successfully",
                userService.register(request)
        );
    }
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return new ApiResponse<>(
                true,
                "Login successful",
                userService.login(request)
        );
    }
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        return new ApiResponse<>(
                true,
                "Token refreshed successfully",
                userService.refreshToken(request)
        );
    }
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        return new ApiResponse<>(
                true,
                "Password reset email sent",
                userService.forgotPassword(request)
        );
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return new ApiResponse<>(
                true,
                "Password reset successful",
                userService.resetPassword(request)
        );
    }
}