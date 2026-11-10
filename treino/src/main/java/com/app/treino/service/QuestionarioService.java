package com.app.treino.service;

import com.app.treino.model.Exercicio;
import com.app.treino.model.Questionario;
import com.app.treino.model.Treino;
import com.app.treino.model.TreinoExercicio;
import com.app.treino.model.TreinoExercicioId;
import com.app.treino.repository.ClienteRepository;
import com.app.treino.repository.ExercicioRepository;
import com.app.treino.repository.InicianteRepository;
import com.app.treino.repository.QuestionarioRepository;
import com.app.treino.repository.TreinoExercicioRepository;
import com.app.treino.repository.TreinoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuestionarioService {

    private static final Pattern NUMERO = Pattern.compile("\\d+");
    private static final int MAX_EXERCICIOS_POR_TREINO = 6;
    private static final int QTD_AQUECIMENTO = 3;
    private static final String[] LETRAS = {"A", "B", "C", "D", "E", "F", "G"};

    private final QuestionarioRepository repository;
    private final ClienteRepository clientes;
    private final InicianteRepository iniciantes;
    private final TreinoRepository treinos;
    private final TreinoExercicioRepository treinoExercicios;
    private final ExercicioRepository exercicios;

    public QuestionarioService(QuestionarioRepository repository, ClienteRepository clientes,
                                InicianteRepository iniciantes, TreinoRepository treinos,
                                TreinoExercicioRepository treinoExercicios, ExercicioRepository exercicios) {
        this.repository = repository;
        this.clientes = clientes;
        this.iniciantes = iniciantes;
        this.treinos = treinos;
        this.treinoExercicios = treinoExercicios;
        this.exercicios = exercicios;
    }

    public List<Questionario> listar() {
        return repository.findAll();
    }

    @Transactional
    public Questionario criar(Questionario q) {
        if (q.getIdUsuario() == null || !clientes.existsById(q.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        if (!iniciantes.existsById(q.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só clientes iniciantes respondem questionário (avançados/intermediários montam o próprio treino)");
        }

       String objetivo = normalizar(q.getObjetivo());
boolean emagrecimento = "emagrecer".equals(objetivo) || "emagrecimento".equals(objetivo);
boolean massa = "massa".equals(objetivo) || "massa muscular".equals(objetivo) || "ganhar massa".equals(objetivo);
if (!emagrecimento && !massa) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "objetivo deve ser 'emagrecer' ou 'massa'");
}
q.setObjetivo(emagrecimento ? "emagrecer" : "massa");

        q.setId(null);
        Questionario salvo = repository.save(q);

        gerarTreinos(salvo);

        return salvo;
    }

   private void gerarTreinos(Questionario questionario) {
    int[] seriesReps = seriesRepsPorObjetivo(questionario.getObjetivo());

    List<Exercicio> peito = exercicios.sortearVariosPorGrupoENivel("Peito", "iniciante", 3);
    List<Exercicio> bracoAB = exercicios.sortearVariosPorGrupoENivel("Braco", "iniciante", 4);
    List<Exercicio> bracoA = bracoAB.size() >= 2 ? bracoAB.subList(0, 2) : bracoAB;
    List<Exercicio> bracoB = bracoAB.size() >= 4 ? bracoAB.subList(2, 4) : List.of();
    List<Exercicio> costas = exercicios.sortearVariosPorGrupoENivel("Costas", "iniciante", 3);
    List<Exercicio> perna = exercicios.sortearVariosPorGrupoENivel("Perna", "iniciante", 6);

    List<Exercicio> treinoAExercicios = new ArrayList<>();
    treinoAExercicios.addAll(peito);
    treinoAExercicios.addAll(bracoA);

    List<Exercicio> treinoBExercicios = new ArrayList<>();
    treinoBExercicios.addAll(costas);
    treinoBExercicios.addAll(bracoB);

    List<Exercicio> treinoCExercicios = perna;

    String[] nomes = {"Treino A", "Treino B", "Treino C"};
    List<List<Exercicio>> gruposPorTreino = List.of(treinoAExercicios, treinoBExercicios, treinoCExercicios);

    for (int i = 0; i < nomes.length; i++) {
        Treino treino = new Treino();
        treino.setNome(nomes[i]);
        treino.setDescricao("Gerado automaticamente a partir do questionário");
        treino.setIdQuestionario(questionario.getId());
        treino.setIdUsuario(questionario.getIdUsuario());
        treino.setModalidade("Musculacao");
        treino.setNivel("iniciante");
        Treino treinoSalvo = treinos.save(treino);

        List<Exercicio> aquecimento = exercicios.sortearAquecimento(QTD_AQUECIMENTO);
        for (Exercicio ex : aquecimento) {
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(1);
            te.setRepeticoes(1);
            treinoExercicios.save(te);
        }

        for (Exercicio ex : gruposPorTreino.get(i)) {
            TreinoExercicio te = new TreinoExercicio();
            te.setId(new TreinoExercicioId(treinoSalvo.getId(), ex.getId()));
            te.setSeries(seriesReps[0]);
            te.setRepeticoes(seriesReps[1]);
            treinoExercicios.save(te);
        }
    }
}

    private int[] seriesRepsPorObjetivo(String objetivo) {
        if ("emagrecer".equals(objetivo)) {
            return new int[]{3, 15};
        } else if ("massa".equals(objetivo)) {
            return new int[]{4, 8};
        }
        return new int[]{3, 10};
    }

    private int extrairNumero(String texto, int padrao) {
        if (texto == null) return padrao;
        Matcher m = NUMERO.matcher(texto);
        return m.find() ? Integer.parseInt(m.group()) : padrao;
    }

    private String normalizar(String texto) {
        if (texto == null) return null;
        String semAcento = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return semAcento.trim().toLowerCase();
    }
}