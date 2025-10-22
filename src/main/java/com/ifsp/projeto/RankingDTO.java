package com.ifsp.projeto;

public class RankingDTO {
    private String turma;
    private long totalDoacoes;

    public RankingDTO(String turma, long totalDoacoes) {
        this.turma = turma;
        this.totalDoacoes = totalDoacoes;
    }

    public String getTurma() { return turma; }
    public void setTurma(String turma) { this.turma = turma; }

    public long getTotalDoacoes() { return totalDoacoes; }
    public void setTotalDoacoes(long totalDoacoes) { this.totalDoacoes = totalDoacoes; }
}
