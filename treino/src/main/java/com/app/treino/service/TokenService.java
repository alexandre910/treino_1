package com.app.treino.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();

    public String gerarToken(Integer clienteId) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, clienteId);
        return token;
    }

    public Integer buscarClienteId(String token) {
        return tokens.get(token);
    }
}