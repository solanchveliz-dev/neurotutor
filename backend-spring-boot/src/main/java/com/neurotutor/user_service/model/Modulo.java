package com.neurotutor.user_service.model;

import jakarta.persistence.*;@Entity
@Table(name = "modulos")
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private int ejerciciosTotales;

    @Column(name = "nivel_requerido")
    private String nivelRequerido; // BASICO, INTERMEDIO, AVANZADO

    // Getters y Setters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getEjerciciosTotales() { return ejerciciosTotales; }
    public String getNivelRequerido() { return nivelRequerido; }
}