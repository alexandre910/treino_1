package com.app.treino.controller;

import com.app.treino.model.Questionario;
import com.app.treino.service.QuestionarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionarios")
public class QuestionarioController {

    private final QuestionarioService service;

    public QuestionarioController(QuestionarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Questionario> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Questionario criar(@RequestBody Questionario q) {
        return service.criar(q);
    }
}