package com.neurotutor.user_service.service;

import com.neurotutor.user_service.model.Estudiante;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    private static final String SECRET =
            "0123456789abcdef0123456789abcdef";

    @Test
    void generatedTokenContainsStudentId() {
        JwtService jwtService = new JwtService(SECRET, 60_000);
        Estudiante student = new Estudiante();
        student.setId(42L);
        student.setEmail("student@example.com");

        String token = jwtService.generateToken(student);

        assertEquals("42", jwtService.extractStudentId(token));
    }
}
