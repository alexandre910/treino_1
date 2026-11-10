package com.app.treino.controller;

/*
 * Desativado: a integração com a WorkoutX API não é mais usada.
 * Os exercícios agora são cadastrados manualmente pela própria API (/api/exercicios).
 *
import com.app.treino.service.WorkoutXService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workoutx")
public class WorkoutXController {

    private final WorkoutXService service;

    public WorkoutXController(WorkoutXService service) {
        this.service = service;
    }

    @PostMapping("/importar")
    public Map<String, Integer> importar() {
        return Map.of("importados", service.importar());
    }
}
*/