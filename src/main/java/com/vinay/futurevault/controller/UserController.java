package com.vinay.futurevault.controller;

import com.vinay.futurevault.dto.*;
import com.vinay.futurevault.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile() {

        return new ApiResponse<>(
                true,
                "Profile fetched successfully",
                userService.getProfile()
        );
    }
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return new ApiResponse<>(
                true,
                "Profile updated successfully",
                userService.updateProfile(email, request)
        );
    }
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return new ApiResponse<>(
                true,
                "Password changed successfully",
                userService.changePassword(email, request)
        );
    }
}