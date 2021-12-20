package com.luisrosero.workermaps;

import android.app.ActivityManager;
import android.content.Context;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luisrosero.workermaps.entidades.Trabajador;

public class ManejoServicio {

    public ManejoServicio() {
    }

    public Boolean isServicioActivo(FloatingActionButton fab){


        if (isMyServiceRunning(ServicioEscucha.class, fab)){
            fab.setImageResource(R.drawable.ic_baseline_check_24);
            return true;
        }else{
            fab.setImageResource(R.drawable.ic_baseline_clear_24);
            return false;



        }

    }


    public Boolean isServicioActivo(Context context){


        if (isMyServiceRunning(ServicioEscucha.class, context)){

            return true;
        }else{

            return false;



        }

    }




    private boolean isMyServiceRunning(Class<?> serviceClass, FloatingActionButton fab) {
        ActivityManager manager = (ActivityManager) fab.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void retirarVheiculo(Trabajador vehiculo) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("activos").child(vehiculo.getId()).removeValue();

    }
}