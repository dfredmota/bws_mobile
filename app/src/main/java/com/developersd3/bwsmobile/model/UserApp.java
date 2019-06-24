package com.developersd3.bwsmobile.model;

/**
 * Created by fred on 05/03/16.
 */
public class UserApp {

    private Integer id;

    private String login;

    private String senha;

    private Boolean superUsuario;

    private Integer colaborador;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getSuperUsuario() {
        return superUsuario;
    }

    public void setSuperUsuario(Boolean superUsuario) {
        this.superUsuario = superUsuario;
    }

    public Integer getColaborador() {
        return colaborador;
    }

    public void setColaborador(Integer colaborador) {
        this.colaborador = colaborador;
    }
}
