package com.app.treino.controller;

import com.app.treino.model.Exercicio;
import com.app.treino.service.ExercicioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    private final ExercicioService service;

    public ExercicioController(ExercicioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Exercicio> listar(@RequestParam(required = false) String grupoMuscular,
                               @RequestParam(required = false) String modalidade,
                               @RequestParam(required = false) Integer idUsuario) {
    return service.listar(grupoMuscular, modalidade, idUsuario);
}

    @GetMapping("/{id}")
    public Exercicio buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Exercicio criar(@RequestBody Exercicio exercicio) {
        return service.criar(exercicio);
    }

    @PutMapping("/{id}")
    public Exercicio atualizar(@PathVariable Integer id, @RequestBody Exercicio exercicio) {
        return service.atualizar(id, exercicio);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        service.excluir(id);
    }
}