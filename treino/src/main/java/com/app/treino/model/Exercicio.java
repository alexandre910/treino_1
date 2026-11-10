package com.app.treino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exercicios")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercicio")
    private Integer id;

    @Column(name = "nome_exercicio")
    private String nome;

    @Column(name = "grupo_muscular")
    private String grupoMuscular;

    @Column(name = "descricao_exercicio")
    private String descricao;

    @Column(name = "demonstracao_video")
    private String demonstracaoVideo;

    private String modalidade;

    private String nivel;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDemonstracaoVideo() { return demonstracaoVideo; }
    public void setDemonstracaoVideo(String demonstracaoVideo) { this.demonstracaoVideo = demonstracaoVideo; }
    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
}