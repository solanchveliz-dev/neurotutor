package com.neurotutor.user_service.model;

import jakarta.persistence.*;import java.util.List;

@Entity
@Table(name = "ejercicios")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "modulo_id")
    private Modulo modulo; // El nivel (B, I o A)

    private String enunciado;

    // 🚀 NUEVO: Soporte para problemas visuales (Gráficos, rectas, figuras)
    private String imagenUrl;

    @ElementCollection
    private List<String> opciones; // Las 4 opciones

    private int respuestaCorrectaIndex;

    // 🚀 NUEVO: Identifica la habilidad específica (Ej: "Simplificación", "MCD")
    // Esto alimenta la HU-25 (Recomendaciones Personalizadas)
    private String subtema;

    // 🚀 NUEVO: Orden secuencial del 1 al 10
    private int orden;

    @Column(length = 1000)
    private String explicacionTutorIa; // HU-22: El "cerebro" del Tutor IA

    private int puntos = 10;

    private boolean esExamenFinal = false; // HU-23

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Modulo getModulo() { return modulo; }
    public void setModulo(Modulo modulo) { this.modulo = modulo; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) { this.opciones = opciones; }

    public int getRespuestaCorrectaIndex() { return respuestaCorrectaIndex; }
    public void setRespuestaCorrectaIndex(int respuestaCorrectaIndex) { this.respuestaCorrectaIndex = respuestaCorrectaIndex; }

    public String getSubtema() { return subtema; }
    public void setSubtema(String subtema) { this.subtema = subtema; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public String getExplicacionTutorIa() { return explicacionTutorIa; }
    public void setExplicacionTutorIa(String explicacionTutorIa) { this.explicacionTutorIa = explicacionTutorIa; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public boolean isEsExamenFinal() { return esExamenFinal; }
    public void setEsExamenFinal(boolean esExamenFinal) { this.esExamenFinal = esExamenFinal; }
}
