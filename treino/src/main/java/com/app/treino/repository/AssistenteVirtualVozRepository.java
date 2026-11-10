package com.app.treino.repository;

import com.app.treino.model.AssistenteVirtualVoz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssistenteVirtualVozRepository extends JpaRepository<AssistenteVirtualVoz, Integer> {
    List<AssistenteVirtualVoz> findByIdUsuario(Integer idUsuario);
}