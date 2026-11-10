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

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-3.8-flash}")
    private String modelo;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")    

   public String gerar(String prompt) {
    String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelo
            + ":generateContent?key=" + apiKey;

    Map<String, Object> body = Map.of(
            "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            )
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> requisicao = new HttpEntity<>(body, headers);

    int tentativas = 3;
    Exception ultimoErro = null;

    for (int i = 1; i <= tentativas; i++) {
        try {
            Map<String, Object> resposta = restTemplate.postForObject(url, requisicao, Map.class);
            return extrairTexto(resposta);
        } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable e) {
            ultimoErro = e;
            try { Thread.sleep(2000); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar o Gemini: " + e.getMessage());
        }
    }
    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
            "O Gemini está sobrecarregado no momento, tente novamente em instantes: " + ultimoErro.getMessage());
}

    @SuppressWarnings("unchecked")
    private String extrairTexto(Map<String, Object> resposta) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) resposta.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "O Gemini não retornou resposta");
        }
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}