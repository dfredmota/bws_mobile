package com.developersd3.bwsmobile.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class EstoqueMovimentacaoAct extends AppCompatActivity {

    MyCustomAdapter dataAdapter = null;
    public static DataManipulator dh;
    Context context;
    ArrayList<String> idsBensParam = new ArrayList<String>();
    Intent intent;
    TextView semCargaText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_checkboxes_estoque);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);

        intent = new Intent(EstoqueMovimentacaoAct.this, EstoqueMovimentacaoResumoAct.class);

        context = getBaseContext();

        this.dh = new DataManipulator(this);

        UserApp userApp = dh.findUserLogado();

        semCargaText = (TextView) findViewById(R.id.text_sem_carga);

        if(userApp.getColaborador() != null) {

            //Generate list View from ArrayList
            displayListView(userApp.getColaborador().toString());
            checkButtonClick();
        }else{
            semCargaText.setVisibility(View.VISIBLE);
            Button myButton = (Button) findViewById(R.id.findSelected_estoque);
            myButton.setVisibility(View.INVISIBLE);
        }

    }

    private void displayListView(String colaborador) {

        List<Bem> bemList = dh.selectAllBensByColaboradorAndTipo(colaborador, "4");

        if(bemList == null || bemList.isEmpty()){
            semCargaText.setVisibility(View.VISIBLE);
        }

        dataAdapter = new MyCustomAdapter(this,R.layout.country_info, bemList);

        ListView listView = (ListView) findViewById(R.id.listView1_estoque);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Bem bem = (Bem) parent.getItemAtPosition(position);
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Bem> {

        private ArrayList<Bem> bemList;

        public MyCustomAdapter(Context context, int textViewResourceId,List<Bem> bemList) {

            super(context, textViewResourceId, bemList);

            this.bemList = new ArrayList<Bem>();

            this.bemList.addAll(bemList);
        }

        private class ViewHolder {

            TextView code;

            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);


                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();

                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);

                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {

                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;

                        Bem bem = (Bem) cb.getTag();

                        bem.setSelected(cb.isChecked());

                        if(cb.isChecked()) {
                            show(bem.getInsumo(),bem.getCodigo(), bem.getQtdDisponivel());
                        }
                        else{
                            if(idsBensParam.contains(bem.getInsumo()+"-"+bem.getCodigo()+"-"+bem.getQtdDisponivel())){
                                idsBensParam.remove(bem.getInsumo()+"-"+bem.getCodigo()+"-"+bem.getQtdDisponivel());
                            }
                        }
                    }


                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Bem bem = bemList.get(position);
            holder.name.setText(bem.getInsumo() + "  - Qtd Disponivel: " + bem.getQtdDisponivel());
            holder.name.setChecked(bem.isSelected());
            holder.name.setTag(bem);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected_estoque);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // mandar as ferramentas selecionadas pra proxima tela

                StringBuffer responseText = new StringBuffer();
                responseText.append("os seguintes itens de estoque foram selecionadas:\n");

                ArrayList<Bem> bemList = dataAdapter.bemList;

                List<Bem> listaSelecionados = new ArrayList<Bem>();

                for(int i=0;i<bemList.size();i++){
                    Bem bem = bemList.get(i);
                    if(bem.isSelected()){

                        listaSelecionados.add(bem);
                        responseText.append("\n" + bem.getInsumo() + "Qtd Disponivel: "+bem.getQtdDisponivel());

                    }
                }

                // recuperar a lista de selecionados e enviar para a tela de resumo
                intent.putStringArrayListExtra("estoqueSelecionados", idsBensParam);

                //Start details activity
                startActivity(intent);

                Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
            }


        });

    }

    public void show(final String nomeBem,final Integer codigoEstoque,Integer qtdDisponivel)
    {

        final Dialog d = new Dialog(EstoqueMovimentacaoAct.this);

        d.setTitle("Selecione a quantidade alocada:");

        d.setContentView(R.layout.dialog);

        Button confirmar = (Button) d.findViewById(R.id.button1);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);

        np.setMaxValue(qtdDisponivel); // max value 100

        np.setMinValue(0);   // min value 0

        np.setWrapSelectorWheel(false);

        confirmar.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                idsBensParam.add(nomeBem + "-" + codigoEstoque + "-" + np.getValue());
                d.dismiss();
            }
        });

        d.show();

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(EstoqueMovimentacaoAct.this,MenuAct.class);
        startActivity(intent);
    }

}