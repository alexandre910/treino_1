package com.app.treino.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agenda")
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agenda")
    private Integer id;

    @Column(name = "data_agendada")
    private LocalDateTime dataAgendada;

    private String status;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_treino")
    private Integer idTreino;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getDataAgendada() { return dataAgendada; }
    public void setDataAgendada(LocalDateTime dataAgendada) { this.dataAgendada= dataAgendada; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public Integer getIdTreino() { return idTreino; }
    public void setIdTreino(Integer idTreino) { this.idTreino = idTreino; }
}