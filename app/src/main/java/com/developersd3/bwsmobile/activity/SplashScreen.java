package com.developersd3.bwsmobile.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.delegate.SincronizaDBDelegate;
import com.developersd3.bwsmobile.model.Data;
import com.developersd3.bwsmobile.model.UserApp;
import com.developersd3.bwsmobile.tasks.LoginTask;
import com.developersd3.bwsmobile.tasks.SincronizaDBTask;

public class SplashScreen extends AppCompatActivity implements SincronizaDBDelegate {

    private boolean permissoesConcedidas = false;

    private final static String FTP_HOST = "162.243.196.32";
    private final static String FTP_USER = "rails";
    private final static String FTP_PASS = "bws@1234";
    private final static Integer FTP_PORT = 22;

    private static final int REQUEST_CODE = 0x11;

    ProgressDialog ringProgressDialog;

    UserApp usuarioSessao;

    Context context;

    /**
     * Id to identify a storage permission request.
     */
    private static final int PERMISSAO_ARMAZENAMENTO = 0;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        context = getBaseContext();

        usuarioSessao = Data.getUsuario(preferences);

        if(usuarioSessao != null) {

            SincronizaDBTask task = new SincronizaDBTask(this,this.context);

            task.execute(new UserApp[]{usuarioSessao});

        }else{

            navToLogin();
        }
    }

    @Override
    public void carregaDialog() {

        ringProgressDialog= ProgressDialog.show(this,"Sincronizando Dados...","");
        ringProgressDialog.show();
    }

    private void navToLogin(){

        Intent i = new Intent(this, LoginAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);

    }


    private void navToHome(){

        Intent i = new Intent(this, HomeAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);

    }

    @Override
    public void sincronismoRealizado(Boolean result) {

        ringProgressDialog.dismiss();

        navToHome();

    }

    private void verificarPermissaoArmazenamento() {

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {

            //  Verifica se já foi dadas as permissões de leitura e escrita
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Solicita ao usuário as permissões de leitura e escrita
                ActivityCompat.requestPermissions(SplashScreen.this, PERMISSIONS_STORAGE,  PERMISSAO_ARMAZENAMENTO);

            } else {
                permissoesConcedidas = true;
            }
        } else {
            permissoesConcedidas = true;
        }
    }
}
