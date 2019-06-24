package com.developersd3.bwsmobile.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.developersd3.bwsmobile.R;

public class SubMenuAlmoxarifadoAct extends AppCompatActivity {


    private Button ferramentaBtn;
    private Button patrimonioBtn;
    private Button epiBtn;
    private Button estoqueBtn;
    private Button locacaoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu_almoxarifado);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);

        ferramentaBtn = (Button) findViewById(R.id.ferramentasBtn);

        ferramentaBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SubMenuAlmoxarifadoAct.this, TipoTransferencia.class);
                startActivity(intent);
            }

        });

    }

}
