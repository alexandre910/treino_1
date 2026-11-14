package com.app.treino.service;

import com.app.treino.dto.LoginResponse;
import com.app.treino.model.Avancado;
import com.app.treino.model.Cliente;
import com.app.treino.model.Iniciante;
import com.app.treino.model.Intermediario;
import com.app.treino.repository.AvancadoRepository;
import com.app.treino.repository.ClienteRepository;
import com.app.treino.repository.InicianteRepository;
import com.app.treino.repository.IntermediarioRepository;
import com.app.treino.repository.QuestionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.Normalizer;
import java.util.List;

@Service
public class ClienteService {

    private static final int IDADE_MINIMA = 13;
    private static final long SESSOES_PARA_INTERMEDIARIO = 10;
    private static final long SESSOES_PARA_AVANCADO = 15;

    private final ClienteRepository repository;
    private final InicianteRepository iniciantes;
    private final IntermediarioRepository intermediarios;
    private final AvancadoRepository avancados;
    private final QuestionarioRepository questionarios;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public ClienteService(ClienteRepository repository, InicianteRepository iniciantes,
                           IntermediarioRepository intermediarios, AvancadoRepository avancados,
                           QuestionarioRepository questionarios) {
        this.repository = repository;
        this.iniciantes = iniciantes;
        this.intermediarios = intermediarios;
        this.avancados = avancados;
        this.questionarios = questionarios;
    }

    public List<Cliente> listar() {
        return repository.findAll();
    }

    public Cliente buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private String normalizar(String texto) {
        if (texto == null) return null;
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return semAcento.trim().toLowerCase();
    }

    public Cliente criar(Cliente cliente) {
        if (repository.existsByEmail(cliente.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        if (cliente.getIdade() == null || cliente.getIdade() < IDADE_MINIMA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "É necessário ter pelo menos " + IDADE_MINIMA + " anos para se cadastrar");
        }

        if (cliente.getSenha() == null || !cliente.getSenha().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "A senha deve ter pelo menos 8 caracteres, com letras, números e um símbolo");
        }

        String nivel = normalizar(cliente.getNivelExperiencia());
        if (nivel == null || (!nivel.equals("iniciante") && !nivel.equals("intermediario") && !nivel.equals("avancado"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "nivelExperiencia deve ser 'iniciante', 'intermediario' ou 'avancado'");
        }
        cliente.setNivelExperiencia(nivel);

        cliente.setSenha(encoder.encode(cliente.getSenha()));

        cliente.setId(null);
        Cliente salvo = repository.save(cliente);

        if (nivel.equals("iniciante")) {
            Iniciante i = new Iniciante();
            i.setId(salvo.getId());
            iniciantes.save(i);
        } else if (nivel.equals("intermediario")) {
            Intermediario im = new Intermediario();
            im.setId(salvo.getId());
            intermediarios.save(im);
        } else {
            Avancado a = new Avancado();
            a.setId(salvo.getId());
            avancados.save(a);
        }

        return salvo;
    }

    public LoginResponse login(String email, String senha) {
        Cliente cliente = repository.findByEmail(email)
                .filter(c -> encoder.matches(senha, c.getSenha()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));
        cliente.setSenha(null);

        boolean questionarioPendente = "iniciante".equalsIgnoreCase(cliente.getNivelExperiencia())
                && !questionarios.existsByIdUsuario(cliente.getId());

        return new LoginResponse(cliente, questionarioPendente);
    }

    public Cliente avaliarEvolucaoDeNivel(Integer idUsuario, long totalSessoesConcluidas) {
        Cliente cliente = buscar(idUsuario);
        String nivelAtual = cliente.getNivelExperiencia();

        if ("iniciante".equalsIgnoreCase(nivelAtual) && totalSessoesConcluidas >= SESSOES_PARA_INTERMEDIARIO) {
            iniciantes.deleteById(cliente.getId());
            Intermediario im = new Intermediario();
            im.setId(cliente.getId());
            intermediarios.save(im);
            cliente.setNivelExperiencia("intermediario");
            cliente = repository.save(cliente);
            nivelAtual = "intermediario";
        }

        if ("intermediario".equalsIgnoreCase(nivelAtual) && totalSessoesConcluidas >= SESSOES_PARA_AVANCADO) {
            intermediarios.deleteById(cliente.getId());
            Avancado a = new Avancado();
            a.setId(cliente.getId());
            avancados.save(a);
            cliente.setNivelExperiencia("avancado");
            cliente = repository.save(cliente);
        }

        return cliente;
    }
}