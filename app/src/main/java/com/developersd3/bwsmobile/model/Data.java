package com.developersd3.bwsmobile.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.*;
/**
 * Created by fred on 20/10/16.
 */

public class Data {


    public static void insertUsuario(SharedPreferences mPrefs, UserApp usuario){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        prefsEditor.putString("usuario", json);
        prefsEditor.commit();

    }

    public static UserApp getUsuario(SharedPreferences mPrefs){

        UserApp user = null;

        Gson gson = new Gson();
        String json = mPrefs.getString("usuario", "0");

        if(json != null && !json.equals("0"))
        user = gson.fromJson(json, UserApp.class);

        return user;
    }

    public static void limpaSessao(SharedPreferences mPrefs){

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove("usuario");
        editor.apply();

    }

}
