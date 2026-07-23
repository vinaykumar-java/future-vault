package com.vinay.futurevault.service;
import com.vinay.futurevault.dto.RefreshTokenRequest;
import com.vinay.futurevault.entity.RefreshToken;
import com.vinay.futurevault.dto.LoginRequest;
import com.vinay.futurevault.dto.RegisterRequest;
import com.vinay.futurevault.entity.User;
import com.vinay.futurevault.repository.UserRepository;
import com.vinay.futurevault.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vinay.futurevault.dto.AuthResponse;
import com.vinay.futurevault.service.RefreshTokenService;
import com.vinay.futurevault.dto.ForgotPasswordRequest;
import com.vinay.futurevault.dto.ResetPasswordRequest;
import com.vinay.futurevault.entity.PasswordResetToken;
@Service
public class UserService {

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
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());

        String refreshToken = refreshTokenService
                .createRefreshToken(user.getEmail())
                .getToken();

        return new AuthResponse(accessToken, refreshToken);
    }
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService
                .verifyExpiration(
                        refreshTokenService.findByToken(request.getRefreshToken())
                );

        String accessToken = jwtUtil.generateToken(refreshToken.getEmail());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }
    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
                        passwordResetTokenService.findByToken(request.getToken())
                );

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        passwordResetTokenService.delete(token);

        return "Password reset successful.";
    }
}