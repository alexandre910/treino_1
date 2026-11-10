package com.app.treino.service;

import com.app.treino.dto.ExercicioNoTreinoRequest;
import com.app.treino.dto.TreinoRapidoRequest;
import com.app.treino.dto.TreinoRequest;
import com.app.treino.model.Agenda;
import com.app.treino.model.Exercicio;
import com.app.treino.model.Treino;
import com.app.treino.model.TreinoExercicio;
import com.app.treino.model.TreinoExercicioId;
import com.app.treino.repository.AgendaRepository;
import com.app.treino.repository.AvancadoRepository;
import com.app.treino.repository.ClienteRepository;
import com.app.treino.repository.ExercicioRepository;
import com.app.treino.repository.IntermediarioRepository;
import com.app.treino.repository.TreinoExercicioRepository;
import com.app.treino.repository.TreinoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.app.treino.dto.TreinoAssistenteRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import com.app.treino.repository.InicianteRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class TreinoService {

    private static final int QTD_AQUECIMENTO = 3;
    private static final String[] GRUPOS_TREINO_RAPIDO = {"Peito", "Perna", "Braco", "Costas"};
    private static final String DESCRICAO_TREINO_RAPIDO = "Gerado pelo assistente virtual";

    private final TreinoRepository treinos;
    private final TreinoExercicioRepository itens;
    private final AvancadoRepository avancados;
    private final IntermediarioRepository intermediarios;
    private final ExercicioRepository exercicios;
    private final ClienteRepository clientes;
    private final AgendaRepository agendas;
    private final InicianteRepository iniciantes;

public TreinoService(TreinoRepository treinos, TreinoExercicioRepository itens,
                      AvancadoRepository avancados, IntermediarioRepository intermediarios,
                      InicianteRepository iniciantes,
                      ExercicioRepository exercicios, ClienteRepository clientes,
                      AgendaRepository agendas) {
    this.treinos = treinos;
    this.itens = itens;
    this.avancados = avancados;
    this.intermediarios = intermediarios;
    this.iniciantes = iniciantes;
    this.exercicios = exercicios;
    this.clientes = clientes;
    this.agendas = agendas;
}

    public List<Treino> listar() {
        return treinos.findAll();
    }

    // Treinos do aluno em ordem, com "liberado" e "concluido".
    // O 1º sempre liberado; o próximo só libera quando o anterior tem agenda CONCLUIDO.
    // Treino Rápido do assistente: sempre liberado e fora da sequência.
    public List<Map<String, Object>> treinosDoUsuario(Integer idUsuario) {
        List<Treino> lista = treinos.findAll().stream()
                .filter(t -> idUsuario.equals(t.getIdUsuario()))
                .sorted(Comparator.comparing(Treino::getId))
                .toList();
        List<Agenda> todasAgendas = agendas.findAll();

        List<Map<String, Object>> resposta = new ArrayList<>();
        boolean proximoLiberado = true;
        for (Treino t : lista) {
            boolean concluido = todasAgendas.stream()
                    .anyMatch(a -> t.getId().equals(a.getIdTreino())
                            && "CONCLUIDO".equalsIgnoreCase(a.getStatus()));
            boolean rapido = DESCRICAO_TREINO_RAPIDO.equals(t.getDescricao());

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", t.getId());
            item.put("nome", t.getNome());
            item.put("descricao", t.getDescricao());
            item.put("modalidade", t.getModalidade());
            item.put("nivel", t.getNivel());
            item.put("liberado", rapido || proximoLiberado);
            item.put("concluido", concluido);
            item.put("rapido", rapido);
            item.put("podeExcluir", rapido || t.getIdUsuarioAvancado() != null);
            resposta.add(item);

            if (!rapido) {
                proximoLiberado = proximoLiberado && concluido;
            }
        }
        return resposta;
    }

    public List<Map<String, Object>> exerciciosDoTreino(Integer idTreino) {
        return itens.findAll().stream()
                .filter(te -> te.getId().getIdTreino().equals(idTreino))
                .map(te -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", te.getId());
                    item.put("series", te.getSeries());
                    item.put("repeticoes", te.getRepeticoes());
                    item.put("carga", te.getCarga());
                    item.put("exercicio", exercicios.findById(te.getId().getIdExercicio()).orElse(null));
                    return item;
                })
                .toList();
    }

   public Treino criar(TreinoRequest req) {
    boolean ehAvancado = req.idUsuarioAvancado() != null && avancados.existsById(req.idUsuarioAvancado());
    boolean ehIntermediario = req.idUsuarioAvancado() != null && intermediarios.existsById(req.idUsuarioAvancado());
    boolean ehIniciante = req.idUsuarioAvancado() != null && iniciantes.existsById(req.idUsuarioAvancado());
    boolean ehCalistenia = "calistenia".equalsIgnoreCase(req.modalidade());

    if (req.idUsuarioAvancado() == null || (!ehAvancado && !ehIntermediario && !(ehIniciante && ehCalistenia))) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                ehIniciante
                    ? "Clientes iniciantes só podem criar treino manual de calistenia"
                    : "Só clientes avançados ou intermediários podem criar o próprio treino");
    }

    boolean ehMusculacao = "musculacao".equalsIgnoreCase(req.modalidade());
    if (!ehAvancado && ehMusculacao && "avancado".equalsIgnoreCase(req.nivel())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Treino não disponível para seu nível");
    }

    Treino treino = new Treino();
    treino.setNome(req.nome());
    treino.setDescricao(req.descricao());
    treino.setIdUsuarioAvancado(req.idUsuarioAvancado());
    treino.setIdUsuario(req.idUsuario());
    treino.setModalidade(req.modalidade());
    treino.setNivel(req.nivel());
    Treino treinoSalvo = treinos.save(treino);
    // ... resto do método continua igual (aquecimento, etc.)
    

        List<Exercicio> aquecimento = exercicios.sortearAquecimento(QTD_AQUECIMENTO);
        for (Exercicio ex : aquecimento) {
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(1);
            te.setRepeticoes(1);
            itens.save(te);
        }

        return treinoSalvo;
    }

    public Treino criarTreinoRapido(TreinoRapidoRequest req) {
        if (req.idUsuario() == null || !clientes.existsById(req.idUsuario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }

        Treino treino = new Treino();
        treino.setNome("Treino Rápido");
        treino.setDescricao(DESCRICAO_TREINO_RAPIDO);
        treino.setIdUsuario(req.idUsuario());
        treino.setModalidade("Musculacao");
        treino.setGeradoPeloAssistente(true);
        treino.setNivel(avancados.existsById(req.idUsuario()) ? "avancado" : "iniciante");
        Treino treinoSalvo = treinos.save(treino);

        List<Exercicio> aquecimento = exercicios.sortearAquecimento(QTD_AQUECIMENTO);
        for (Exercicio ex : aquecimento) {
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(1);
            te.setRepeticoes(1);
            itens.save(te);
        }

        for (String grupo : GRUPOS_TREINO_RAPIDO) {
            Exercicio ex = exercicios.sortearPorGrupo(grupo);
            if (ex == null) continue;
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(3);
            te.setRepeticoes(10);
            itens.save(te);
        }

        return treinoSalvo;
    }

    public TreinoExercicio adicionarExercicio(Integer idTreino, ExercicioNoTreinoRequest req) {
        if (!treinos.existsById(idTreino)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado");
        }
        if (req.idExercicio() == null || !exercicios.existsById(req.idExercicio())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado");
        }
        TreinoExercicio te = new TreinoExercicio();
        te.setId(new TreinoExercicioId(idTreino, req.idExercicio()));
        te.setSeries(req.series());
        te.setRepeticoes(req.repeticoes());
        te.setCarga(req.carga());
        return itens.save(te);
    }

    public void excluir(Integer id) {
        if (!treinos.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado");
        }
        List<Agenda> agendasDoTreino = agendas.findAll().stream()
                .filter(a -> id.equals(a.getIdTreino()))
                .toList();
        agendas.deleteAll(agendasDoTreino);

        List<TreinoExercicio> vinculados = itens.findAll().stream()
                .filter(te -> te.getId().getIdTreino().equals(id))
                .toList();
        for (TreinoExercicio te : vinculados) {
            itens.deleteById(te.getId());
        }
        treinos.deleteById(id);
    }

    public Treino criarTreinoAssistente(TreinoAssistenteRequest req) {
        if (req.idUsuario() == null || !clientes.existsById(req.idUsuario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        if (req.nivel() == null || req.tipo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nivel e tipo são obrigatórios");
        }

        Treino treino = new Treino();
        treino.setIdUsuario(req.idUsuario());
        treino.setModalidade("Musculacao");
        treino.setNivel(req.nivel());
        treino.setGeradoPeloAssistente(true);
        treino.setDescricao(DESCRICAO_TREINO_RAPIDO);

        List<Exercicio> selecionados = new ArrayList<>();

        if ("especifico".equalsIgnoreCase(req.tipo())) {
            if (req.grupo() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grupo é obrigatório para treino específico");
            }
            treino.setNome("Treino " + capitalizar(req.grupo()));
            selecionados = exercicios.sortearVariosPorGrupoENiveis(req.grupo(), niveisPermitidos(req.nivel()), 4);
        } else {
            treino.setNome("Treino Variado");
            for (String grupo : GRUPOS_TREINO_RAPIDO) {
                Exercicio ex = exercicios.sortearPorGrupoENiveis(grupo, niveisPermitidos(req.nivel()));
                if (ex != null) selecionados.add(ex);
            }
        }

        if (selecionados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Não há exercícios cadastrados para esse nível/grupo ainda");
        }

        Treino treinoSalvo = treinos.save(treino);

        List<Exercicio> aquecimento = exercicios.sortearAquecimento(QTD_AQUECIMENTO);
        for (Exercicio ex : aquecimento) {
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(1);
            te.setRepeticoes(1);
            itens.save(te);
        }

        for (Exercicio ex : selecionados) {
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(3);
            te.setRepeticoes(10);
            itens.save(te);
        }

        if (req.horario() == null || req.horario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um horário para agendar o treino");
        }
        LocalTime horaEscolhida;
        try {
            horaEscolhida = LocalTime.parse(req.horario());
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horário inválido, use o formato HH:mm (ex: 19:30)");
        }

        Agenda agenda = new Agenda();
        agenda.setIdUsuario(req.idUsuario());
        agenda.setIdTreino(treinoSalvo.getId());
        agenda.setDataAgendada(LocalDateTime.of(LocalDate.now(), horaEscolhida));
        agenda.setStatus("AGENDADO");
        agendas.save(agenda);

        return treinoSalvo;
    }

    private String capitalizar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private List<String> niveisPermitidos(String nivel) {
        if (nivel == null) return List.of("iniciante");
        return switch (nivel.toLowerCase()) {
            case "intermediario" -> List.of("iniciante", "intermediario");
            case "avancado" -> List.of("iniciante", "intermediario", "avancado");
            default -> List.of("iniciante");
        };
    }
}