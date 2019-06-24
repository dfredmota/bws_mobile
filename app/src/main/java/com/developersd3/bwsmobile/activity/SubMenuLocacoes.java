package com.developersd3.bwsmobile.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.developersd3.bwsmobile.R;

public class SubMenuLocacoes extends AppCompatActivity {

    private Button locacaoBtn;

    private Button devolucaoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu_locacoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);

        locacaoBtn = (Button) findViewById(R.id.locacaoBtn);

        locacaoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SubMenuLocacoes.this, LocacaoAct.class);
                startActivity(intent);
            }

        });

        devolucaoBtn  = (Button) findViewById(R.id.devolucaoBtn);

        devolucaoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SubMenuLocacoes.this, null);
                startActivity(intent);
            }

        });




    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SubMenuLocacoes.this,MenuAct.class);
        startActivity(intent);
    }

}
