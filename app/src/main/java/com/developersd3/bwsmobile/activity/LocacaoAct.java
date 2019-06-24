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
import com.developersd3.bwsmobile.model.MaterialLocado;
import com.developersd3.bwsmobile.model.UserApp;

import java.util.ArrayList;
import java.util.List;

public class LocacaoAct extends AppCompatActivity {

    MyCustomAdapter dataAdapter = null;
    public static DataManipulator dh;
    Context context;
    ArrayList<String> idsMateriaisLocadosParam = new ArrayList<String>();
    Intent intent;
    TextView semCargaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_checkboxes_locacao);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);

        intent = new Intent(LocacaoAct.this, LocacaoAct.class);

        context = getBaseContext();

        this.dh = new DataManipulator(this);

        UserApp userApp = dh.findUserLogado();

        semCargaText = (TextView) findViewById(R.id.text_sem_carga);

        //Generate list View from ArrayList
        displayListView();

        checkButtonClick();

    }

    private void displayListView() {

        List<MaterialLocado> materialLocados = dh.findAllMaterialLocado();


        if(materialLocados == null || materialLocados.isEmpty()){

                semCargaText.setVisibility(View.VISIBLE);
                Button myButton = (Button) findViewById(R.id.findSelectedMaterialLocacao);
                myButton.setVisibility(View.INVISIBLE);

        }

        dataAdapter = new MyCustomAdapter(this,R.layout.country_info, materialLocados);

        ListView listView = (ListView) findViewById(R.id.listView1MaterialLocacao);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                MaterialLocado materialLocado = (MaterialLocado) parent.getItemAtPosition(position);
            }
        });

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelectedMaterialLocacao);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // mandar as ferramentas selecionadas pra proxima tela

                StringBuffer responseText = new StringBuffer();
                responseText.append("Os Seguintes Materiais foram selecionados:\n");

                ArrayList<MaterialLocado> bemList = dataAdapter.materiaisLocadosList;

                List<MaterialLocado> listaSelecionados = new ArrayList<MaterialLocado>();

                for (int i = 0; i < bemList.size(); i++) {
                    MaterialLocado material = bemList.get(i);
                    if (material.isSelected()) {

                        listaSelecionados.add(material);
                        responseText.append("\n" + material.getDescricao() + "Qtd Disponivel: " + material.getQtd());

                    }
                }

                // recuperar a lista de selecionados e enviar para a tela de resumo
                intent.putStringArrayListExtra("materiaisSelecionados", idsMateriaisLocadosParam);

                //Start details activity
                startActivity(intent);

                Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG).show();
            }


        });

    }

    private class MyCustomAdapter extends ArrayAdapter<MaterialLocado> {

        private ArrayList<MaterialLocado> materiaisLocadosList;

        public MyCustomAdapter(Context context, int textViewResourceId,List<MaterialLocado> materiaisLocadosList) {

            super(context, textViewResourceId, materiaisLocadosList);

            this.materiaisLocadosList = new ArrayList<MaterialLocado>();

            this.materiaisLocadosList.addAll(materiaisLocadosList);
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

                        MaterialLocado material = (MaterialLocado) cb.getTag();

                        material.setSelected(cb.isChecked());

                        if(cb.isChecked()) {
                            show(material.getDescricao(),material.getId(), material.getQtd());
                        }
                        else{
                            if(idsMateriaisLocadosParam.contains(material.getDescricao()+"-"+material.getId()+"-"+material.getQtd())){
                                idsMateriaisLocadosParam.remove(material.getDescricao()+"-"+material.getId()+"-"+material.getQtd());
                            }
                        }
                    }


                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            MaterialLocado materialLocado = materiaisLocadosList.get(position);
            holder.name.setText(materialLocado.getDescricao() + "  - Qtd Disponivel: " + materialLocado.getQtd());
            holder.name.setChecked(materialLocado.isSelected());
            holder.name.setTag(materialLocado);

            return convertView;

        }

    }

    public void show(final String nomeMaterial,final Long codigoMaterial,Long qtdDisponivel)
    {

        final Dialog d = new Dialog(LocacaoAct.this);

        d.setTitle("Selecione a quantidade alocada:");

        d.setContentView(R.layout.dialog);

        Button confirmar = (Button) d.findViewById(R.id.button1);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);

        np.setMaxValue(qtdDisponivel.intValue()); // max value 100

        np.setMinValue(0);   // min value 0

        np.setWrapSelectorWheel(false);

        confirmar.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                idsMateriaisLocadosParam.add(nomeMaterial + "-" + codigoMaterial + "-" + np.getValue());
                d.dismiss();
            }
        });

        d.show();


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(LocacaoAct.this,MenuAct.class);
        startActivity(intent);
    }

}
