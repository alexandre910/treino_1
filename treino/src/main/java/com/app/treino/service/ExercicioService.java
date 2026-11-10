package com.app.treino.service;

import com.app.treino.model.Exercicio;
import com.app.treino.repository.AvancadoRepository;
import com.app.treino.repository.ExercicioRepository;
import com.app.treino.repository.IntermediarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExercicioService {

    private final ExercicioRepository repository;
    private final AvancadoRepository avancados;
    private final IntermediarioRepository intermediarios;

    public ExercicioService(ExercicioRepository repository, AvancadoRepository avancados,
                             IntermediarioRepository intermediarios) {
        this.repository = repository;
        this.avancados = avancados;
        this.intermediarios = intermediarios;
    }

    public List<Exercicio> listar(String grupoMuscular, String modalidade, Integer idUsuario) {
        List<Exercicio> base;
        if (grupoMuscular != null && modalidade != null) {
            base = repository.findByGrupoMuscularIgnoreCaseAndModalidadeIgnoreCase(grupoMuscular, modalidade);
        } else if (grupoMuscular != null) {
            base = repository.findByGrupoMuscularIgnoreCase(grupoMuscular);
        } else if (modalidade != null) {
            base = repository.findByModalidadeIgnoreCase(modalidade);
        } else {
            base = repository.findAll();
        }

        if (idUsuario == null) {
            return base;
        }

        List<String> niveisPermitidos = niveisPermitidos(idUsuario);
        return base.stream()
                .filter(ex -> ex.getNivel() == null || ex.getNivel().isBlank()
                        || niveisPermitidos.contains(ex.getNivel().toLowerCase()))
                .toList();
    }

    private List<String> niveisPermitidos(Integer idUsuario) {
        if (avancados.existsById(idUsuario)) {
            return List.of("iniciante", "intermediario", "avancado");
        }
        if (intermediarios.existsById(idUsuario)) {
            return List.of("iniciante", "intermediario");
        }
        return List.of("iniciante");
    }

    public Exercicio buscar(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercício não encontrado"));
    }

    public Exercicio criar(Exercicio exercicio) {
        exercicio.setId(null);
        return repository.save(exercicio);
    }

    public Exercicio atualizar(Integer id, Exercicio dados) {
        Exercicio atual = buscar(id);
        atual.setNome(dados.getNome());
        atual.setGrupoMuscular(dados.getGrupoMuscular());
        atual.setDescricao(dados.getDescricao());
        atual.setDemonstracaoVideo(dados.getDemonstracaoVideo());
        atual.setModalidade(dados.getModalidade());
        atual.setNivel(dados.getNivel());
        return repository.save(atual);
    }

    public void excluir(Integer id) {
        buscar(id);
        repository.deleteById(id);
    }
}