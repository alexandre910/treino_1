package com.app.treino.service;

/*
 * Desativado: a integração com a WorkoutX API não é mais usada.
 * Os exercícios agora são cadastrados manualmente pela própria API (/api/exercicios).
 *
import com.app.treino.dto.WorkoutXResposta;
import com.app.treino.model.Exercicio;
import com.app.treino.repository.ExercicioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkoutXService {

    private static final String URL = "https://api.workoutxapp.com/v1/exercises";

    private final ExercicioRepository repository;
    private final RestClient client=RestClient.create();

    @Value("${workoutx.api-key}")
    private String apiKey;

    public WorkoutXService(ExercicioRepository repository) {
        this.repository = repository;
        
    }

    public int importar() {
        WorkoutXResposta resposta;
        try {
            resposta = client.get()
                    .uri(URL)
                    .header("X-WorkoutX-Key", apiKey)
                    .retrieve()
                    .body(WorkoutXResposta.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "WorkoutX respondeu " + e.getStatusCode().value());
        }

        if (resposta == null || resposta.data() == null) {
            return 0;
        }

        int salvos = 0;
        for (var item : resposta.data()) {
            if (item.name() == null || repository.existsByNome(item.name())) {
                continue;
            }
            String texto = (item.instructions() != null && !item.instructions().isEmpty())
                    ? String.join("\n", item.instructions())
                    : item.description();

            Exercicio e = new Exercicio();
            e.setNome(item.name());
            e.setGrupoMuscular(item.target());
            e.setDescricao(cortar(texto, 2000));
            e.setDemonstracaoVideo(item.gifUrl());
            repository.save(e);
            salvos++;
        }
        return salvos;
    }

    private String cortar(String texto, int max) {
        if (texto == null) return null;
        return texto.length() > max ? texto.substring(0, max) : texto;
    }
}
*/