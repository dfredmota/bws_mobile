package com.developersd3.bwsmobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.delegate.SincronizaDBDelegate;
import com.developersd3.bwsmobile.model.AsyncResponse;
import com.developersd3.bwsmobile.model.BemAlocacao;
import com.developersd3.bwsmobile.model.ControleAlocacao;
import com.developersd3.bwsmobile.model.Data;
import com.developersd3.bwsmobile.model.UserApp;
import com.developersd3.bwsmobile.tasks.SincronizaDBTask;

import java.util.List;

public class SincronizarAct extends AppCompatActivity implements SincronizaDBDelegate {


    public static DataManipulator dh;

    Button sincronizarBtn;

    ProgressDialog ringProgressDialog;

    /**
     * 0 - offiline
     * 1 - não dados
     * 2 - sincronizou
     *
     */
    private Integer sincronizou = 0;

    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizar);

        act = this;

        setTitle("SINCRONIZAR DADOS");

        this.dh = new DataManipulator(this);

        if(isOnline()) {

                    String messagem = "Deseja realmente sincronizar os dados?";


                    AlertDialog.Builder builder = new AlertDialog.Builder(SincronizarAct.this);
                    builder.setMessage(messagem)
                            .setCancelable(false)
                            .setPositiveButton("Sim",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // realizar aqui o insert na base externa do postgres

                                            SincronizaDBTask task = new SincronizaDBTask(SincronizarAct.this, act.getBaseContext());

                                            UserApp user = Data.getUsuario(PreferenceManager.getDefaultSharedPreferences(act));

                                            task.execute(new UserApp[]{user});


                                        }
                                    }).setNegativeButton("não",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();

                                    navToHome();

                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();


        }

        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(SincronizarAct.this);

            String msg = "INTERNET NÃO DISPONIVEL.TENTE MAIS TARDE";

            builder.setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent intent = new Intent(SincronizarAct.this, MenuAct.class);
                                    startActivity(intent);

                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

        }

    }

    private void navToHome(){

        Intent i = new Intent(this, HomeAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SincronizarAct.this, MenuAct.class);
        startActivity(intent);
    }

    @Override
    public void carregaDialog() {

        ringProgressDialog= ProgressDialog.show(this,"Sincronizando Dados...","");
        ringProgressDialog.show();
    }

    @Override
    public void sincronismoRealizado(Boolean result) {

        ringProgressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(SincronizarAct.this);

        String msg = "";

        if(result)
            msg = "DADOS SINCRONIZADOS COM SUCESSO!";
        else
            msg = "NÃO HÁ DADOS PARA SINCRONIZAR";

        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                navToHome();

                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();

    }
}
