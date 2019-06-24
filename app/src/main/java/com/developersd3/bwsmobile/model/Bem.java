package com.developersd3.bwsmobile.model;

import java.util.Date;

/**
 * Created by fred on 29/02/16.
 */
public class Bem {

    private Integer  codigo;
    private String   insumo;
    private Integer   tipoBem;
    private Integer   qtdDisponivel;
    private Integer   qtdDevolvida;
    private Integer   quantidade;
    boolean selected = false;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getQtdDevolvida() {
        return qtdDevolvida;
    }

    public void setQtdDevolvida(Integer qtdDevolvida) {
        this.qtdDevolvida = qtdDevolvida;
    }

    public Integer getQtdDisponivel() {
        return qtdDisponivel;
    }

    public void setQtdDisponivel(Integer qtdDisponivel) {
        this.qtdDisponivel = qtdDisponivel;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getInsumo() {
        return insumo;
    }

    public void setInsumo(String insumo) {
        this.insumo = insumo;
    }

    public Integer getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(Integer tipoBem) {
        this.tipoBem = tipoBem;
    }
}
