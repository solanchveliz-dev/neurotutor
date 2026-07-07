package com.neurotutor.user_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nombreCompleto;

    private String grado;
    private String seccion;
    private String password;

    // 🚀 EP-02 / HU-11: Persistencia del nivel calculado
    @Column(name = "nivel_diagnostico")
    private String nivelDiagnostico; // BASICO, INTERMEDIO, AVANZADO

    // 🚀 EP-02 / HU-10: Flag crítico para la navegación automática en Android
    @Column(name = "examen_completado")
    private boolean examenCompletado = false;

    // 🚀 EP-02 / HU-14: Campo necesario para el Dashboard
    @Column(name = "puntos_totales")
    private int puntosTotales = 0;

    // 🚀 HU-25: Módulos que el estudiante ya completó (para control de puntos)
    @ManyToMany
    @JoinTable(
            name = "estudiante_modulos_completados",
            joinColumns = @JoinColumn(name = "estudiante_id"),
            inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    private List<Modulo> modulosCompletados = new ArrayList<>();

    // Campos de seguridad existentes
    private int intentosFallidos = 0;
    private LocalDateTime bloqueadoHasta;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String genero;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String telefonoTutor;
    private String nombreTutor;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNivelDiagnostico() { return nivelDiagnostico; }
    public void setNivelDiagnostico(String nivelDiagnostico) { this.nivelDiagnostico = nivelDiagnostico; }

    public boolean isExamenCompletado() { return examenCompletado; }
    public void setExamenCompletado(boolean examenCompletado) { this.examenCompletado = examenCompletado; }

    public int getPuntosTotales() { return puntosTotales; }
    public void setPuntosTotales(int puntosTotales) { this.puntosTotales = puntosTotales; }

    public List<Modulo> getModulosCompletados() { return modulosCompletados; }
    public void setModulosCompletados(List<Modulo> modulosCompletados) { this.modulosCompletados = modulosCompletados; }

    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getTelefonoTutor() { return telefonoTutor; }
    public void setTelefonoTutor(String telefonoTutor) { this.telefonoTutor = telefonoTutor; }

    public String getNombreTutor() { return nombreTutor; }
    public void setNombreTutor(String nombreTutor) { this.nombreTutor = nombreTutor; }
}
