package com.app.treino.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model:openai/gpt-oss-120b}")
    private String modelo;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String gerar(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        Map<String, Object> body = Map.of(
                "model", modelo,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> requisicao = new HttpEntity<>(body, headers);

        Exception ultimoErro = null;
        long esperaMs = 1500;
        int tentativas = 3;

        for (int i = 1; i <= tentativas; i++) {
            try {
                Map<String, Object> resposta = restTemplate.postForObject(url, requisicao, Map.class);
                return extrairTexto(resposta);
            } catch (org.springframework.web.client.HttpServerErrorException e) {
                ultimoErro = e;
            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
                ultimoErro = e;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar o Groq: " + e.getMessage());
            }

            if (i < tentativas) {
                try {
                    Thread.sleep(esperaMs);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                esperaMs *= 2;
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "O Groq está indisponível no momento, tente novamente em instantes: "
                        + (ultimoErro != null ? ultimoErro.getMessage() : ""));
    }

    @SuppressWarnings("unchecked")
    private String extrairTexto(Map<String, Object> resposta) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) resposta.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O Groq não retornou resposta");
        }
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}