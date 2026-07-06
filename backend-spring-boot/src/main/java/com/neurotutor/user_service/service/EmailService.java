package com.neurotutor.user_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
            LOGGER.error("No se puede enviar el reset token a {}: MAIL_USERNAME o MAIL_PASSWORD no estan configurados", to);
            return false;
        }

        try {
            LOGGER.info("Iniciando envio sincronico del reset token a {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("NeuroTutor - Recuperacion de contrasena");
            message.setText("Hola,\n\nTu token de recuperacion para NeuroTutor es: " + token +
                    "\n\nEste codigo es valido por 60 minutos. Si no solicitaste este cambio, puedes ignorar este correo.");
            message.setFrom(emailRemitente);

            mailSender.send(message);

            LOGGER.info("Reset token enviado correctamente a {}", to);
            return true;

        } catch (Exception e) {
            LOGGER.error("Fallo SMTP: tipo={}, mensaje={}, causa={}",
                    e.getClass().getName(),
                    e.getMessage(),
                    e.getCause() == null ? "sin causa adicional" : e.getCause().toString());

            LOGGER.error("EMAIL ERROR COMPLETO enviando reset token a {}", to, e);
            return false;
        }
    }
}