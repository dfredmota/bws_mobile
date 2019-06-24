package com.developersd3.bwsmobile.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.delegate.AdressFromGPSDelegate;
import com.developersd3.bwsmobile.model.AsyncResponse;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.CentroCusto;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.UserApp;
import com.developersd3.bwsmobile.tasks.AddressFromGPSTask;
import com.developersd3.bwsmobile.tasks.LoginTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FerramentaEntregaResumoAct extends AppCompatActivity implements AsyncResponse,AdressFromGPSDelegate {

    private TextView ferramentasListText;

    private TextView dados_colaborador;

    private Button executarBtn;

    public static DataManipulator dh;

    private Integer entregadorId;

    private Integer recebedorId;

    private Integer obraId;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

    String dataTransferencia;

    ArrayList<String> listaDeFerramentasSelecionadas;

    private String entreguePor;

    private String recebidoPor;

    private String obra;

    private String telefoneSms;

    Integer alocacaoId;

    ProgressDialog ringProgressDialog;

    /**
     * 0 - offiline
     * 1 - dados offline
     * 2 - sincronizou
     *
     */
    private Integer sincronizou = 0;

    private Location location;

    String enderecoAlocacao = "Matriz";

    String tipoAlocacao = "";

    Integer tipoControleAloc =0;


    public boolean islivesearchon;
    public static double netlistentime = 0 * 60 * 1000; // minutes * 60 sec/min * 1000 for milliseconds
    public static double netlistendistance = 0 * 1609.344; // miles * conversion to meters
    public static double gpslistentime = 30 * 60 * 1000; // minutes * 60 sec/min * 1000 for milliseconds
    public static double gpslistendistance = 0 * 1609.344; // miles * conversion to meters

    private static Location currentlocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    int MY_REQUEST_CODE = 999;

    UserApp usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ringProgressDialog= ProgressDialog.show(this,"Carregando","");
        ringProgressDialog.show();

        setContentView(R.layout.activity_ferramenta_entrega_resumo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        setTitle("RESUMO");

        executarBtn = (Button) findViewById(R.id.findSelected);

        dataTransferencia = format.format(new Date());

        this.dh = new DataManipulator(this);

        List<Colaborador> listaColaboradores = dh.findAllColaboradores();

        List<CentroCusto> listaCentroCusto = dh.findAllCentroDeCustos();

        //  Lista de todos os colaboradores com exceção do colaborador que está logado na aplicação
        List<Colaborador> listaRecebedores = new ArrayList<Colaborador>();

        //  Obtém o usuário que está logado na aplicação
        usuarioLogado = dh.findUserLogado();

        //  Monta a lista com todos os colaboradores com exeção do colaborador que está logado na aplicação
        listaRecebedores.addAll(listaColaboradores);

        ArrayAdapter<Colaborador> adpRecebedor = new ArrayAdapter<Colaborador>(this, android.R.layout.simple_dropdown_item_1line, listaRecebedores);
        AutoCompleteTextView recebedor = (AutoCompleteTextView) findViewById(R.id.comboRecebedor);
        recebedor.setAdapter(adpRecebedor);

        ArrayAdapter<CentroCusto> adpObra = new ArrayAdapter<CentroCusto>(this, android.R.layout.simple_dropdown_item_1line, listaCentroCusto);
        AutoCompleteTextView obras = (AutoCompleteTextView) findViewById(R.id.comboCentroCusto);
        obras.setAdapter(adpObra);

        recebedor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Colaborador selected = (Colaborador) arg0.getAdapter().getItem(arg2);

                recebedorId = selected.getId();
                recebidoPor = selected.getNome();
                telefoneSms = selected.getTelefone();
            }
        });

        obras.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CentroCusto selected = (CentroCusto) arg0.getAdapter().getItem(arg2);

                obraId = selected.getId();
                obra = selected.getNome();

            }
        });

        Bundle bundle = getIntent().getExtras();

        tipoAlocacao = (String) bundle.get("tipoOperacao");

        if(tipoAlocacao.equalsIgnoreCase("ENTREGA"))
            tipoControleAloc = 1;
        else
            tipoControleAloc = 2;

        listaDeFerramentasSelecionadas = bundle.getStringArrayList("itensSelecionados");

        ferramentasListText = (TextView) findViewById(R.id.ferramentasList);

        dados_colaborador = (TextView) findViewById(R.id.dados_colaborador);

        // recupera dados do usuario que esta logado na app

        UserApp user = dh.findUserLogado();

        dados_colaborador.setText("Usuário: - " + user.getLogin());

        entregadorId = (Integer)bundle.get("entregadorId");

        entreguePor  = (String)bundle.get("entreguePor");

        telefoneSms  = (String)bundle.get("telefoneEntregador");

        // recupera os ids passados por parametro e busca no sqllite
        List<Bem> listaFerramentasSqlLite = new ArrayList<Bem>();

        StringBuilder itens = new StringBuilder("");

        for (String bemString : listaDeFerramentasSelecionadas) {

            String[] bemValue = bemString.split("-");

            String nomeBem        =  bemValue[0];
            String codigo         =  bemValue[1];
            String qtdSelecionada =  bemValue[2];


            itens.append(nomeBem+ " - Qtd Selecionada: "+qtdSelecionada);
            itens.append("\n");
        }

        ferramentasListText.setText(itens.toString());

        //Implementação da ação de confirmar a transferencia validando os 3 primeiro digitos do CPF

        executarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Context context = getBaseContext();
                final AlertDialog.Builder alert = new AlertDialog.Builder(FerramentaEntregaResumoAct.this);
                final EditText input = new EditText(FerramentaEntregaResumoAct.this);
                input.setWidth(5);
                alert.setMessage("Digite a senha:");
                alert.setView(input);
                alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String value = input.getText().toString().trim();

                        Colaborador recebedor = dh.findColaborador(recebedorId);

                        String cpf3Digitos = recebedor.getCpf().substring(0,3);

                        if (cpf3Digitos.equals(value)) {
                            // realizar aqui o insert na base externa do postgres
                            PostGreSQL retrieve = new PostGreSQL();

                            retrieve.delegate = FerramentaEntregaResumoAct.this;

                            retrieve.execute();

                            ringProgressDialog = ProgressDialog.show(FerramentaEntregaResumoAct.this, "Aguarde", "Salvando Dados..", true);
                            ringProgressDialog.setCancelable(true);
                        } else {
                            Toast.makeText(getApplicationContext(), "Senha incorreta. Tente novamente.", Toast.LENGTH_LONG).show();

                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();

            }
        });


        location = getLocation();

        if(location == null){

            ringProgressDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(FerramentaEntregaResumoAct.this);

            builder.setMessage("HABILITE O GPS POR FAVOR")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    navToHome();

                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();


        }else {

            AddressFromGPSTask task = new AddressFromGPSTask(this);

            task.execute(new Double[]{location.getLatitude(), location.getLongitude()});
        }

    }

    @Override
    public void carregaDialog() {


    }

    @Override
    public void retrieveAdress(String result) {

        ringProgressDialog.dismiss();

        enderecoAlocacao = result;

    }

    public boolean isOnline() {
        boolean on;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
       Log.d("Logado??", String.valueOf(netInfo.isConnectedOrConnecting())) ;
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            on = true;
        }else {
            on = false;
        }

        return on;
    }

    /**
     * Classe responsável por realizar o insert da alocação no banco postgres
     *
     *
     */
    public class PostGreSQL extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;
        public AsyncResponse delegate = null;

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                DaoUtil dao = new DaoUtil();


                // verifica se tem internet no aparelho
                if(isOnline()){

                alocacaoId = dao.insertControleAlocacao(dataTransferencia,entregadorId,recebedorId,obraId,tipoControleAloc,enderecoAlocacao);

                // se for verdadeiro o controle alocação foi salvo no postgres e deve ser salvo as ferramentas
                // caso contrário gravar na base local para futuramente salvar no postgresql
                if(alocacaoId != null){

                    sincronizou = 2;

                    for (String bemString : listaDeFerramentasSelecionadas) {

                        String[] bemValue = bemString.split("-");

                        Integer codigo = Integer.parseInt(bemValue[1]);
                        Integer qtdSelecionada = Integer.parseInt(bemValue[2]);

                        // salvar os bens na tabela bens alocação
                        dao.insertBensAlocacao(alocacaoId, codigo, qtdSelecionada);

                        // diminui quantidade alocada no entregador
                        dao.atualizaQtdBensDiminui(codigo, qtdSelecionada, entregadorId);


                        /** soma quantidade alocado no recebedor
                         *
                         * primeiro deve verificar se já existe cadastro desse bem pra
                         * esse colaborador na tabela se ja tiver executa um update
                         * caso contrário executa um insert
                         *
                         */
                        Boolean temNaCarga = dao.temBemNaCarga(recebedorId,codigo);

                        if(temNaCarga) {
                            dao.atualizaQtdBensSoma(codigo, qtdSelecionada, recebedorId);
                        }
                        else {
                            dao.insertQtdBemColaborador(codigo,qtdSelecionada,recebedorId);
                        }
                    }

                    // sincroniza tabela de bens com as quantidades atualizadas
                    List<Bem> listaFromPostgres = dao.carregaBens(entregadorId);

                    dh.deleteAllBens();

                    if (listaFromPostgres != null && !listaFromPostgres.isEmpty()) {

                        for (Bem bem : listaFromPostgres) {

                            // realizar o insert dos bens no sqllite
                            dh.insertBem(bem.getCodigo(),bem.getInsumo(),bem.getTipoBem().toString(), bem.getQuantidade());

                        }

                    }

                }else{

                    sincronizou = 1;

                    // caso não tenha salvo no postgresql salvar na sqllite
                    Long controleAlocacaoSqlLiteId = dh.insertControleAlocacao(dataTransferencia,entregadorId,recebedorId,obraId,1,0);

                    for (String bemString : listaDeFerramentasSelecionadas) {

                        String[] bemValue = bemString.split("-");

                        String nomeBem        =  bemValue[0];
                        String codigo         =  bemValue[1];
                        String qtdSelecionada =  bemValue[2];

                        // salvar os bens na tabela bens alocação
                        dh.insertBemAlocacao(controleAlocacaoSqlLiteId.intValue(), Integer.parseInt(codigo.trim()), Integer.parseInt(qtdSelecionada));
                    }

                }


                    // limpa tabelas
                    dh.limpaBaseDeDados();

                    // carrega db
                    dh.carregaListas(usuarioLogado);

                    dao.deletaZerados(usuarioLogado.getColaborador());

                }else{

                    sincronizou = 1;
                    // caso não tenha internet disponivel já grava no sqllite

                    // caso não tenha salvo no postgresql salvar na sqllite
                    Long controleAlocacaoSqlLiteId = dh.insertControleAlocacao(dataTransferencia,entregadorId,recebedorId,obraId,tipoControleAloc,0);

                    for (String bemString : listaDeFerramentasSelecionadas) {

                        String[] bemValue = bemString.split("-");

                        String nomeBem        =  bemValue[0];
                        String codigo         =  bemValue[1];
                        String qtdSelecionada =  bemValue[2];

                        // salvar os bens na tabela bens alocação
                        dh.insertBemAlocacao(controleAlocacaoSqlLiteId.intValue(), Integer.parseInt(codigo.trim()), Integer.parseInt(qtdSelecionada));
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

            if(sincronizou == 2) {

                SmsManager smsManager = SmsManager.getDefault();

                StringBuilder sms = new StringBuilder();

                sms.append("BWS MOBILE ");
                sms.append(System.getProperty("line.separator"));
                sms.append("ID Transacao: " + alocacaoId);
                sms.append(System.getProperty("line.separator"));
                sms.append("Entregador: " + entreguePor);
                sms.append(System.getProperty("line.separator"));
                sms.append("Recebedor: " + recebidoPor);
                sms.append(System.getProperty("line.separator"));
                sms.append("Data: " + format2.format(new Date()));
                sms.append(System.getProperty("line.separator"));
                sms.append("Ferramentas: ");
                sms.append(System.getProperty("line.separator"));

                for (String bemString : listaDeFerramentasSelecionadas) {

                    if (sms.length() < 160) {

                        String[] bemValue = bemString.split("-");

                        String nomeBem = bemValue[0];
                        String qtdSelecionada = bemValue[2];


                        sms.append(nomeBem + " - Qtde: " + qtdSelecionada);
                        sms.append(System.getProperty("line.separator"));
                    } else {
                        break;
                    }
                }

                smsManager.sendTextMessage(telefoneSms, null, sms.toString(), null, null);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(FerramentaEntregaResumoAct.this);

            String msg = "";

            if(sincronizou == 1)
                msg = "DADOS SALVOS OFFILINE.SINCRONIZE QUANDO HOUVER INTERNET.";

            if(sincronizou == 2)
                msg = "DADOS SALVOS ONLINE COM SUCESSO!";

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

        @Override
        protected void onCancelled() {

        }
    }

    @Override
    public void processFinish(List<String> output) {

    }

    private void navToHome(){

        Intent i = new Intent(this, HomeAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);

    }

    @Override
    public void onBackPressed()
    {
        navToHome();
    }


    public Location getLocation() {

        Location location = null;

        locationListener = new LocListener();

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

        try {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            Boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            Boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) netlistentime, (float) netlistendistance, locationListener);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) gpslistentime, (float) gpslistendistance, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private class LocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location latestlocation) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

}
