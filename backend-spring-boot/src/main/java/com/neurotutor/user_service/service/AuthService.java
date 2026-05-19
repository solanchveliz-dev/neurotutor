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
import java.util.UUID;
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
    private EmailService emailService; // 🔥 Inyección del servicio de correos

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ==================== REGISTRO ====================
    public AuthResponse register(RegisterRequest request) {
        // Validar email
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Correo inválido");
        }

        if (estudianteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este correo ya tiene una cuenta. Inicia sesión");
        }

        if (request.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }

        if (!request.getPassword().equals(request.getPassword2())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        String[] grados = {"1ro", "2do", "3ro", "4to", "5to", "6to"};
        boolean gradoValido = false;
        for (String g : grados) {
            if (g.equals(request.getGrado())) gradoValido = true;
        }
        if (!gradoValido) {
            throw new RuntimeException("Grado inválido");
        }

        if (!request.getSeccion().matches("[A-D]")) {
            throw new RuntimeException("Sección inválida. Debe ser A, B, C o D");
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setEmail(request.getEmail());
        estudiante.setNombreCompleto(request.getNombreCompleto());
        estudiante.setGrado(request.getGrado());
        estudiante.setSeccion(request.getSeccion());
        estudiante.setPassword(passwordEncoder.encode(request.getPassword()));
        estudianteRepository.save(estudiante);

        String token = "token-registro-" + System.currentTimeMillis();
        return new AuthResponse("Registro exitoso", estudiante.getEmail(), token);
    }

    // ==================== LOGIN ====================
    public AuthResponse login(LoginRequest request) {
        Estudiante estudiante = estudianteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));

        if (estudiante.getBloqueadoHasta() != null &&
                estudiante.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cuenta bloqueada por 15 minutos");
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
        return new AuthResponse("Login exitoso", estudiante.getEmail(), token);
    }

    // ==================== RECUPERACIÓN DE CONTRASEÑA ====================

    public String forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();

        System.out.println("========================================");
        System.out.println("🔐 SOLICITUD DE RECUPERACIÓN DE CONTRASEÑA");
        System.out.println("📧 Email solicitante: " + email);

        // Verificar si el email existe
        if (!estudianteRepository.existsByEmail(email)) {
            System.out.println("⚠️ Email NO registrado en el sistema: " + email);
            System.out.println("========================================");
            throw new RuntimeException("Email no registrado");
        }

        System.out.println("✅ Email encontrado en el sistema");

        // Eliminar tokens anteriores del mismo email
        tokenRepository.findByEmail(email).ifPresent(tokenAnterior -> {
            tokenRepository.delete(tokenAnterior);
            System.out.println("🗑️ Token anterior eliminado");
        });

        // 🔥 AQUÍ ESTÁ EL CAMBIO SÚPER IMPORTANTE// 🔥
        // Generamos un número aleatorio de 6 dígitos entre 100000 y 999999
        int numeroAleatorio = (int) (Math.random() * 900000) + 100000;
        String token = String.valueOf(numeroAleatorio);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setEmail(email);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(60));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        System.out.println("✅ Nuevo token de 6 dígitos guardado en BD");

        // Disparamos la orden para mandar el email real por Gmail con los 6 números
        emailService.sendResetToken(email, token);

        System.out.println("========================================");
        System.out.println("📝 TOKEN DE RECUPERACIÓN: " + token);
        System.out.println("⏰ Válido por 60 minutos");
        System.out.println("========================================");

        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        System.out.println("========================================");
        System.out.println("🔐 SOLICITUD DE RESTABLECIMIENTO DE CONTRASEÑA");
        System.out.println("📧 Email: " + request.getEmail());
        System.out.println("📝 Token recibido: " + request.getToken());

        // Validar que las contraseñas coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            System.out.println("❌ Error: Las contraseñas no coinciden");
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Validar longitud de contraseña
        if (request.getNewPassword().length() < 8) {
            System.out.println("❌ Error: Contraseña muy corta (menos de 8 caracteres)");
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }

        // Buscar token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> {
                    System.out.println("❌ Error: Token no encontrado en BD");
                    return new RuntimeException("Token inválido o expirado");
                });

        System.out.println("✅ Token encontrado en BD");

        // Verificar si ya fue usado
        if (resetToken.isUsed()) {
            System.out.println("❌ Error: Token ya fue utilizado anteriormente");
            throw new RuntimeException("Token ya utilizado");
        }

        // Verificar si expiró
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            System.out.println("❌ Error: Token expirado (fecha: " + resetToken.getExpiryDate() + ")");
            throw new RuntimeException("Token expirado");
        }

        // Verificar que el email coincida
        if (!resetToken.getEmail().equals(request.getEmail())) {
            System.out.println("❌ Error: El email no coincide con el token");
            throw new RuntimeException("Token inválido");
        }

        System.out.println("✅ Token válido, procediendo a cambiar contraseña");

        // Buscar estudiante
        Estudiante estudiante = estudianteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar contraseña
        estudiante.setPassword(passwordEncoder.encode(request.getNewPassword()));
        estudiante.setIntentosFallidos(0);
        estudiante.setBloqueadoHasta(null);
        estudianteRepository.save(estudiante);

        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        System.out.println("✅ Contraseña actualizada exitosamente");
        System.out.println("========================================");
    }
}