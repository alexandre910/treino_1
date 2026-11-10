package com.app.treino.controller;

import com.app.treino.dto.ExercicioNoTreinoRequest;
import com.app.treino.dto.TreinoAssistenteRequest;
import com.app.treino.dto.TreinoRapidoRequest;
import com.app.treino.dto.TreinoRequest;
import com.app.treino.model.Treino;
import com.app.treino.model.TreinoExercicio;
import com.app.treino.service.TreinoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService service;

    public TreinoController(TreinoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Treino> listar() {
        return service.listar();
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Map<String, Object>> treinosDoUsuario(@PathVariable Integer idUsuario) {
        return service.treinosDoUsuario(idUsuario);
    }

    @GetMapping("/{id}/exercicios")
    public List<Map<String, Object>> exercicios(@PathVariable Integer id) {
        return service.exerciciosDoTreino(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Treino criar(@RequestBody TreinoRequest req) {
        return service.criar(req);
    }

    @PostMapping("/rapido")
    @ResponseStatus(HttpStatus.CREATED)
    public Treino criarRapido(@RequestBody TreinoRapidoRequest req) {
        return service.criarTreinoRapido(req);
    }

    @PostMapping("/assistente")
    @ResponseStatus(HttpStatus.CREATED)
    public Treino criarAssistente(@RequestBody TreinoAssistenteRequest req) {
        return service.criarTreinoAssistente(req);
    }

    @PostMapping("/{id}/exercicios")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoExercicio adicionarExercicio(@PathVariable Integer id, @RequestBody ExercicioNoTreinoRequest req) {
        return service.adicionarExercicio(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        service.excluir(id);
    }
}