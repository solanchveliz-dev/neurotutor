package com.neurotutor.user_service.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
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
            LOGGER.info("forgot-password request received for: {}", request.getEmail());
            AuthService.ForgotPasswordResult result = authService.forgotPassword(request);
            
            if (!result.accountExists()) {
                return ResponseEntity.ok(new ForgotPasswordResponse(
                        false, false, "No se pudo procesar la solicitud de recuperacion.", null
                ));
            }

            // Con el procesamiento asíncrono, ya no esperamos a que el correo se envíe.
            // Retornamos éxito al cliente para que pueda avanzar a la pantalla de Reset.
            
            boolean debugEnabled = resetTokenDebug || !isProductionProfile();
            String debugToken = debugEnabled ? result.token() : null;

            if (debugToken != null) {
                LOGGER.warn("RESET TOKEN DEBUG habilitado para {}.", request.getEmail());
                return ResponseEntity.ok(new ForgotPasswordResponse(
                        true, false, "Codigo de desarrollo: " + debugToken, debugToken
                ));
            }

            return ResponseEntity.ok(new ForgotPasswordResponse(
                    true, true, "Si el correo esta registrado, recibiras un codigo para restablecer tu contrasena.", null
            ));
        } catch (RuntimeException e) {
            LOGGER.error("Error en forgot-password: {}", e.getMessage());
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ForgotPasswordResponse {
        private final boolean success;
        private final boolean emailSent;
        private final String message;
        private final String debugToken;

        public ForgotPasswordResponse(boolean success, boolean emailSent, String message, String debugToken) {
            this.success = success;
            this.emailSent = emailSent;
            this.message = message;
            this.debugToken = debugToken;
        }

        public boolean isSuccess() { return success; }
        public boolean isEmailSent() { return emailSent; }
        public String getMessage() { return message; }
        public String getDebugToken() { return debugToken; }
        public String getDevCode() { return debugToken; }
    }

}
