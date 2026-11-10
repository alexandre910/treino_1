package com.app.treino.repository;

import com.app.treino.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Integer> {

    java.util.List<com.app.treino.model.Agenda> findByIdUsuarioOrderByDataAgendadaAsc(Integer idUsuario);

    long countByIdUsuarioAndStatusIgnoreCase(Integer idUsuario, String status);
}