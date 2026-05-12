package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.repository.EstudianteRepository;
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

        String[] grados = {"1ro","2do","3ro","4to","5to","6to"};
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
}