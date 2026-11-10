package com.app.treino.service;

import com.app.treino.dto.NutricaoRequest;
import com.app.treino.dto.PerguntaRequest;
import com.app.treino.model.Agenda;
import com.app.treino.model.AssistenteVirtualVoz;
import com.app.treino.model.Cliente;
import com.app.treino.repository.AgendaRepository;
import com.app.treino.repository.AssistenteVirtualVozRepository;
import com.app.treino.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AssistenteVirtualVozService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static final String AVISO_ESPECIALISTA =
            "\n\n⚠️ Isso é apenas uma informação geral. Procure um nutricionista ou especialista para uma orientação personalizada.";

    private final AssistenteVirtualVozRepository repository;
    private final ClienteRepository clientes;
    private final AgendaRepository agendas;
    private final GeminiService gemini;

    public AssistenteVirtualVozService(AssistenteVirtualVozRepository repository,
                                       ClienteRepository clientes, AgendaRepository agendas,
                                       GeminiService gemini) {
        this.repository = repository;
        this.clientes = clientes;
        this.agendas = agendas;
        this.gemini = gemini;
    }

    public List<AssistenteVirtualVoz> historico(Integer idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }

    public AssistenteVirtualVoz buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }

    public AssistenteVirtualVoz responder(PerguntaRequest req) {
        Cliente cliente = clientes.findById(req.idUsuario()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (req.pergunta() == null || req.pergunta().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe a pergunta");
        }

        String resposta = gerarResposta(cliente, req.pergunta().toLowerCase());

        AssistenteVirtualVoz registro = new AssistenteVirtualVoz();
        registro.setIdUsuario(cliente.getId());
        registro.setPergunta(req.pergunta());
        registro.setResposta(resposta);
        return repository.save(registro);
    }

    private String gerarResposta(Cliente cliente, String texto) {
        if (texto.contains("nivel") || texto.contains("nível")) {
            return "Seu nível atual é: " + cliente.getNivelExperiencia() + ".";
        }

        if (texto.contains("aquecimento")) {
            return "Antes de treinar, faça de 5 a 10 minutos de aquecimento: "
                    + "caminhada ou bicicleta leve, seguido de alongamento dinâmico dos músculos que você vai trabalhar.";
        }

        if (texto.contains("agenda") || texto.contains("agendar") || texto.contains("horário") || texto.contains("aula")) {
            List<Agenda> proximas = agendas.findByIdUsuarioOrderByDataAgendadaAsc(cliente.getId());
            if (proximas.isEmpty()) {
                return "Você ainda não tem nenhuma aula agendada.";
            }
            Agenda proxima = proximas.get(0);
            return "Sua próxima aula está agendada para " + proxima.getDataAgendada().format(FORMATO_DATA)
                    + ", status: " + proxima.getStatus() + ".";
        }

        return "Desculpe, não entendi. Você pode perguntar sobre seu nível, aquecimento ou sua próxima aula agendada.";
    }

    public AssistenteVirtualVoz nutricao(NutricaoRequest req) {
        Cliente cliente = clientes.findById(req.idUsuario()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        if (req.etapa() == null || req.etapa().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe a etapa (inicio ou escolha)");
        }

        String pergunta;
        String resposta;

        if ("inicio".equalsIgnoreCase(req.etapa())) {
            pergunta = "Nutrição";
            String prompt = "Explique de forma breve e motivadora (máximo 4 frases) a importância da nutrição "
                    + "para a saúde, musculação e calistenia. Não dê recomendações específicas de dieta ainda, "
                    + "apenas a importância geral. Termine perguntando se o objetivo da pessoa é ganhar massa muscular ou emagrecer.";
            resposta = gemini.gerar(prompt);

        } else if ("escolha".equalsIgnoreCase(req.etapa())) {
            String objetivo = normalizarObjetivo(req.objetivo());
            pergunta = "Nutrição - objetivo: " + objetivo;
            String foco = objetivo.equals("massa")
                    ? "ganho de massa muscular (hipertrofia)"
                    : "emagrecimento e perda de gordura";
            String prompt = "Dê uma orientação breve e prática (máximo 6 frases, pode usar tópicos) sobre alimentação "
                    + "para uma pessoa com foco em " + foco + ", que pratica musculação e/ou calistenia. "
                    + "Cite grupos de alimentos, sem quantidades exatas nem marcas.";
            resposta = gemini.gerar(prompt) + AVISO_ESPECIALISTA;

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Etapa inválida, use 'inicio' ou 'escolha'");
        }

        AssistenteVirtualVoz registro = new AssistenteVirtualVoz();
        registro.setIdUsuario(cliente.getId());
        registro.setPergunta(pergunta);
        registro.setResposta(resposta);
        return repository.save(registro);
    }

    private String normalizarObjetivo(String texto) {
        if (texto == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o objetivo");
        String t = texto.toLowerCase();
        if (t.contains("massa") || t.contains("hipertrofia") || t.contains("ganhar")) return "massa";
        if (t.contains("emagre") || t.contains("perder") || t.contains("secar")) return "emagrecimento";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Objetivo deve ser 'massa' ou 'emagrecimento'");
    }
}