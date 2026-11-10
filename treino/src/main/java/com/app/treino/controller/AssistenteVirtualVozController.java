package com.app.treino.controller;

import com.app.treino.dto.NutricaoRequest;
import com.app.treino.dto.PerguntaRequest;
import com.app.treino.model.AssistenteVirtualVoz;
import com.app.treino.service.AssistenteVirtualVozService;
import com.app.treino.service.ElevenLabsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assistente")
public class AssistenteVirtualVozController {

    private final AssistenteVirtualVozService service;
    private final ElevenLabsService voz;

    public AssistenteVirtualVozController(AssistenteVirtualVozService service, ElevenLabsService voz) {
        this.service = service;
        this.voz = voz;
    }

    @GetMapping
    public List<AssistenteVirtualVoz> historico(@RequestParam Integer idUsuario) {
        return service.historico(idUsuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssistenteVirtualVoz perguntar(@RequestBody PerguntaRequest req) {
        return service.responder(req);
    }

    @GetMapping(value = "/{id}/audio", produces = "audio/mpeg")
    public ResponseEntity<byte[]> audio(@PathVariable Integer id) {
        AssistenteVirtualVoz registro = service.buscar(id);
        byte[] bytes = voz.gerarAudio(registro.getResposta());
        return ResponseEntity.ok().contentType(MediaType.valueOf("audio/mpeg")).body(bytes);
    }

    @PostMapping("/nutricao")
    public AssistenteVirtualVoz nutricao(@RequestBody NutricaoRequest req) {
         return service.nutricao(req);
    }
}