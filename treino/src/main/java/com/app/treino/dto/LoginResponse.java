package com.app.treino.dto;

import com.app.treino.model.Cliente;

public record LoginResponse(
        Cliente cliente,
        boolean questionarioPendente,
        String token
) {}