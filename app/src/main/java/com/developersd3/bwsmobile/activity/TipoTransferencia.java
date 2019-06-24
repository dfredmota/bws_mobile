package com.developersd3.bwsmobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.model.AsyncResponse;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.SpinnerAdapter;
import com.developersd3.bwsmobile.tasks.LoginTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TipoTransferencia extends AppCompatActivity implements AsyncResponse {

    Spinner itemList;
    Spinner itemTipoOpList;

    DataManipulator dh;

    Integer entregadorId;

    String entreguePor;

    String telefoneEntregador;

    ProgressDialog ringProgressDialog;

    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_transferencia);

        act = this;

        this.dh = new DataManipulator(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        List<String> list = new ArrayList<String>();
        list.add("FERRAMENTAS");
        list.add("EPI");
        list.add("PATRIMÔNIO");
        list.add("ESTOQUE");

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.adapter_spinner, list, getResources());

        itemList = (Spinner) findViewById(R.id.spinnerMedida);
        itemList.setAdapter(adapter);

        Button btnAvancar = (Button) findViewById(R.id.btn_avancar);

        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(TipoTransferencia.this, FerramentaEntregaAct.class);

                intent.putExtra("tipoTransferencia",itemList.getSelectedItem().toString());
                intent.putExtra("tipoOperacao",itemTipoOpList.getSelectedItem().toString());
                intent.putExtra("entregadorId",entregadorId);
                intent.putExtra("entreguePor",entreguePor);
                intent.putExtra("telefoneEntregador",telefoneEntregador);

                startActivity(intent);


            }
        });

        List<String> listTipo = new ArrayList<String>();
        listTipo.add("ENTREGA");
        listTipo.add("DEVOLUÇÃO");

        SpinnerAdapter adapterTipo = new SpinnerAdapter(this, R.layout.adapter_spinner, listTipo, getResources());

        itemTipoOpList = (Spinner) findViewById(R.id.spinnerTipoOperacao);
        itemTipoOpList.setAdapter(adapterTipo);

        List<Colaborador> listaColaboradores = dh.findAllColaboradores();

        ArrayAdapter<Colaborador> adpEntregador = new ArrayAdapter<Colaborador>(this, android.R.layout.simple_dropdown_item_1line, listaColaboradores);
        AutoCompleteTextView entregador = (AutoCompleteTextView) findViewById(R.id.comboEntregador);
        entregador.setAdapter(adpEntregador);


        entregador.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Colaborador selected = (Colaborador) arg0.getAdapter().getItem(arg2);

                entregadorId = selected.getId();
                entreguePor = selected.getNome();
                telefoneEntregador = selected.getTelefone();

                ringProgressDialog = ProgressDialog.show(act,"Carregando Lista de Bens","");
                ringProgressDialog.show();

                PostGreSQL task = new PostGreSQL();

                task.execute();
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        navToHome();
    }

    private void navToHome(){

        Intent i = new Intent(this, HomeAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);

    }

    @Override
    public void processFinish(List<String> output) {

    }


    public class PostGreSQL extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;
        public AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                DaoUtil dao = new DaoUtil();

                List<Bem> listaFromPostgres = dao.carregaBens(entregadorId);

                dh.deleteAllBens();

                if (listaFromPostgres != null && !listaFromPostgres.isEmpty()) {

                    for (Bem bem : listaFromPostgres) {

                        // realizar o insert dos bens no sqllite
                        dh.insertBem(bem.getCodigo(), bem.getInsumo(), bem.getTipoBem().toString(), bem.getQuantidade());

                    }
                }

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            ringProgressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {

        }
    }

}
