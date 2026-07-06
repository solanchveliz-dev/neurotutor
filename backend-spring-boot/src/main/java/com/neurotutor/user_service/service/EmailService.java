package com.neurotutor.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String emailRemitente;

    @Value("${spring.mail.password:}")
    private String emailPassword;

    public boolean isConfigured() {
        return emailRemitente != null && !emailRemitente.isBlank()
                && emailPassword != null && !emailPassword.isBlank();
    }

    public boolean sendResetToken(String to, String token) {
        if (!isConfigured()) {
            LOGGER.info("email async started: false (SMTP disabled)");
            return false;
        }
        LOGGER.info("email async started: true");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("🔐 NeuroTutor - Recuperación de contraseña");

            message.setText("Hola,\n\nTu token de recuperación para NeuroTutor es: " + token +
                    "\n\nEste código es válido por 60 minutos. Si no solicitaste este cambio, puedes ignorar este correo.");

            message.setFrom(emailRemitente);

            // 📡 Esto es lo que tardaba varios segundos en conectar con Gmail:
            mailSender.send(message);
            LOGGER.info("email async success: true");
            return true;

        } catch (Exception e) {
            LOGGER.error("email async failed: true; type={}", e.getClass().getSimpleName());
            return false;
        }
    }
}
