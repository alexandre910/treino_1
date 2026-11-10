package com.app.treino.repository;

import com.app.treino.model.TreinoExercicio;
import com.app.treino.model.TreinoExercicioId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoExercicioRepository extends JpaRepository<TreinoExercicio, TreinoExercicioId> {
}