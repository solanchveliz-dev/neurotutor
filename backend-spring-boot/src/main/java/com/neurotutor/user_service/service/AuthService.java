package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.model.PasswordResetToken;
import com.neurotutor.user_service.repository.EstudianteRepository;
import com.neurotutor.user_service.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class AuthService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ==================== REGISTRO ====================
    public AuthResponse register(RegisterRequest request) {
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Correo inválido");
        }

        if (estudianteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este correo ya tiene una cuenta.");
        }

        if (!request.getPassword().equals(request.getPassword2())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setEmail(request.getEmail());
        estudiante.setNombreCompleto(request.getNombreCompleto());
        estudiante.setGrado(request.getGrado());
        estudiante.setSeccion(request.getSeccion());
        estudiante.setPassword(passwordEncoder.encode(request.getPassword()));

        // 🚀 HU-10: Por defecto un usuario nuevo no ha hecho el examen
        estudiante.setExamenCompletado(false);

        estudianteRepository.save(estudiante);

        String token = "token-registro-" + System.currentTimeMillis();

        // 🚀 Enviamos el ID recién generado por MySQL
        return new AuthResponse(
                "Registro exitoso",
                estudiante.getEmail(),
                token,
                estudiante.getId().toString(),
                estudiante.isExamenCompletado()
        );
    }

    // ==================== LOGIN ====================
    public AuthResponse login(LoginRequest request) {
        Estudiante estudiante = estudianteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (estudiante.getBloqueadoHasta() != null &&
                estudiante.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cuenta bloqueada temporalmente");
        }

        if (!passwordEncoder.matches(request.getPassword(), estudiante.getPassword())) {
            estudiante.setIntentosFallidos(estudiante.getIntentosFallidos() + 1);
            if (estudiante.getIntentosFallidos() >= 5) {
                estudiante.setBloqueadoHasta(LocalDateTime.now().plusMinutes(15));
            }
            estudianteRepository.save(estudiante);
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }

        estudiante.setIntentosFallidos(0);
        estudiante.setBloqueadoHasta(null);
        estudianteRepository.save(estudiante);

        String token = "token-login-" + System.currentTimeMillis();

        // 🚀 CRÍTICO: Devolvemos el ID real y el estado del examen
        // Con esto Android sabrá si mandar al niño al DiagnosticScreen o al Dashboard
        return new AuthResponse(
                "Login exitoso",
                estudiante.getEmail(),
                token,
                estudiante.getId().toString(),
                estudiante.isExamenCompletado()
        );
    }

    // ... (El resto de métodos forgotPassword y resetPassword se mantienen igual ya que funcionan bien)
    public String forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        if (!estudianteRepository.existsByEmail(email)) {
            throw new RuntimeException("Email no registrado");
        }
        tokenRepository.findByEmail(email).ifPresent(tokenRepository::delete);

        int numeroAleatorio = (int) (Math.random() * 900000) + 100000;
        String token = String.valueOf(numeroAleatorio);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(60));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        emailService.sendResetToken(email, token);
        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token inválido o expirado");
        }

        Estudiante estudiante = estudianteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        estudiante.setPassword(passwordEncoder.encode(request.getNewPassword()));
        estudiante.setIntentosFallidos(0);
        estudiante.setBloqueadoHasta(null);
        estudianteRepository.save(estudiante);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}