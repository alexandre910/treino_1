package com.app.treino.controller;

import com.app.treino.dto.AgendaResponse;
import com.app.treino.model.Agenda;
import com.app.treino.service.AgendaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    private final AgendaService service;

    public AgendaController(AgendaService service) {
        this.service = service;
    }

    @GetMapping
    public List<AgendaResponse> listar(@RequestParam Integer idUsuario) {
        return service.listarComLiberacao(idUsuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agenda criar(@RequestBody Agenda a) {
        return service.criar(a);
    }

    @PatchMapping("/{id}/status")
    public Agenda atualizarStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return service.atualizarStatus(id, body.get("status"));
    }
}