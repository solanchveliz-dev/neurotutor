package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.ForgotPasswordRequest;
import com.neurotutor.user_service.dto.ResetPasswordRequest;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock EstudianteRepository estudianteRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock PasswordResetTokenRepository tokenRepository;
    @Mock EmailService emailService;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    @Test
    void forgotPasswordDoesNotRevealUnknownEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("unknown@example.com");
        when(estudianteRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        AuthService.ForgotPasswordResult result = authService.forgotPassword(request);

        assertFalse(result.accountExists());
        assertFalse(result.emailSent());
        assertNull(result.token());
        verify(emailService, never()).sendResetToken(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void resetPasswordRejectsTokenFromDifferentEmail() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("victim@example.com");
        request.setToken("123456");
        request.setNewPassword("new-password");
        request.setConfirmPassword("new-password");
        when(tokenRepository.findByTokenAndEmail("123456", "victim@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.resetPassword(request));
        verify(estudianteRepository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }
}
