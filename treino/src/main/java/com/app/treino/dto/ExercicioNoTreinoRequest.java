package com.app.treino.dto;

import java.math.BigDecimal;

public record ExercicioNoTreinoRequest(
        Integer idExercicio,
        Integer series,
        Integer repeticoes,
        BigDecimal carga
) {}