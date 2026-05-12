package com.neurotutor.user_service.service;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.model.Estudiante;
import com.neurotutor.user_service.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class AuthService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public AuthResponse register(RegisterRequest request) {
        // Validar email
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new RuntimeException("Correo inválido");
        }

        // Validar email único
        if (estudianteRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este correo ya tiene una cuenta. Inicia sesión");
        }

        // Validar password
        if (request.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }

        // Validar que coincidan las contraseñas
        if (!request.getPassword().equals(request.getPassword2())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Validar grado (4to,5to,6to,7mo)
        String[] grados = {"4to","5to","6to","7mo"};
        boolean gradoValido = false;
        for (String g : grados) {
            if (g.equals(request.getGrado())) gradoValido = true;
        }
        if (!gradoValido) {
            throw new RuntimeException("Grado inválido. Debe ser 4to, 5to, 6to o 7mo");
        }

        // Validar sección (A,B,C,D)
        if (!request.getSeccion().matches("[A-D]")) {
            throw new RuntimeException("Sección inválida. Debe ser A, B, C o D");
        }

        // Crear estudiante
        Estudiante estudiante = new Estudiante();
        estudiante.setEmail(request.getEmail());
        estudiante.setNombreCompleto(request.getNombreCompleto());
        estudiante.setGrado(request.getGrado());
        estudiante.setSeccion(request.getSeccion());
        estudiante.setPassword(passwordEncoder.encode(request.getPassword()));
        estudianteRepository.save(estudiante);

        // Token temporal (después implementaremos JWT completo)
        String token = "token-temporal-para-desarrollo";
        return new AuthResponse("Registro exitoso", estudiante.getEmail(), token);
    }
}