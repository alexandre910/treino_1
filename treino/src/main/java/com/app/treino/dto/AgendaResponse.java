package com.app.treino.dto;

import java.time.LocalDateTime;

public record AgendaResponse(
        Integer id,
        LocalDateTime dataAgendada,
        String status,
        Integer idUsuario,
        Integer idTreino,
        boolean liberado
) {}