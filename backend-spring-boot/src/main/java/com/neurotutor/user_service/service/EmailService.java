package com.neurotutor.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async; // 🚀 IMPORTANTE: Importación añadida
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailRemitente;

    @Async // 🚀 SÚPER PODER: Hace que el envío corra en segundo plano sin congelar la petición de Android
    public void sendResetToken(String to, String token) {
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

            System.err.println("====================================");
            System.err.println("ERROR COMPLETO EN ENVÍO DE CORREO");
            System.err.println("Destinatario: " + to);
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());

            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }

            e.printStackTrace();

            System.err.println("====================================");
        }
    }
}