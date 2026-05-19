package com.neurotutor.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 🔥 IMPORTANTE
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 🔥 Esto lee automáticamente el correo que configuraste en tu application.properties
    @Value("${spring.mail.username}")
    private String emailRemitente;

    public void sendResetToken(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🔐 NeuroTutor - Recuperación de contraseña");

        // Un mensaje un poco más amigable para el estudiante
        message.setText("Hola,\n\nTu token de recuperación para NeuroTutor es: " + token +
                "\n\nEste código es válido por 60 minutos. Si no solicitaste este cambio, puedes ignorar este correo.");

        // 🔥 CORRECCIÓN: Aquí usamos tu Gmail real configurado en las propiedades
        message.setFrom(emailRemitente);

        mailSender.send(message);
        System.out.println("📧 Correo enviado con éxito a: " + to);
    }
}