package com.app.treino.repository;

import com.app.treino.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoRepository extends JpaRepository<Treino, Integer> {
}