package com.neurotutor.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

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

    @Async
    public void sendResetToken(String to, String token) {
        if (!isConfigured()) {
            return;
        }
        try {
            System.out.println("⏳ Iniciando envío de correo asíncrono para: " + to + " en segundo plano...");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("🔐 NeuroTutor - Recuperación de contraseña");

            message.setText("Hola,\n\nTu token de recuperación para NeuroTutor es: " + token +
                    "\n\nEste código es válido por 60 minutos. Si no solicitaste este cambio, puedes ignorar este correo.");

            message.setFrom(emailRemitente);

            // 📡 Esto es lo que tardaba varios segundos en conectar con Gmail:
            mailSender.send(message);

            System.out.println("📧 Correo enviado con éxito a: " + to);

        } catch (Exception e) {
            // Captura cualquier error de Gmail (claves mal puestas o puertos bloqueados) sin tirar la app móvil
            System.err.println("❌ Error crítico enviando el correo de fondo a " + to + ": " + e.getMessage());
        }
    }
}
