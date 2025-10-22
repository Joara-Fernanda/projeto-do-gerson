package com.ifsp.projeto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByCategoriaIgnoreCase(String categoria);

    List<Produto> findByCategoriaIgnoreCaseAndNomeContainingIgnoreCase(String categoria, String nome);

    // Busca produtos de um doador específico
    List<Produto> findByEmailDoador(String emailDoador);

    // Busca produtos de um doador que ainda não foram removidos
    List<Produto> findByEmailDoadorAndRemovidoFalse(String emailDoador);

    // BUSCAS COM FILTRO AUTOMÁTICO DE REMOVIDO
    List<Produto> findByNomeContainingIgnoreCaseAndRemovidoFalse(String nome);

    List<Produto> findByCategoriaIgnoreCaseAndRemovidoFalse(String categoria);

    List<Produto> findByCategoriaIgnoreCaseAndNomeContainingIgnoreCaseAndRemovidoFalse(String categoria, String nome);

    List<Produto> findAllByRemovidoFalse(); // ESTE É O MÉTODO QUE ESTAVA DANDO ERRO
}
