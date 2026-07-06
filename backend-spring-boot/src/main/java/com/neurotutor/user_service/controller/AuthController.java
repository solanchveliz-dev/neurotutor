package com.neurotutor.user_service.controller;

import com.neurotutor.user_service.dto.*;
import com.neurotutor.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment environment;

    @Value("${app.environment:production}")
    private String appEnvironment;

    @Value("${reset-token.debug:false}")
    private boolean resetTokenDebug;

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
            boolean debugEnabled = resetTokenDebug || !isProductionProfile();
            if (debugEnabled && result.accountExists() && !result.emailSent()) {
                LOGGER.warn("RESET TOKEN DEBUG habilitado para {}. No activar en produccion salvo pruebas controladas.", request.getEmail());
                return ResponseEntity.ok(new TokenResponse(
                        null,
                        "El codigo fue generado, pero no se pudo enviar el correo. Revisa configuracion SMTP.",
                        result.token()
                ));
            }
            if (result.accountExists() && !result.emailSent()) {
                return ResponseEntity.status(503).body(new ErrorResponse(
                        "El codigo fue generado, pero no se pudo enviar el correo. Revisa configuracion SMTP."
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

    private boolean isProductionProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("production".equalsIgnoreCase(profile) || "prod".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        if (activeProfiles.length > 0) {
            return false;
        }
        return "production".equalsIgnoreCase(appEnvironment) || "prod".equalsIgnoreCase(appEnvironment);
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
        private String debugToken;
        public TokenResponse(String token, String message, String debugToken) {
            this.token = token;
            this.message = message;
            this.debugToken = debugToken;
        }
        public String getToken() { return token; }
        public String getMessage() { return message; }
        public String getDebugToken() { return debugToken; }
        public String getDevCode() { return debugToken; }
    }

}
