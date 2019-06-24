package com.developersd3.bwsmobile.tasks;

/**
 * Created by fred on 06/11/16.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.delegate.LoginDelegate;
import com.developersd3.bwsmobile.delegate.SincronizaDBDelegate;
import com.developersd3.bwsmobile.model.BemAlocacao;
import com.developersd3.bwsmobile.model.ControleAlocacao;
import com.developersd3.bwsmobile.model.UserApp;

import java.util.List;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class SincronizaDBTask extends AsyncTask<UserApp, String, Boolean> {

    private SincronizaDBDelegate syncDelegate;

    private Exception exception;

    private Context context;

    public SincronizaDBTask(SincronizaDBDelegate syncDelegate, Context context) {
        this.syncDelegate = syncDelegate;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(UserApp... params) {

        DataManipulator dm = new DataManipulator(this.context);

        DaoUtil dao = new DaoUtil();

        Boolean retorno = false;

        try {

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

                            dm.updateBensControleAlocacaoSincronizada(bem.getIdBem());

                        }


                    }

                    dm.updateControleAlocacaoSincronizada(con.getId());

                }

            }else{

                retorno = false;
            }

            // limpa tabelas
            dm.limpaBaseDeDados();

            // carrega db
            dm.carregaListas(params[0]);

            dao.deletaZerados(params[0].getColaborador());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retorno;

    }


    @Override
    protected void onPreExecute() {
        this.syncDelegate.carregaDialog();
    }

    @Override
    protected void onPostExecute(Boolean result) {

        this.syncDelegate.sincronismoRealizado(result);
    }


}