package com.app.treino.dto;

public record TreinoAssistenteRequest(
        Integer idUsuario,
        String nivel,
        String tipo,
        String grupo,
        String periodo,   // "manha" | "tarde" | "noite" - só contexto da conversa, não é salvo
        String horario     // obrigatório, formato "HH:mm" (ex: "19:30")
) {
}