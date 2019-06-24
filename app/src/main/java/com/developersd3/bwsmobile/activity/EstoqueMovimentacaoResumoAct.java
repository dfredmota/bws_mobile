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
import android.widget.TextView;
import android.widget.Toast;

import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.dao.DaoUtil;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.model.AsyncResponse;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.CentroCusto;
import com.developersd3.bwsmobile.model.Colaborador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EstoqueMovimentacaoResumoAct extends AppCompatActivity implements AsyncResponse {

    private TextView estoqueListText;

    private TextView dados_colaborador;

    private Button executarBtn;

    public static DataManipulator dh;

    private Integer responsavelOrigemId;

    private Integer responsavelDestinoId;

    private Integer obraOrigemId;
    private Integer obraDestinoId;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

    String dataTransferencia;

    ArrayList<String> listaDeEstoqueSelecionados;

    private String entreguePara;

    private String recebidoPor;

    private String obraOrigemDesc;
    private String obraDestinoDesc;

    private String telefoneSms;

    Integer movimentacaoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estoque_movimentacao_resumo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);

        executarBtn = (Button) findViewById(R.id.findSelectedEstoque);

        dataTransferencia = format.format(new Date());

        this.dh = new DataManipulator(this);

        List<Colaborador> listaColaboradores = dh.findAllColaboradores();

        List<CentroCusto> listaCentroCusto = dh.findAllCentroDeCustos();

        ArrayAdapter<Colaborador> adpColaboradorResponsavelOrigem = new ArrayAdapter<Colaborador>(this, android.R.layout.simple_dropdown_item_1line, listaColaboradores);
        AutoCompleteTextView colabOrigemResp = (AutoCompleteTextView) findViewById(R.id.comboResponsavelOrigem_estoque);
        colabOrigemResp.setAdapter(adpColaboradorResponsavelOrigem);

        ArrayAdapter<Colaborador> adpColabResponsavelDestino = new ArrayAdapter<Colaborador>(this, android.R.layout.simple_dropdown_item_1line, listaColaboradores);
        AutoCompleteTextView colabDestinoResp = (AutoCompleteTextView) findViewById(R.id.comboResponsavelDestino_estoque);
        colabDestinoResp.setAdapter(adpColabResponsavelDestino);

        ArrayAdapter<CentroCusto> adpObraOrigem = new ArrayAdapter<CentroCusto>(this, android.R.layout.simple_dropdown_item_1line, listaCentroCusto);
        final AutoCompleteTextView obraOrigem = (AutoCompleteTextView) findViewById(R.id.comboCentroCusto_estoque_origem);
        obraOrigem.setAdapter(adpObraOrigem);

        ArrayAdapter<CentroCusto> adpObraDestino = new ArrayAdapter<CentroCusto>(this, android.R.layout.simple_dropdown_item_1line, listaCentroCusto);
        AutoCompleteTextView obraDestino = (AutoCompleteTextView) findViewById(R.id.comboCentroCusto_estoque_destino);
        obraDestino.setAdapter(adpObraDestino);

        colabOrigemResp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Colaborador selected = (Colaborador) arg0.getAdapter().getItem(arg2);

                responsavelOrigemId = selected.getId();

                entreguePara = selected.getNome();

            }
        });

        colabDestinoResp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Colaborador selected = (Colaborador) arg0.getAdapter().getItem(arg2);


                responsavelDestinoId = selected.getId();
                recebidoPor = selected.getNome();
                telefoneSms = selected.getTelefone();
            }
        });

        obraOrigem.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CentroCusto selected = (CentroCusto) arg0.getAdapter().getItem(arg2);

                obraOrigemId = selected.getId();
                obraOrigemDesc = selected.getNome();

            }
        });

        obraDestino.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CentroCusto selected = (CentroCusto) arg0.getAdapter().getItem(arg2);

                obraDestinoId = selected.getId();
                obraDestinoDesc = selected.getNome();

            }
        });

        Bundle bundle = getIntent().getExtras();

        listaDeEstoqueSelecionados = bundle.getStringArrayList("estoqueSelecionados");

        estoqueListText = (TextView) findViewById(R.id.estoqueList);

        // recupera os ids passados por parametro e busca no sqllite
        List<Bem> listaFerramentasSqlLite = new ArrayList<Bem>();

        StringBuilder itens = new StringBuilder("");

        for (String bemString : listaDeEstoqueSelecionados) {

            String[] bemValue = bemString.split("-");

            String nomeBem        =  bemValue[0];
            String codigo         =  bemValue[1];
            String qtdSelecionada =  bemValue[2];


            itens.append(nomeBem+ " - Qtd Selecionada: "+qtdSelecionada);
            itens.append("\n");
        }

        estoqueListText.setText(itens.toString());

        executarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String messagem = "Deseja realmente transferir essa carga de itens de estoque?";


                AlertDialog.Builder builder = new AlertDialog.Builder(EstoqueMovimentacaoResumoAct.this);
                builder.setMessage(messagem)
                        .setCancelable(false)
                        .setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // realizar aqui o insert na base externa do postgres
                                        PostGreSQL retrieve = new PostGreSQL();

                                        retrieve.delegate = EstoqueMovimentacaoResumoAct.this;

                                        retrieve.execute();

                                        Toast.makeText(getApplicationContext(), "Transferência de Carga realizada com sucesso!", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(EstoqueMovimentacaoResumoAct.this, SubMenuAlmoxarifadoAct.class);
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
    public class PostGreSQL extends AsyncTask<Void, Void, Boolean> {


        private Exception exception;
        public AsyncResponse delegate = null;


        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                DaoUtil dao = new DaoUtil();


                // verifica se tem internet no aparelho
                if(isOnline()){

                    movimentacaoId = dao.insertMovimentacao(dataTransferencia,obraOrigemId,obraDestinoId,responsavelOrigemId,responsavelDestinoId);

                    // se for verdadeiro o controle alocação foi salvo no postgres e deve ser salvo as ferramentas
                    // caso contrário gravar na base local para futuramente salvar no postgresql
                    if(movimentacaoId != null){

                        for (String bemString : listaDeEstoqueSelecionados) {

                            String[] bemValue = bemString.split("-");

                            Integer codigo = Integer.parseInt(bemValue[1]);
                            Integer qtdSelecionada = Integer.parseInt(bemValue[2]);

                            // salvar os bens na tabela bens alocação
                            dao.insertBensMovimentacao(movimentacaoId, codigo, qtdSelecionada);

                            // atualizar quantidades dos bens
                            //dao.atualizaQtdBens(codigo,qtdSelecionada);

                            // TODO por enquanto não mexer nesse campo de colaborador
                            // o rastreio das quantidade vai ser pego pelo controle de alocações
                            // dao.atualizaBemColaborador(codigo,recebedorId);

                        }


                    }else{

                        // caso não tenha salvo no postgresql salvar na sqllite
                        Long movimentacaoSqlLiteId = dh.insertMovimentacao(dataTransferencia,obraOrigemId,obraDestinoId,responsavelOrigemId,responsavelDestinoId);

                        for (String bemString : listaDeEstoqueSelecionados) {

                            String[] bemValue = bemString.split("-");

                            String nomeBem        =  bemValue[0];
                            String codigo         =  bemValue[1];
                            String qtdSelecionada =  bemValue[2];

                            // salvar os bens na tabela bens alocação
                            dh.insertBemMovimentacao(Integer.parseInt(codigo),movimentacaoSqlLiteId.intValue(),Integer.parseInt(qtdSelecionada));
                        }

                    }


                }else{

                    // caso não tenha internet disponivel já grava no sqllite

                    // caso não tenha salvo no postgresql salvar na sqllite
                    Long movimentacaoSqlLiteId = dh.insertMovimentacao(dataTransferencia, obraOrigemId, obraDestinoId, responsavelOrigemId, responsavelDestinoId);

                    for (String bemString : listaDeEstoqueSelecionados) {

                        String[] bemValue = bemString.split("-");

                        String nomeBem        =  bemValue[0];
                        String codigo         =  bemValue[1];
                        String qtdSelecionada =  bemValue[2];

                        // salvar os bens na tabela bens alocação
                        dh.insertBemMovimentacao(Integer.parseInt(codigo), movimentacaoSqlLiteId.intValue(), Integer.parseInt(qtdSelecionada));
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
            sms.append("ID Transacao: "+movimentacaoId);
            sms.append(System.getProperty("line.separator"));
            sms.append("Responsável Obra Origem: "+entreguePara);
            sms.append(System.getProperty("line.separator"));
            sms.append("Responsável Obra Destino: "+recebidoPor);
            sms.append(System.getProperty("line.separator"));
            sms.append("Data: "+format2.format(new Date()));
            sms.append(System.getProperty("line.separator"));
            sms.append("Estoque: ");
            sms.append(System.getProperty("line.separator"));

            for (String bemString : listaDeEstoqueSelecionados) {

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

        Intent intent = new Intent(EstoqueMovimentacaoResumoAct.this,MenuAct.class);
        startActivity(intent);
    }


}
