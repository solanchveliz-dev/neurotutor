package com.neurotutor.user_service.repository;

import com.neurotutor.user_service.model.Tema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemaRepository extends JpaRepository<Tema, Long> {
}