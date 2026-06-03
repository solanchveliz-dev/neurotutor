package com.neurotutor.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // 🚀 IMPORTANTE

@SpringBootApplication
@EnableAsync // 🚀 ESTA LÍNEA DA EL SÚPER PODER DE ASINCRONÍA
public class UserServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}