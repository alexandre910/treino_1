
package com.app.treino.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class ElevenLabsService {

    private final RestClient client = RestClient.create();

    @Value("${elevenlabs.api-key}")
    private String apiKey;

    @Value("${elevenlabs.voice-id}")
    private String voiceId;

    public byte[] gerarAudio(String texto) {

        return client.post()
                .uri("https://api.elevenlabs.io/v1/text-to-speech/{voiceId}",
                        voiceId)
                .header("xi-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.parseMediaType("audio/mpeg"))
                .body(Map.of(
        "text", texto,
        "model_id", "eleven_multilingual_v2",
        "voice_settings", Map.of(
                "stability", 0.75,
                "similarity_boost", 0.85
        )
))
                .retrieve()
                .body(byte[].class);
    }
}
