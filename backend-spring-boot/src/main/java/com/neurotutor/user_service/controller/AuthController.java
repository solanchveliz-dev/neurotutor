package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Value("${app.environment:production}")
    private String appEnvironment;

    // ==================== REGISTRO ====================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== RECUPERACIÓN DE CONTRASEÑA ====================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            LOGGER.info("forgot-password request received");
            LOGGER.info("forgot-password app env: {}", appEnvironment);
            AuthService.ForgotPasswordResult result = authService.forgotPassword(request);
            boolean development = "development".equalsIgnoreCase(appEnvironment);
            if (development && result.accountExists()) {
                System.out.println("[DEV] Token de recuperacion para " + request.getEmail() + ": " + result.token());
                return ResponseEntity.ok(new TokenResponse(
                        null,
                        result.emailSent()
                                ? "Solicitud registrada. Usa el codigo de desarrollo o revisa tu correo."
                                : "SMTP no esta configurado. Usa el codigo de desarrollo.",
                        result.token()
                ));
            }
            if (result.accountExists() && !result.emailSent()) {
                return ResponseEntity.status(503).body(new ErrorResponse(
                        "El código fue generado, pero el servicio de correo no está disponible. Intenta nuevamente."
                ));
            }
            return ResponseEntity.ok(new TokenResponse(
                    null,
                    "Si el correo está registrado, recibirás un código para restablecer tu contraseña.",
                    null
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(new MessageResponse("Contraseña restablecida con éxito"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // ==================== CLASES DE RESPUESTA ====================

    static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

    static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    static class TokenResponse {
        private String token;
        private String message;
        private String devCode;
        public TokenResponse(String token, String message, String devCode) {
            this.token = token;
            this.message = message;
            this.devCode = devCode;
        }
        public String getToken() { return token; }
        public String getMessage() { return message; }
        public String getDevCode() { return devCode; }
    }

}
