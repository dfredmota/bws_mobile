package com.developersd3.bwsmobile.model;

/**
 * Created by fred on 20/04/16.
 */
public class PrazoLocacao {

    private Long id;

    private String descricao;

    private Long   qtdDias;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getQtdDias() {
        return qtdDias;
    }

    public void setQtdDias(Long qtdDias) {
        this.qtdDias = qtdDias;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
