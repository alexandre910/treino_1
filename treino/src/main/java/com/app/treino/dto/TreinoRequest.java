package com.app.treino.dto;

public record TreinoRequest(
        String nome,
        String descricao,
        String modalidade,
        String nivel,
        Integer idUsuario,
        Integer idUsuarioAvancado
) {}