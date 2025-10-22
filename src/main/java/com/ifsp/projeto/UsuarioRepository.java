package com.ifsp.projeto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // ================= BUSCAS =================

    // Buscar usuário por email
    Usuario findByEmail(String email);

    // Buscar usuários cujo nome contenha uma string (ignore case)
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // Buscar todos os usuários por tipo (ex: doador ou beneficiado)
    List<Usuario> findByTipo(String tipo);

    // ================= ATUALIZAÇÃO =================

    // Atualizar senha de um usuário por email
    @Modifying
    @Transactional
    @Query("UPDATE Usuario u SET u.senha = :senhaNova WHERE u.email = :email")
    void updateSenha(String email, String senhaNova);
}

    