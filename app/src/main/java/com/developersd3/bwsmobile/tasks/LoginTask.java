package com.developersd3.bwsmobile.tasks;

/**
 * Created by fred on 06/11/16.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.developersd3.bwsmobile.activity.LoginAct;
import com.developersd3.bwsmobile.activity.MenuAct;
import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.delegate.LoginDelegate;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.BemAlocacao;
import com.developersd3.bwsmobile.model.CentroCusto;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.ControleAlocacao;
import com.developersd3.bwsmobile.model.Fornecedor;
import com.developersd3.bwsmobile.model.MaterialLocado;
import com.developersd3.bwsmobile.model.PrazoLocacao;
import com.developersd3.bwsmobile.model.UserApp;

import java.util.List;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<String, String, UserApp> {

    private LoginDelegate loginDelegate;

    private Exception exception;

    private Context context;

    public LoginTask(LoginDelegate loginDelegate, Context context) {
        this.loginDelegate = loginDelegate;
        this.context = context;
    }

    @Override
    protected UserApp doInBackground(String... params) {

        UserApp userApp = null;


            try {

                // acessando base externa do postgres
                DaoUtil dao = new DaoUtil();

                userApp = dao.validarLogin(params[0], params[1]);


                // carrega as listas do postgres para a base de dados android
                if (userApp.getColaborador() != null) {

                    DataManipulator dm = new DataManipulator(this.context);

                    dm.limpaBaseDeDados();

                    dm.carregaListas(userApp);

                    // Sincronizar primeiro as alocações e os bens alocaçoes
                    List<ControleAlocacao> listControleAlocacoes = dm.selectAllControleAlocacoesByNotSincronized();

                    if (listControleAlocacoes != null && !listControleAlocacoes.isEmpty()) {

                        for (ControleAlocacao con : listControleAlocacoes) {

                            Integer idAlocacao = dao.insertControleAlocacao(con.getDataTransferencia(), con.getEntregadorId(), con.getRecebedorId(),
                                    con.getCentroCustoId(), con.getTipoAlocacao(),con.getEndereco());

                            // recupera os bens alocação no sql lite
                            List<BemAlocacao> listBens = dm.selectAllBensAlocacaoByNotSincronized(con.getId());

                            if (listBens != null && !listBens.isEmpty()) {

                                for (BemAlocacao bem : listBens) {

                                    dao.insertBensAlocacao(idAlocacao, bem.getIdBem(), bem.getQtdAlocada());

                                    // atualizar quantidades dos bens
                                    if (con.getTipoAlocacao() == 1) {

                                        // diminui quantidade alocada no entregador
                                        dao.atualizaQtdBensDiminui(bem.getIdBem(), bem.getQtdAlocada(), con.getEntregadorId());

                                        /** soma quantidade alocado no recebedor
                                         *
                                         * primeiro deve verificar se já existe cadastro desse bem pra
                                         * esse colaborador na tabela se ja tiver executa um update
                                         * caso contrário executa um insert
                                         *
                                         */
                                        Boolean temNaCarga = dao.temBemNaCarga(con.getRecebedorId(), bem.getIdBem());

                                        if (temNaCarga) {
                                            dao.atualizaQtdBensSoma(bem.getIdBem(), bem.getQtdAlocada(), con.getRecebedorId());
                                        } else {
                                            dao.insertQtdBemColaborador(bem.getIdBem(), bem.getQtdAlocada(), con.getRecebedorId());
                                        }


                                    }

                                    dm.updateBensControleAlocacaoSincronizada(bem.getIdBem());

                                }


                            }

                            dm.updateControleAlocacaoSincronizada(con.getId());

                        }

                    }


                }
                else {
                    return null;
                }






            } catch (Exception e) {
                return userApp;
            }



        return userApp;
    }

    @Override
    protected void onPreExecute() {
        this.loginDelegate.carregaDialog();
    }

    @Override
    protected void onPostExecute(UserApp userApp) {

        this.loginDelegate.loginRealizado(userApp);

    }

 }