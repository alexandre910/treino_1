package com.app.treino.repository;

import com.app.treino.model.Questionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionarioRepository extends JpaRepository<Questionario, Integer> {
    boolean existsByIdUsuario(Integer idUsuario);
}