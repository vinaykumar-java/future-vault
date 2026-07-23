package com.vinay.futurevault.service;

import com.vinay.futurevault.dto.AuthResponse;
import com.vinay.futurevault.dto.ChangePasswordRequest;
import com.vinay.futurevault.dto.LoginRequest;
import com.vinay.futurevault.dto.RegisterRequest;
import com.vinay.futurevault.entity.RefreshToken;
import com.vinay.futurevault.exception.EmailAlreadyExistsException;
import com.vinay.futurevault.exception.InvalidCredentialsException;
import com.vinay.futurevault.repository.UserRepository;
import com.vinay.futurevault.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vinay.futurevault.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegister() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Vinay");
        request.setEmail("vinay@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        // Act
        String result = userService.register(request);

        // Assert
        assertEquals("User Registered Successfully", result);

        verify(userRepository).save(any(User.class));
    }
    @Test
    void testRegister_EmailAlreadyExists() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("Vinay");
        request.setEmail("vinay@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        // Act & Assert
        EmailAlreadyExistsException exception =
                assertThrows(
                        EmailAlreadyExistsException.class,
                        () -> userService.register(request)
                );

        assertEquals("Email already exists", exception.getMessage());
    }
    @Test
    void testLoginSuccess() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("vinay@gmail.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("vinay@gmail.com");
        user.setPassword("encodedPassword");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(true);

        when(jwtUtil.generateToken(user.getEmail()))
                .thenReturn("access-token");

        when(refreshTokenService.createRefreshToken(user.getEmail()))
                .thenReturn(refreshToken);

        // Act
        AuthResponse response = userService.login(request);

        // Assert
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }
    @Test
    void testLoginUserNotFound() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@gmail.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }
    @Test
    void testLoginInvalidPassword() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("vinay@gmail.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("vinay@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()))
                .thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }
    @Test
    void testChangePasswordSuccess() {

        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old123");
        request.setNewPassword("new123");
        request.setConfirmPassword("new123");

        User user = new User();
        user.setEmail("vinay@gmail.com");
        user.setPassword("encodedOldPassword");

        when(userRepository.findByEmail("vinay@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("old123", "encodedOldPassword"))
                .thenReturn(true);

        when(passwordEncoder.encode("new123"))
                .thenReturn("encodedNewPassword");

        // Act
        String result = userService.changePassword(
                "vinay@gmail.com",
                request
        );

        // Assert
        assertEquals("Password changed successfully", result);

        verify(userRepository).save(user);
    }
    @Test
    void testChangePasswordWrongOldPassword() {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("new123");
        request.setConfirmPassword("new123");

        User user = new User();
        user.setEmail("vinay@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("vinay@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encodedPassword"))
                .thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.changePassword(
                                "vinay@gmail.com",
                                request
                        )
                );

        assertEquals(
                "Old password is incorrect",
                exception.getMessage()
        );
    }
    @Test
    void testChangePasswordMismatch() {

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old123");
        request.setNewPassword("new123");
        request.setConfirmPassword("new456");

        User user = new User();
        user.setEmail("vinay@gmail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("vinay@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("old123", "encodedPassword"))
                .thenReturn(true);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.changePassword(
                                "vinay@gmail.com",
                                request
                        )
                );

        assertEquals(
                "New password and confirm password do not match",
                exception.getMessage()
        );
    }
}