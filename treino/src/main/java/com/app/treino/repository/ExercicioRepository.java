package com.app.treino.repository;

import com.app.treino.model.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExercicioRepository extends JpaRepository<Exercicio, Integer> {
    boolean existsByNome(String nome);

    List<Exercicio> findByGrupoMuscularIgnoreCase(String grupoMuscular);

    List<Exercicio> findByModalidadeIgnoreCase(String modalidade);

    List<Exercicio> findByGrupoMuscularIgnoreCaseAndModalidadeIgnoreCase(String grupoMuscular, String modalidade);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade IS NULL OR modalidade <> 'Aquecimento' ORDER BY RAND() LIMIT :qtd", nativeQuery = true)
    List<Exercicio> sortearAleatorios(@Param("qtd") int qtd);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Aquecimento' ORDER BY RAND() LIMIT :qtd", nativeQuery = true)
    List<Exercicio> sortearAquecimento(@Param("qtd") int qtd);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Musculacao' AND grupo_muscular = :grupo ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Exercicio sortearPorGrupo(@Param("grupo") String grupo);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Musculacao' AND grupo_muscular = :grupo AND nivel = :nivel ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Exercicio sortearPorGrupoENivel(@Param("grupo") String grupo, @Param("nivel") String nivel);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Musculacao' AND grupo_muscular = :grupo AND nivel = :nivel ORDER BY RAND() LIMIT :qtd", nativeQuery = true)
    List<Exercicio> sortearVariosPorGrupoENivel(@Param("grupo") String grupo, @Param("nivel") String nivel, @Param("qtd") int qtd);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Musculacao' AND nivel = :nivel ORDER BY RAND() LIMIT :qtd", nativeQuery = true)
    List<Exercicio> sortearAleatoriosPorNivel(@Param("nivel") String nivel, @Param("qtd") int qtd);

   
    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Musculacao' AND grupo_muscular = :grupo AND nivel IN :niveis ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Exercicio sortearPorGrupoENiveis(@Param("grupo") String grupo, @Param("niveis") List<String> niveis);

    @Query(value = "SELECT * FROM exercicios WHERE modalidade = 'Musculacao' AND grupo_muscular = :grupo AND nivel IN :niveis ORDER BY RAND() LIMIT :qtd", nativeQuery = true)
    List<Exercicio> sortearVariosPorGrupoENiveis(@Param("grupo") String grupo, @Param("niveis") List<String> niveis, @Param("qtd") int qtd);
}