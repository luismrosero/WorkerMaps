package com.luisrosero.workermaps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.luisrosero.workermaps.entidades.Trabajador;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

public class BDLocal {

    private Context context;

    public BDLocal(Context context) {
        this.context = context;
    }

    public String objetToString (Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public Trabajador stringToTrabajador (String s){

        Gson gson = new Gson();
        Type tipo = new TypeToken<Trabajador>(){}.getType();
        Trabajador usuario = gson.fromJson(s, tipo);
        return usuario;
    }

    public void setTrabajador(Trabajador vehiculo) {
        String urisString = objetToString(vehiculo);
        SharedPreferences sharedPref = context.getSharedPreferences("datos", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("trabajador", urisString);
        editor.apply();

    }

    public Trabajador getTrabajador() {

        SharedPreferences sharedPref = context.getSharedPreferences("datos", MODE_PRIVATE);
        String traString = sharedPref.getString("trabajador", null);

        Trabajador trabajador = stringToTrabajador(traString);
        return trabajador;


    }


}
