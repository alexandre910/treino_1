package com.app.treino.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TreinoExercicioId implements Serializable {

    @Column(name = "id_treino")
    private Integer idTreino;

    @Column(name = "id_exercicio")
    private Integer idExercicio;

    public TreinoExercicioId() {}

    public TreinoExercicioId(Integer idTreino, Integer idExercicio) {
        this.idTreino = idTreino;
        this.idExercicio = idExercicio;
    }

    public Integer getIdTreino() { return idTreino; }
    public void setIdTreino(Integer idTreino) { this.idTreino = idTreino; }
    public Integer getIdExercicio() { return idExercicio; }
    public void setIdExercicio(Integer idExercicio) { this.idExercicio = idExercicio; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreinoExercicioId that)) return false;
        return Objects.equals(idTreino, that.idTreino) && Objects.equals(idExercicio, that.idExercicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTreino, idExercicio);
    }
}