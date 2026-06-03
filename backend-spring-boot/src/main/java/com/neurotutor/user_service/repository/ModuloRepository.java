package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    // 🚀 Filtra automáticamente los temas permitidos para el nivel del estudiante
    List<Modulo> findByNivelRequerido(String nivelRequerido);
}