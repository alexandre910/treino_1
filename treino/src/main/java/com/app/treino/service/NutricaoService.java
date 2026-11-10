package com.app.treino.service;

import com.app.treino.dto.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class NutricaoService {

    private static final String URL_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent";

    @Value("${gemini.api-key}")
    private String apiKey;

    private final RestClient client = RestClient.create();

    public String perguntar(String pergunta) {
        String prompt = "Você é o assistente virtual de um app de fitness chamado PulseFit, especializado em nutrição esportiva. " +
                "Responda de forma objetiva e curta (poucos parágrafos), em português, à pergunta do usuário sobre nutrição " +
                "relacionada à prática de exercícios. Sempre termine a resposta com a frase exata: " +
                "\"Procure sempre orientação de um médico ou nutricionista.\"\n\nPergunta: " + pergunta;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        GeminiResponse resposta;
        try {
            resposta = client.post()
                    .uri(URL_BASE + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Gemini respondeu " + e.getStatusCode().value());
        }

        if (resposta == null || resposta.candidates() == null || resposta.candidates().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta vazia da IA");
        }

        List<GeminiResponse.Part> parts = resposta.candidates().get(0).content().parts();
        if (parts == null || parts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resposta vazia da IA");
        }

        return parts.get(0).text();
    }
}