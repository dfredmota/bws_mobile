package com.developersd3.bwsmobile.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developersd3.bwsmobile.BuildConfig;
import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.delegate.LoginDelegate;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.CentroCusto;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.Data;
import com.developersd3.bwsmobile.model.Fornecedor;
import com.developersd3.bwsmobile.model.MaterialLocado;
import com.developersd3.bwsmobile.model.PrazoLocacao;
import com.developersd3.bwsmobile.model.UserApp;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.developersd3.bwsmobile.tasks.LoginTask;
import com.jcraft.jsch.*;

/**
 * Activity responsáveis por executar as funções de login
 */
public class LoginAct extends AppCompatActivity implements LoginDelegate {

    public static DataManipulator dh;

    Bundle bundle;

    Context context;

    ProgressDialog ringProgressDialog;

    Button btnLogin;

    EditText editTextUsuario;
    EditText editEditTextSenha;

    private int MY_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getBaseContext();

        registrarInteracao();

        editTextUsuario = (EditText) findViewById(R.id.login);

        editEditTextSenha = (EditText) findViewById(R.id.password);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},
                    MY_REQUEST_CODE);
        }


    }

    @Override
    public void carregaDialog() {

        ringProgressDialog= ProgressDialog.show(this,"Carregando","");
        ringProgressDialog.show();
    }

    @Override
    public void loginRealizado(UserApp user) {

        ringProgressDialog.dismiss();

        if (user != null) {
            Toast.makeText(getApplicationContext(), "Login Realizado Com Sucesso!", Toast.LENGTH_LONG).show();

            Data.insertUsuario(PreferenceManager.getDefaultSharedPreferences(this),user);

            navToHome();

        } else {
            Toast.makeText(getApplicationContext(), "Login Inválido!", Toast.LENGTH_LONG).show();
            return;
        }


    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    private void navToHome(){

        Intent i = new Intent(this, HomeAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);

    }

    private void registrarInteracao(){

        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOnline())
                login();
                else
                Toast.makeText(getApplicationContext(), "Sem Rede.Tente mais tarde",
                        Toast.LENGTH_LONG).show();


            }
        });
    }

    private void login() {

        String usuario = ((EditText) findViewById(R.id.login)).getText().toString();
        String senha = ((EditText) findViewById(R.id.password)).getText().toString();

        if (usuario == null || usuario.isEmpty()) {
            ((EditText) findViewById(R.id.login)).setError("Campo obrigatório");
            return;
        }

        if (senha == null || senha.isEmpty()) {
            ((EditText) findViewById(R.id.password)).setError("Campo obrigatório");
            return;
        }


        LoginTask task = new LoginTask(this,this.context);

        task.execute(new String[]{usuario,senha});

    }


}