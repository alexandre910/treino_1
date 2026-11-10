package com.app.treino.service;

import com.app.treino.dto.AgendaResponse;
import com.app.treino.model.Agenda;
import com.app.treino.repository.AgendaRepository;
import com.app.treino.repository.ClienteRepository;
import com.app.treino.repository.TreinoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgendaService {

    private static final int LIMITE_DIAS_AGENDAMENTO = 30;

    private final AgendaRepository repository;
    private final ClienteRepository clientes;
    private final TreinoRepository treinos;
    private final ClienteService clienteService;

    public AgendaService(AgendaRepository repository, ClienteRepository clientes, TreinoRepository treinos,
                          ClienteService clienteService) {
        this.repository = repository;
        this.clientes = clientes;
        this.treinos = treinos;
        this.clienteService = clienteService;
    }

    public List<Agenda> listar() {
        return repository.findAll();
    }

    /** Lista a agenda de um usuário já com a informação de liberado/bloqueado,
     *  seguindo a regra: o primeiro treino sempre começa liberado, e cada
     *  próximo só libera quando o anterior estiver com status CONCLUIDO. */
    public List<AgendaResponse> listarComLiberacao(Integer idUsuario) {
        List<Agenda> itens = repository.findByIdUsuarioOrderByDataAgendadaAsc(idUsuario);
        List<AgendaResponse> resultado = new ArrayList<>();

        boolean anteriorConcluido = true;
        for (Agenda a : itens) {
            boolean liberado = anteriorConcluido;
            resultado.add(new AgendaResponse(
                    a.getId(), a.getDataAgendada(), a.getStatus(), a.getIdUsuario(), a.getIdTreino(), liberado));
            anteriorConcluido = "CONCLUIDO".equalsIgnoreCase(a.getStatus());
        }

        return resultado;
    }

    public Agenda criar(Agenda a) {
        if (a.getIdUsuario() == null || !clientes.existsById(a.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        if (a.getIdTreino() == null || !treinos.existsById(a.getIdTreino())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado");
        }
        if (a.getDataAgendada() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe a data do agendamento");
        }

        LocalDateTime limite = LocalDateTime.now().plusDays(LIMITE_DIAS_AGENDAMENTO);
        if (a.getDataAgendada().isAfter(limite)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só é possível agendar com até " + LIMITE_DIAS_AGENDAMENTO + " dias de antecedência");
        }

        if (a.getStatus() == null || a.getStatus().isBlank()) {
            a.setStatus("AGENDADO");
        }
        a.setId(null);
        return repository.save(a);
    }

    public Agenda atualizarStatus(Integer id, String status) {
        Agenda a = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));
        boolean jaEstavaConcluido = "CONCLUIDO".equalsIgnoreCase(a.getStatus());
        a.setStatus(status);
        Agenda salvo = repository.save(a);

        if (!jaEstavaConcluido && "CONCLUIDO".equalsIgnoreCase(status)) {
            long total = repository.countByIdUsuarioAndStatusIgnoreCase(a.getIdUsuario(), "CONCLUIDO");
            clienteService.avaliarEvolucaoDeNivel(a.getIdUsuario(), total);
        }

        return salvo;
    }
}