package com.neurotutor.user_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modulos")
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private int ejerciciosTotales;

    @Column(name = "nivel_requerido")
    private String nivelRequerido; // BASICO, INTERMEDIO, AVANZADO

    @Column(columnDefinition = "TEXT")
    private String teoriaHtml;

    // 🚀 ESTO ES LO QUE FALTA PARA QUE ARRANQUE EL BACKEND
    @ManyToOne
    @JoinColumn(name = "tema_id")
    private Tema tema;

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getEjerciciosTotales() { return ejerciciosTotales; }
    public void setEjerciciosTotales(int ejerciciosTotales) { this.ejerciciosTotales = ejerciciosTotales; }

    public String getNivelRequerido() { return nivelRequerido; }
    public void setNivelRequerido(String nivelRequerido) { this.nivelRequerido = nivelRequerido; }

    public String getTeoriaHtml() { return teoriaHtml; }
    public void setTeoriaHtml(String teoriaHtml) { this.teoriaHtml = teoriaHtml; }

    public Tema getTema() { return tema; }
    public void setTema(Tema tema) { this.tema = tema; }
}