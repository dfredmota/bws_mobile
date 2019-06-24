package com.developersd3.bwsmobile.model;

import java.util.Date;

/**
 * Created by fred on 20/03/16.
 */
public class ControleAlocacao {

    private Integer   id;
    private String    dataTransferencia;
    private Integer   entregadorId;
    private Integer   recebedorId;
    private Integer   centroCustoId;
    private Integer   tipoAlocacao;
    private String    endereco;

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(String dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public Integer getEntregadorId() {
        return entregadorId;
    }

    public void setEntregadorId(Integer entregadorId) {
        this.entregadorId = entregadorId;
    }

    public Integer getRecebedorId() {
        return recebedorId;
    }

    public void setRecebedorId(Integer recebedorId) {
        this.recebedorId = recebedorId;
    }

    public Integer getCentroCustoId() {
        return centroCustoId;
    }

    public void setCentroCustoId(Integer centroCustoId) {
        this.centroCustoId = centroCustoId;
    }

    public Integer getTipoAlocacao() {
        return tipoAlocacao;
    }

    public void setTipoAlocacao(Integer tipoAlocacao) {
        this.tipoAlocacao = tipoAlocacao;
    }

}
