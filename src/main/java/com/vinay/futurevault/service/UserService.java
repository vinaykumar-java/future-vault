package com.vinay.futurevault.service;

import com.vinay.futurevault.dto.*;
import com.vinay.futurevault.entity.PasswordResetToken;
import com.vinay.futurevault.entity.RefreshToken;
import com.vinay.futurevault.entity.User;
import com.vinay.futurevault.enums.Role;
import com.vinay.futurevault.exception.EmailAlreadyExistsException;
import com.vinay.futurevault.exception.InvalidCredentialsException;
import com.vinay.futurevault.exception.UserNotFoundException;
import com.vinay.futurevault.repository.UserRepository;
import com.vinay.futurevault.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RefreshTokenService refreshTokenService,
                       PasswordResetTokenService passwordResetTokenService,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public AuthResponse login(LoginRequest request) {

        logger.info("LOGIN METHOD STARTED");
        logger.info("Login attempt for email: {}", request.getEmail());

        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());

        logger.info("User found: {}", optionalUser.isPresent());

        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = optionalUser.get();

        logger.info("Entered Password = {}", request.getPassword());
        logger.info("DB Password = {}", user.getPassword());

        boolean match = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        logger.info("Password Match = {}", match);

        if (!match) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());

        String refreshToken = refreshTokenService
                .createRefreshToken(user.getEmail())
                .getToken();

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.verifyExpiration(
                        refreshTokenService.findByToken(
                                request.getRefreshToken()));

        String accessToken =
                jwtUtil.generateToken(refreshToken.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        PasswordResetToken token =
                passwordResetTokenService.createToken(user.getEmail());

        String resetLink =
                "http://localhost:8080/api/auth/reset-password?token="
                        + token.getToken();

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );

        return "Password reset link sent successfully.";
    }

    public String resetPassword(ResetPasswordRequest request) {

        PasswordResetToken token =
                passwordResetTokenService.verifyExpiration(
                        passwordResetTokenService.findByToken(
                                request.getToken()));

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        passwordResetTokenService.delete(token);

        return "Password reset successful.";
    }

    public UserProfileResponse getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public UserProfileResponse updateProfile(
            String currentEmail,
            UpdateProfileRequest request) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Email already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public String changePassword(
            String currentEmail,
            ChangePasswordRequest request) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Old password is incorrect");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new InvalidCredentialsException(
                    "New password and confirm password do not match");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        return "Password changed successfully";
    }
}