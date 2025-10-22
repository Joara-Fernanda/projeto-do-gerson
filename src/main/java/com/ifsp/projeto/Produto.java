package com.ifsp.projeto;

import jakarta.persistence.*;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;
    private String descricao;
    private String categoria;
    private String emailDoador;
    private String telefoneDoador;
    private String turma;

    @Lob
    private String imagem;

    @Column(nullable = false)
    private boolean doado = false;

    @Column(nullable = false)
    private boolean removido = false; // NOVO CAMPO para controle de visibilidade e reset

    // ================= GETTERS E SETTERS =================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEmailDoador() { return emailDoador; }
    public void setEmailDoador(String emailDoador) { this.emailDoador = emailDoador; }

    public String getTelefoneDoador() { return telefoneDoador; }
    public void setTelefoneDoador(String telefoneDoador) { this.telefoneDoador = telefoneDoador; }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public boolean isDoado() { return doado; }
    public void setDoado(boolean doado) { this.doado = doado; }

    public boolean isRemovido() { return removido; }
    public void setRemovido(boolean removido) { this.removido = removido; }

    
}
