package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    // 🚀 Busca ejercicios normales o de examen para un nivel específico
    List<Ejercicio> findByModuloIdAndEsExamenFinal(Long moduloId, boolean esExamenFinal);
    long countByModuloIdAndEsExamenFinal(Long moduloId, boolean esExamenFinal);
}
