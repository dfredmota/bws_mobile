package com.developersd3.bwsmobile.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.developersd3.bwsmobile.R;
import com.developersd3.bwsmobile.dao.DataManipulator;
import com.developersd3.bwsmobile.model.Bem;
import com.developersd3.bwsmobile.model.UserApp;
import java.util.ArrayList;
import java.util.List;

public class FerramentaEntregaAct extends AppCompatActivity {

    MyCustomAdapter dataAdapter = null;
    public static DataManipulator dh;
    Context context;
    ArrayList<String> idsBensParam = new ArrayList<String>();
    Intent intent;
    TextView txtSelecione;
    Button myButton;
    String tipoTransferenciaParam;
    String tipoOperacao;

    Integer entregadorId;
    String  entreguePor;
    String  telefoneEntregador;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_checkboxes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        setTitle("ITENS DA TRANSFERÊNCIA");

        intent = new Intent(FerramentaEntregaAct.this, FerramentaEntregaResumoAct.class);

        context = getBaseContext();

        this.dh = new DataManipulator(this);

        UserApp userApp = dh.findUserLogado();

        txtSelecione = (TextView) findViewById(R.id.selecione_txt);

        myButton = (Button) findViewById(R.id.findSelected);

        tipoTransferenciaParam = getIntent().getExtras().getString("tipoTransferencia");

        tipoOperacao = getIntent().getExtras().getString("tipoOperacao");

        entregadorId = getIntent().getExtras().getInt("entregadorId");

        entreguePor  = getIntent().getExtras().getString("entreguePor");

        telefoneEntregador = getIntent().getExtras().getString("telefoneEntregador");


        if(userApp.getColaborador() != null) {

            //Generate list View from ArrayList
            displayListView(userApp.getColaborador().toString());
            checkButtonClick();
        }

    }

    private void displayListView(String colaborador) {

        String tipoConsulta = "";

        if(tipoTransferenciaParam.equalsIgnoreCase("FERRAMENTAS"))
            tipoConsulta = "1";
        if(tipoTransferenciaParam.equalsIgnoreCase("PATRIMÔNIO"))
            tipoConsulta = "2";
        if(tipoTransferenciaParam.equalsIgnoreCase("EPI"))
            tipoConsulta = "3";
        if(tipoTransferenciaParam.equalsIgnoreCase("ESTOQUE"))
            tipoConsulta = "4";


        List<Bem> bemList = dh.selectAllBensByColaboradorAndTipo(colaborador, tipoConsulta);

        if(bemList == null || bemList.isEmpty()){

                AlertDialog.Builder builder = new AlertDialog.Builder(FerramentaEntregaAct.this);

                builder.setMessage("NÃO HÁ ITENS NA SUA CARGA DE "+tipoTransferenciaParam)
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

            txtSelecione.setVisibility(View.VISIBLE);

            myButton.setVisibility(View.VISIBLE);

            dataAdapter = new MyCustomAdapter(this, R.layout.country_info, bemList);

            ListView listView = (ListView) findViewById(R.id.listView1);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);

            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // When clicked, show a toast with the TextView text
                    Bem bem = (Bem) parent.getItemAtPosition(position);
                }
            });
        }

    }

    private void navToHome(){

        Intent i = new Intent(this, HomeAct.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);

    }

    private class ViewHolder {

        TextView code;

        CheckBox name;
    }

    private class MyCustomAdapter extends ArrayAdapter<Bem> {

        private ArrayList<Bem> bemList;

        public MyCustomAdapter(Context context, int textViewResourceId,List<Bem> bemList) {

            super(context, textViewResourceId, bemList);

            this.bemList = new ArrayList<Bem>();

            this.bemList.addAll(bemList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder = new ViewHolder();

            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                convertView = vi.inflate(R.layout.country_info, null);

                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {

                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;

                        Bem bem = (Bem) cb.getTag();

                        bem.setSelected(cb.isChecked());

                        if(cb.isChecked()) {
                            show(bem.getInsumo(),bem.getCodigo(), bem.getQuantidade(),holder,position);
                        }
                        else{
                            if(idsBensParam.contains(bem.getInsumo()+"-"+bem.getCodigo()+"-"+bem.getQuantidade())){
                                idsBensParam.remove(bem.getInsumo()+"-"+bem.getCodigo()+"-"+bem.getQuantidade());
                            }
                        }
                    }


                });
            }


            Bem bem = bemList.get(position);
            holder.name.setText(bem.getInsumo() + "  - Qtd Disponivel: " + bem.getQuantidade());
            holder.name.setChecked(bem.isSelected());
            holder.name.setTag(bem);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // mandar as ferramentas selecionadas pra proxima tela

                StringBuffer responseText = new StringBuffer();
                responseText.append("As Seguintes "+tipoTransferenciaParam+" foram selecionadas:\n");

                ArrayList<Bem> bemList = dataAdapter.bemList;

                List<Bem> listaSelecionados = new ArrayList<Bem>();

                for(int i=0;i<bemList.size();i++){
                    Bem bem = bemList.get(i);
                    if(bem.isSelected()){

                        listaSelecionados.add(bem);
                        responseText.append("\n" + bem.getInsumo() + "Qtd Carga: "+bem.getQuantidade());

                    }
                }

                // recuperar a lista de selecionados e enviar para a tela de resumo
                intent.putStringArrayListExtra("itensSelecionados", idsBensParam);

                intent.putExtra("tipoOperacao",tipoOperacao);

                intent.putExtra("entregadorId",entregadorId);
                intent.putExtra("entreguePor",entreguePor);
                intent.putExtra("telefoneEntregador",telefoneEntregador);

                //Start details activity
                startActivity(intent);

                Toast.makeText(getApplicationContext(),responseText, Toast.LENGTH_LONG).show();
            }


        });

    }

    public void show(final String nomeBem,final Integer codigoBem,Integer qtdDisponivel,final ViewHolder holder,final int position)
    {

        final Dialog d = new Dialog(FerramentaEntregaAct.this);

        d.setTitle("Selecione a quantidade:");

        d.setContentView(R.layout.dialog);

        Button confirmar = (Button) d.findViewById(R.id.button1);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);

        np.setMaxValue(qtdDisponivel); // max value 100

        np.setMinValue(1);   // min value 0

        np.setWrapSelectorWheel(false);

        confirmar.setOnClickListener(new OnClickListener()

        {
            @Override
            public void onClick(View v) {
                idsBensParam.add(nomeBem + "-" + codigoBem + "-" + np.getValue());
                Bem bem = dataAdapter.bemList.get(position);
                holder.name.setText(bem.getInsumo() + "  - Qtd Carga: " + bem.getQuantidade() + " | Selecionado: " + np.getValue());
                holder.name.setChecked(bem.isSelected());
                holder.name.setTag(bem);

                d.dismiss();
            }
        });

        d.show();

    }

    @Override
    public void onBackPressed()
    {
        navToHome();
    }



}

