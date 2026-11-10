package com.app.treino.repository;

import com.app.treino.model.Avancado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvancadoRepository extends JpaRepository<Avancado, Integer> {
}