package com.app.treino.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "treino_exercicio")
public class TreinoExercicio {

    @EmbeddedId
    private TreinoExercicioId id;

    private Integer series;

    private Integer repeticoes;

    private BigDecimal carga;

    public TreinoExercicioId getId() { return id; }
    public void setId(TreinoExercicioId id) { this.id = id; }
    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public BigDecimal getCarga() { return carga; }
    public void setCarga(BigDecimal carga) { this.carga = carga; }
}