package com.app.treino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "intermediario")
public class Intermediario {

    @Id
    @Column(name = "id_usuario")
    private Integer id;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}