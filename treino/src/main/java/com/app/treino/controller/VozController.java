

package com.app.treino.controller;

import com.app.treino.service.ElevenLabsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voz")
public class VozController {

    private final ElevenLabsService elevenLabsService;

    public VozController(ElevenLabsService elevenLabsService) {
        this.elevenLabsService = elevenLabsService;
    }

    @PostMapping("/falar")
    public ResponseEntity<byte[]> falar(@RequestBody Mensagem mensagem) {

        byte[] audio = elevenLabsService.gerarAudio(mensagem.texto());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .body(audio);
    }

    public record Mensagem(String texto) {
    }
}
