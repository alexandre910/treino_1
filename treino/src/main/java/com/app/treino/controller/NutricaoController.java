package com.app.treino.controller;

import com.app.treino.service.NutricaoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nutricao")
public class NutricaoController {

    private final NutricaoService service;

    public NutricaoController(NutricaoService service) {
        this.service = service;
    }

    @PostMapping("/perguntar")
    public Map<String, String> perguntar(@RequestBody Pergunta pergunta) {
        String resposta = service.perguntar(pergunta.pergunta());
        return Map.of("resposta", resposta);
    }

    public record Pergunta(String pergunta) {}
}