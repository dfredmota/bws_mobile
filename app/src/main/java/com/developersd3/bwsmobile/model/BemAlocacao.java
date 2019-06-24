package com.developersd3.bwsmobile.model;

/**
 * Created by fred on 20/03/16.
 */
public class BemAlocacao {

    private Integer idAlocacao;
    private Integer idBem;
    private Integer qtdAlocada;

    public Integer getIdAlocacao() {
        return idAlocacao;
    }

    public void setIdAlocacao(Integer idAlocacao) {
        this.idAlocacao = idAlocacao;
    }

    public Integer getIdBem() {
        return idBem;
    }

    public void setIdBem(Integer idBem) {
        this.idBem = idBem;
    }

    public Integer getQtdAlocada() {
        return qtdAlocada;
    }

    public void setQtdAlocada(Integer qtdAlocada) {
        this.qtdAlocada = qtdAlocada;
    }
}
