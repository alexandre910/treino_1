package com.app.treino.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assistente_virtual_voz")
public class AssistenteVirtualVoz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_assistente")
    private Integer id;

    @Column(name = "pergunta_usuario")
    private String pergunta;

    @Column(name = "resposta_assistente")
    private String resposta;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }
    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}