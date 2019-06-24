package com.developersd3.bwsmobile.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
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
import com.developersd3.bwsmobile.model.AsyncResponse;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.Colaborador;
import com.developersd3.bwsmobile.model.Fornecedor;
import com.developersd3.bwsmobile.model.PrazoLocacao;
import com.developersd3.bwsmobile.model.UserApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocacaoResumoAct extends AppCompatActivity implements AsyncResponse {

    private TextView materiaisLocadosListText;

    private TextView dados_colaborador;

    private Button executarBtn;

    public static DataManipulator dh;

    private Integer responsavelId;

    private Integer fornecedorId;

    private String forncedorNome;

    private Integer prazoId;

    private String prazoDesc;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

    String dataTransferencia;

    ArrayList<String> listaDeMateriaisSelecionados;

    private String responsavelNome;

    private String recebidoPor;

    private String obra;

    private String telefoneSms;

    Integer alocacaoId;

    EditText contrato;

    EditText valorLocacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locacao_resumo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);

        executarBtn = (Button) findViewById(R.id.executarLocacaoBtn);

        contrato = (EditText) findViewById(R.id.numContrato);

        valorLocacao  = (EditText) findViewById(R.id.valorLocacao);

        dataTransferencia = format.format(new Date());

        this.dh = new DataManipulator(this);

        List<Colaborador> listaColaboradores = dh.findAllColaboradores();

        List<Fornecedor> listaFornecedores   = dh.findAllFornecedores();

        List<PrazoLocacao> listaPrazos       = dh.findAllPrazoLocacoes();

        ArrayAdapter<Colaborador> adpColaborador = new ArrayAdapter<Colaborador>(this, android.R.layout.simple_dropdown_item_1line, listaColaboradores);
        AutoCompleteTextView responsavel = (AutoCompleteTextView) findViewById(R.id.comboResponsaveis);
        responsavel.setAdapter(adpColaborador);

        ArrayAdapter<Fornecedor> adpFornecedores = new ArrayAdapter<Fornecedor>(this, android.R.layout.simple_dropdown_item_1line, listaFornecedores);
        AutoCompleteTextView fornecedores = (AutoCompleteTextView) findViewById(R.id.comboFornecedores);
        fornecedores.setAdapter(adpFornecedores);


        ArrayAdapter<PrazoLocacao> adpPrazos = new ArrayAdapter<PrazoLocacao>(this, android.R.layout.simple_dropdown_item_1line, listaPrazos);
        AutoCompleteTextView prazos = (AutoCompleteTextView) findViewById(R.id.comboPrazos);
        prazos.setAdapter(adpPrazos);

        responsavel.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Colaborador selected = (Colaborador) arg0.getAdapter().getItem(arg2);

                responsavelId = selected.getId();

                responsavelNome = selected.getNome();

                telefoneSms = selected.getTelefone();

            }
        });


        fornecedores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Fornecedor selected = (Fornecedor) arg0.getAdapter().getItem(arg2);

                fornecedorId = selected.getId().intValue();

                forncedorNome = selected.getRazaoSocial();

            }
        });


        prazos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                PrazoLocacao selected = (PrazoLocacao) arg0.getAdapter().getItem(arg2);

                prazoId = selected.getId().intValue();

                prazoDesc = selected.getDescricao();

            }
        });


        Bundle bundle = getIntent().getExtras();

        listaDeMateriaisSelecionados = bundle.getStringArrayList("materiaisSelecionados");

        materiaisLocadosListText = (TextView) findViewById(R.id.materiais_seleciondos_list);

        dados_colaborador = (TextView) findViewById(R.id.dados_colaborador_epi);

        // recupera dados do usuario que esta logado na app

        UserApp user = dh.findUserLogado();

        dados_colaborador.setText("Funcionário - " + user.getLogin());

        // recupera os ids passados por parametro e busca no sqllite
        List<Bem> listaFerramentasSqlLite = new ArrayList<Bem>();

        StringBuilder itens = new StringBuilder("");

        for (String bemString : listaDeMateriaisSelecionados) {

            String[] bemValue = bemString.split("-");

            String nomeBem        =  bemValue[0];
            String codigo         =  bemValue[1];
            String qtdSelecionada =  bemValue[2];


            itens.append(nomeBem+ " - Qtd Selecionada: "+qtdSelecionada);
            itens.append("\n");
        }

        materiaisLocadosListText.setText(itens.toString());

        executarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String messagem = "Deseja realmente realizar esta locação?";

                AlertDialog.Builder builder = new AlertDialog.Builder(LocacaoResumoAct.this);
                builder.setMessage(messagem)
                        .setCancelable(false)
                        .setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // realizar aqui o insert na base externa do postgres
                                        PostGreSQL retrieve = new PostGreSQL();

                                        retrieve.delegate = LocacaoResumoAct.this;

                                        retrieve.execute(contrato.getText().toString(),valorLocacao.getText().toString());

                                        Toast.makeText(getApplicationContext(), "Locação realizada com sucesso!", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(LocacaoResumoAct.this, SubMenuLocacoes.class);
                                        startActivity(intent);

                                    }
                                }).setNegativeButton("não",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * Classe responsável por realizar o insert da alocação no banco postgres
     *
     *
     */
    public class PostGreSQL extends AsyncTask<String, String, Boolean> {


        private Exception exception;
        public AsyncResponse delegate = null;


        @Override
        protected Boolean doInBackground(String... params) {

            try {

                DaoUtil dao = new DaoUtil();


                // verifica se tem internet no aparelho
                if(isOnline()) {

                    alocacaoId = dao.insertLocacao(fornecedorId.longValue(), params[0], params[1], prazoId, responsavelId);

                    // se for verdadeiro o controle alocação foi salvo no postgres e deve ser salvo as ferramentas
                    // caso contrário gravar na base local para futuramente salvar no postgresql
                    if (alocacaoId != null) {

                        for (String bemString : listaDeMateriaisSelecionados) {

                            String[] bemValue = bemString.split("-");

                            Integer codigo = Integer.parseInt(bemValue[1]);
                            Integer qtdSelecionada = Integer.parseInt(bemValue[2]);

                            // salvar os bens na tabela bens alocação
                            dao.insertMateriaisLocacao(codigo, alocacaoId, qtdSelecionada);

                            // atualizar quantidades dos bens
                            dao.atualizaQtdMaterialLocado(codigo, qtdSelecionada);

                            // TODO por enquanto não mexer nesse campo de colaborador
                            // o rastreio das quantidade vai ser pego pelo controle de alocações
                            // dao.atualizaBemColaborador(codigo,recebedorId);

                        }


                    } else {

                        // FAZER O OFFLINE


                    }
                }

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            SmsManager smsManager = SmsManager.getDefault();

            StringBuilder sms = new StringBuilder();

            sms.append("BWS MOBILE ");
            sms.append(System.getProperty("line.separator"));
            sms.append("ID Transacao: "+alocacaoId);
            sms.append(System.getProperty("line.separator"));
            sms.append("Responsavel: "+responsavelNome);
            sms.append(System.getProperty("line.separator"));
            sms.append("Data: "+format2.format(new Date()));
            sms.append(System.getProperty("line.separator"));
            sms.append("Materiais Locados: ");
            sms.append(System.getProperty("line.separator"));

            for (String bemString : listaDeMateriaisSelecionados) {

                if(sms.length() < 160 ) {

                    String[] bemValue = bemString.split("-");

                    String nomeBem = bemValue[0];
                    String qtdSelecionada = bemValue[2];


                    sms.append(nomeBem + " - Qtde: " + qtdSelecionada);
                    sms.append(System.getProperty("line.separator"));
                }else{
                    break;
                }
            }

            smsManager.sendTextMessage(telefoneSms, null, sms.toString(), null, null);
        }

        @Override
        protected void onCancelled() {




        }
    }

    @Override
    public void processFinish(List<String> output) {

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(LocacaoResumoAct.this,MenuAct.class);
        startActivity(intent);
    }


}
