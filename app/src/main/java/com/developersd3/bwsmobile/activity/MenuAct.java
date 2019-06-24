package com.developersd3.bwsmobile.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.developersd3.bwsmobile.R;

public class MenuAct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Button almoxarifadoBtn;

    private Button cadastrosBtn;

    private Button sincronizarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable d=getResources().getDrawable(R.drawable.cabecalho);
        getSupportActionBar().setBackgroundDrawable(d);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            boolean almoxarife = bundle.getBoolean("almoxarife");

        }

//        almoxarifadoBtn = (Button)findViewById(R.id.almoxarifadoBtn);
//
//        almoxarifadoBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v)
//            {
//
//                Intent intent = new Intent(MenuAct.this,SubMenuAlmoxarifadoAct.class);
//                startActivity(intent);
//            }
//
//        });
//
//        sincronizarBtn =   (Button)findViewById(R.id.sincronizarMenuBtn);
//
//        sincronizarBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v)
//            {
//
//                Intent intent = new Intent(MenuAct.this,SincronizarAct.class);
//                startActivity(intent);
//            }
//
//        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_venda) {


        }  else if (id == R.id.nav_sair) {

            // refatorar
            System.exit(0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
