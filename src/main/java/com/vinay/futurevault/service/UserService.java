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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
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
}