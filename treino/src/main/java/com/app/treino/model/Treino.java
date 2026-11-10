package com.app.treino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "treino")
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_treino")
    private Integer id;

    @Column(name = "nome_treino")
    private String nome;

    @Column(name = "descricao_treino")
    private String descricao;

    @Column(name = "id_questionario")
    private Integer idQuestionario;

    @Column(name = "id_usuario_avancado")
    private Integer idUsuarioAvancado;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String modalidade;

    private String nivel;

    @Column(name = "gerado_pelo_assistente")
    private Boolean geradoPeloAssistente;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getIdQuestionario() { return idQuestionario; }
    public void setIdQuestionario(Integer idQuestionario) { this.idQuestionario = idQuestionario; }
    public Integer getIdUsuarioAvancado() { return idUsuarioAvancado; }
    public void setIdUsuarioAvancado(Integer idUsuarioAvancado) { this.idUsuarioAvancado = idUsuarioAvancado; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public Boolean getGeradoPeloAssistente() { return geradoPeloAssistente; }
    public void setGeradoPeloAssistente(Boolean geradoPeloAssistente) { this.geradoPeloAssistente = geradoPeloAssistente; }
}