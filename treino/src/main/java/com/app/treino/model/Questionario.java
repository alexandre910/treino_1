package com.app.treino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questionario")
public class Questionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_questionario")
    private Integer id;

    private String objetivo;

    private String frequencia;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getFrequencia() { return frequencia; }
    public void setFrequencia(String frequencia) { this.frequencia = frequencia; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}