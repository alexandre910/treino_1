package com.app.treino.dto;

public record PerguntaRequest(
        Integer idUsuario,
        String pergunta
) {}